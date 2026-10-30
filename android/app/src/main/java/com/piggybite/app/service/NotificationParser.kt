package com.piggybite.app.service

import com.piggybite.app.model.Bank
import com.piggybite.app.model.BankDirectory
import com.piggybite.app.model.NotificationCategory
import com.piggybite.app.model.ReceivedNotification
import com.piggybite.app.util.Formatters

data class ParsedNotification(
    val bank: Bank,
    val amount: Long,
    val isIncome: Boolean,
    val accountNumber: String,
    val note: String,
    val rawText: String
)

object NotificationParser {

    private val promoKeywords = listOf(
        "khuyen mai", "khuyến mại", "khuyến mãi", "voucher", "giam gia", "giảm giá",
        "uu dai", "ưu đãi", "hoan tien", "hoàn tiền", "vay", "vay tieu dung", "vay tiêu dùng",
        "mo the", "mở thẻ", "chuc mung", "chúc mừng", "tri an", "tri ân", "bao hiem", "bảo hiểm",
        "lai suat", "lãi suất", "qua tang", "quà tặng", "sinh nhat", "sinh nhật",
        "flash sale", "deal", "cashback", "tiet kiem", "tiết kiệm", "tich diem", "tích điểm",
        "mua ve", "mua vé", "tang ban", "tặng bạn", "san sale", "săn sale", "ma giam gia", "mã giảm giá",
        "diem thuong", "điểm thưởng", "qua tet", "quà tết", "co hoi trung", "cơ hội trúng", "danh rieng cho ban",
        "dành riêng cho bạn", "nhan ngay", "nhận ngay", "mo tai khoan", "mở tài khoản"
    )

    fun classifyNotification(rawText: String): ReceivedNotification {
        val bank = BankDirectory.findBankByKeyword(rawText) ?: BankDirectory.ALL_BANKS[0]
        val lower = rawText.lowercase()
        val parsed = parse(rawText)

        // If financial amount successfully extracted and has transaction keywords
        if (parsed != null && parsed.amount > 0) {
            val kind = if (parsed.isIncome) NotificationCategory.INCOME else NotificationCategory.EXPENSE
            val summary = if (parsed.isIncome) {
                "Nhận +${Formatters.formatMoney(parsed.amount)} vào ${bank.shortName}"
            } else {
                "Chuyển -${Formatters.formatMoney(parsed.amount)} từ ${bank.shortName}"
            }
            return ReceivedNotification(
                bankName = bank.shortName,
                bankId = bank.id,
                category = kind,
                amount = parsed.amount,
                rawText = rawText,
                summary = summary
            )
        }

        // If not a transaction or contains promo keywords
        val isExplicitPromo = promoKeywords.any { lower.contains(it) }
        val promoSummary = if (isExplicitPromo) {
            "Quảng cáo & Ưu đãi từ ${bank.shortName} (Tự động bỏ qua)"
        } else {
            "Thông báo chung từ ${bank.shortName} (Không có biến động số dư)"
        }

        return ReceivedNotification(
            bankName = bank.shortName,
            bankId = bank.id,
            category = NotificationCategory.PROMO_OR_ADS,
            amount = 0L,
            rawText = rawText,
            summary = promoSummary
        )
    }

    fun parse(rawText: String): ParsedNotification? {
        if (rawText.isBlank()) return null

        try {
            // 1. Match Bank
            val bank = BankDirectory.findBankByKeyword(rawText) ?: return null

            // 2. Determine Income vs Expense
            var isIncome = true
            val lower = rawText.lowercase()

            val expenseKeywords = listOf(
                "thanh toan", "thanh toán", "rut tien", "rút tiền", "chuyen di", "chuyển đi",
                "tru tien", "trừ tiền", "phi duy tri", "phí duy trì", "tra no", "trả nợ",
                "mua hang", "mua hàng", "so du giam", "số dư giảm"
            )
            val incomeKeywords = listOf(
                "nhan tien", "nhận tiền", "nhan duoc", "nhận được", "cong tien", "cộng tiền",
                "chuyen den", "chuyển đến", "so du tang", "số dư tăng", "nap tien", "nạp tiền", "luong", "lương"
            )

            // Check explicit plus/minus (with optional space)
            val plusMatch = Regex("[+]\\s*([0-9,.]+)").find(rawText)
            val minusMatch = Regex("[-]\\s*([0-9,.]+)").find(rawText)

            var amountVal = 0L

            if (plusMatch != null) {
                isIncome = true
                amountVal = cleanAmount(plusMatch.groupValues[1])
            } else if (minusMatch != null) {
                isIncome = false
                amountVal = cleanAmount(minusMatch.groupValues[1])
            } else {
                // Check keywords
                val hasExpense = expenseKeywords.any { lower.contains(it) }
                val hasIncome = incomeKeywords.any { lower.contains(it) }

                if (hasExpense && !hasIncome) {
                    isIncome = false
                }

                // Extract numeric amount near VND / d / đ
                val amountRegex = Regex("([0-9]{1,3}(?:[.,][0-9]{3})+|[0-9]{4,12})\\s*(?:vnd|vnđ|đ|dong|đồng)", RegexOption.IGNORE_CASE)
                val match = amountRegex.find(rawText)
                if (match != null) {
                    amountVal = cleanAmount(match.groupValues[1])
                }
            }

            if (amountVal <= 0L) {
                return null
            }

            // Account number
            val accRegex = Regex("(?:tk|tai khoan|tài khoản|stk|the|thẻ)\\s*([0-9xX*]+)", RegexOption.IGNORE_CASE)
            val accMatch = accRegex.find(rawText)
            val accNum = accMatch?.groupValues?.getOrNull(1) ?: ""

            // Note
            val noteRegex = Regex("(?:nd|noi dung|nội dung|ly do|lý do|ref|message):?\\s*([^.;\\n]+)", RegexOption.IGNORE_CASE)
            val noteMatch = noteRegex.find(rawText)
            val note = noteMatch?.groupValues?.getOrNull(1)?.trim() ?: if (isIncome) "Nhận tiền từ thông báo ${bank.shortName}" else "Chi tiêu từ thông báo ${bank.shortName}"

            return ParsedNotification(
                bank = bank,
                amount = amountVal,
                isIncome = isIncome,
                accountNumber = accNum,
                note = note,
                rawText = rawText
            )
        } catch (t: Throwable) {
            t.printStackTrace()
            return null
        }
    }

    private fun cleanAmount(raw: String): Long {
        return try {
            val clean = raw.replace(".", "").replace(",", "").trim()
            clean.toLongOrNull() ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}
