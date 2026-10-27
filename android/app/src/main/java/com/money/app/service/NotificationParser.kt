package com.money.app.service

import com.money.app.model.Bank
import com.money.app.model.BankDirectory

data class ParsedNotification(
    val bank: Bank,
    val amount: Long,
    val isIncome: Boolean,
    val accountNumber: String,
    val note: String,
    val rawText: String
)

object NotificationParser {

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
