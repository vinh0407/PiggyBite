package com.money.app.service

import com.money.app.model.BankAccount
import com.money.app.model.Transaction
import com.money.app.util.Formatters
import java.util.Calendar
import java.util.Locale

data class CashflowInsight(
    val level: InsightLevel, // SAFE, WARNING, DANGER
    val title: String,
    val message: String,
    val runwayDays: Int,
    val averageDailySpend: Long,
    val isAnomaly: Boolean
)

enum class InsightLevel { SAFE, WARNING, DANGER }

data class RecurringItem(
    val title: String,
    val estimatedAmount: Long,
    val frequency: String,
    val category: String
)

data class BankRecommendation(
    val recommendedAccount: BankAccount?,
    val isSufficient: Boolean,
    val reason: String,
    val warning: String? = null
)

data class AIAnswer(
    val question: String,
    val mainResponse: String,
    val highlightNumber: String? = null,
    val subNote: String? = null
)

object SmartFinanceAdvisor {

    /**
     * Phân tích dòng tiền & phát hiện chi tiêu bất thường.
     */
    fun analyzeCashflow(
        totalBalance: Long,
        todayExpense: Long,
        transactions: List<Transaction>
    ): CashflowInsight {
        val now = System.currentTimeMillis()
        val sevenDaysAgo = now - 7L * 24 * 60 * 60 * 1000

        val past7DaysExpenses = transactions.filter {
            !it.isIncome && it.timestamp in sevenDaysAgo..now
        }

        // Tính chi tiêu trung bình ngày (dựa trên 7 ngày gần nhất)
        val total7Days = past7DaysExpenses.sumOf { it.amount }
        val avgDailySpend = if (total7Days > 0) (total7Days / 7).coerceAtLeast(10_000L) else 50_000L

        // Số ngày ví có thể duy trì (Runway)
        val runwayDays = if (avgDailySpend > 0) {
            (totalBalance / avgDailySpend).toInt().coerceAtMost(365)
        } else {
            999
        }

        // Phát hiện chi tiêu bất thường hôm nay (vượt 150% mức trung bình và >= 100k)
        val isAnomaly = todayExpense > (avgDailySpend * 1.5) && todayExpense >= 100_000L

        return when {
            totalBalance <= 0L -> {
                CashflowInsight(
                    level = InsightLevel.DANGER,
                    title = "Ví đang hết tiền!",
                    message = "Tổng số dư hiện tại là 0đ. Hãy chú ý các khoản cần chi tiêu sắp tới.",
                    runwayDays = 0,
                    averageDailySpend = avgDailySpend,
                    isAnomaly = isAnomaly
                )
            }
            runwayDays < 7 -> {
                CashflowInsight(
                    level = InsightLevel.DANGER,
                    title = "Cảnh báo cạn ví trong $runwayDays ngày",
                    message = "Với tốc độ tiêu ${Formatters.formatMoney(avgDailySpend)}/ngày, quỹ tiền hiện tại có thể hết trong $runwayDays ngày tới.",
                    runwayDays = runwayDays,
                    averageDailySpend = avgDailySpend,
                    isAnomaly = isAnomaly
                )
            }
            isAnomaly -> {
                val percentOver = if (avgDailySpend > 0) ((todayExpense - avgDailySpend) * 100 / avgDailySpend) else 100
                CashflowInsight(
                    level = InsightLevel.WARNING,
                    title = "Chi tiêu hôm nay tăng đột biến",
                    message = "Hôm nay bạn đã chi ${Formatters.formatMoney(todayExpense)}, cao hơn $percentOver% so với mức trung bình hàng ngày.",
                    runwayDays = runwayDays,
                    averageDailySpend = avgDailySpend,
                    isAnomaly = true
                )
            }
            runwayDays in 7..20 -> {
                CashflowInsight(
                    level = InsightLevel.WARNING,
                    title = "Duy trì được ~$runwayDays ngày",
                    message = "Tốc độ chi tiêu trung bình ${Formatters.formatMoney(avgDailySpend)}/ngày. Hãy phân bổ hợp lý tới ngày nhận lương tiếp theo.",
                    runwayDays = runwayDays,
                    averageDailySpend = avgDailySpend,
                    isAnomaly = false
                )
            }
            else -> {
                CashflowInsight(
                    level = InsightLevel.SAFE,
                    title = "Dòng tiền an toàn (~$runwayDays ngày)",
                    message = "Tốc độ chi tiêu ổn định (${Formatters.formatMoney(avgDailySpend)}/ngày). Bạn duy trì tài chính rất tốt!",
                    runwayDays = runwayDays,
                    averageDailySpend = avgDailySpend,
                    isAnomaly = false
                )
            }
        }
    }

    /**
     * AI Gợi ý ngân hàng tối ưu nhất khi thanh toán số tiền [amount].
     */
    fun recommendBankForPayment(
        amount: Long,
        accounts: List<BankAccount>
    ): BankRecommendation {
        if (accounts.isEmpty()) {
            return BankRecommendation(
                recommendedAccount = null,
                isSufficient = false,
                reason = "Bạn chưa thêm tài khoản ngân hàng nào.",
                warning = "Vui lòng thêm tài khoản để chuyển tiền."
            )
        }

        if (amount <= 0L) {
            val best = accounts.maxByOrNull { it.balance }
            return BankRecommendation(
                recommendedAccount = best,
                isSufficient = true,
                reason = "Chọn ${best?.bankName} (số dư lớn nhất: ${Formatters.formatMoney(best?.balance ?: 0L)})."
            )
        }

        // Các tài khoản đủ tiền
        val sufficientAccounts = accounts.filter { it.balance >= amount }

        if (sufficientAccounts.isEmpty()) {
            val maxAcc = accounts.maxByOrNull { it.balance }
            return BankRecommendation(
                recommendedAccount = maxAcc,
                isSufficient = false,
                reason = "Không có tài khoản nào đủ số dư ${Formatters.formatMoney(amount)}.",
                warning = "Tài khoản nhiều tiền nhất (${maxAcc?.bankName}) chỉ còn ${Formatters.formatMoney(maxAcc?.balance ?: 0L)}."
            )
        }

        // Chọn tài khoản có số dư dồi dào nhất và còn dư đệm an toàn cao nhất
        val best = sufficientAccounts.maxByOrNull { it.balance }!!
        val remaining = best.balance - amount

        return BankRecommendation(
            recommendedAccount = best,
            isSufficient = true,
            reason = "Gợi ý dùng ${best.bankName} (Số dư ${Formatters.formatMoney(best.balance)}). Sau khi chuyển vẫn còn dư ${Formatters.formatMoney(remaining)} an toàn.",
            warning = null
        )
    }

    /**
     * AI Phát hiện các khoản chi cố định / đăng ký định kỳ hàng tháng.
     */
    fun detectRecurringExpenses(transactions: List<Transaction>): List<RecurringItem> {
        val expenses = transactions.filter { !it.isIncome }
        val recurringKeywords = mapOf(
            "tien nha" to ("Tiền thuê nhà / phòng trọ" to "Hóa đơn & Tiện ích"),
            "phong tro" to ("Tiền thuê nhà / phòng trọ" to "Hóa đơn & Tiện ích"),
            "tien dien" to ("Hóa đơn tiền điện EVN" to "Hóa đơn & Tiện ích"),
            "tien nuoc" to ("Hóa đơn tiền nước sinh hoạt" to "Hóa đơn & Tiện ích"),
            "internet" to ("Cước Internet / WiFi" to "Hóa đơn & Tiện ích"),
            "wifi" to ("Cước Internet / WiFi" to "Hóa đơn & Tiện ích"),
            "netflix" to ("Gói phim Netflix Premium" to "Giải trí & Sở thích"),
            "spotify" to ("Gói nghe nhạc Spotify" to "Giải trí & Sở thích"),
            "youtube" to ("YouTube Premium" to "Giải trí & Sở thích"),
            "icloud" to ("Dung lượng Apple iCloud" to "Hóa đơn & Tiện ích"),
            "gym" to ("Hội viên phòng tập Gym" to "Sức khỏe & Y tế"),
            "gui xe" to ("Phí gửi xe tháng" to "Di chuyển & Xăng xe"),
            "bao hiem" to ("Phí bảo hiểm định kỳ" to "Hóa đơn & Tiện ích"),
            "hoc phi" to ("Học phí định kỳ" to "Hóa đơn & Tiện ích")
        )

        val detected = mutableListOf<RecurringItem>()
        val seenTitles = mutableSetOf<String>()

        for (tx in expenses) {
            val norm = IncomeNaiveBayes.normalize(tx.note)
            for ((kw, info) in recurringKeywords) {
                if (norm.contains(kw) && !seenTitles.contains(info.first)) {
                    seenTitles.add(info.first)
                    detected.add(
                        RecurringItem(
                            title = info.first,
                            estimatedAmount = tx.amount,
                            frequency = "Hàng tháng",
                            category = info.second
                        )
                    )
                    break
                }
            }
        }

        return detected
    }

    /**
     * Trợ lý Hỏi - Đáp thông minh (Piggy AI Chat).
     */
    fun answerQuestion(
        query: String,
        accounts: List<BankAccount>,
        transactions: List<Transaction>
    ): AIAnswer {
        val q = IncomeNaiveBayes.normalize(query.lowercase(Locale.ROOT))
        val totalBalance = accounts.sumOf { it.balance }

        val calendar = Calendar.getInstance()
        val todayStart = calendar.apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        val monthStart = calendar.apply {
            set(Calendar.DAY_OF_MONTH, 1)
        }.timeInMillis

        return when {
            // Hôm nay đã tiêu bao nhiêu?
            q.contains("hom nay") && (q.contains("tieu") || q.contains("chi") || q.contains("het")) -> {
                val todayExpenses = transactions.filter { !it.isIncome && it.timestamp >= todayStart }
                val total = todayExpenses.sumOf { it.amount }
                if (total == 0L) {
                    AIAnswer(
                        question = query,
                        mainResponse = "Hôm nay bạn chưa phát sinh khoản chi tiêu nào! Rất tiết kiệm.",
                        highlightNumber = "0 đ"
                    )
                } else {
                    val count = todayExpenses.size
                    val top = todayExpenses.maxByOrNull { it.amount }
                    AIAnswer(
                        question = query,
                        mainResponse = "Hôm nay bạn đã chi tổng cộng $count giao dịch.",
                        highlightNumber = Formatters.formatMoney(total),
                        subNote = "Khoản chi lớn nhất: ${top?.note} (${Formatters.formatMoney(top?.amount ?: 0L)})"
                    )
                }
            }

            // Ăn uống tháng này bao nhiêu?
            q.contains("an uong") || q.contains("cafe") || q.contains("ca phe") || q.contains("tra sua") -> {
                val foodExpenses = transactions.filter {
                    !it.isIncome && it.timestamp >= monthStart &&
                        (it.category.contains("Ăn uống") || IncomeNaiveBayes.normalize(it.note).let { n ->
                            n.contains("cafe") || n.contains("an") || n.contains("com") || n.contains("tra sua")
                        })
                }
                val total = foodExpenses.sumOf { it.amount }
                AIAnswer(
                    question = query,
                    mainResponse = "Tháng này bạn đã dành tổng cộng cho Ăn uống & Cà phê:",
                    highlightNumber = Formatters.formatMoney(total),
                    subNote = "Gồm ${foodExpenses.size} lần ăn uống và thanh toán F&B."
                )
            }

            // Ngân hàng nào nhiều tiền nhất?
            q.contains("nhieu tien nhat") || q.contains("lon nhat") || q.contains("con nhieu tien") -> {
                val topBank = accounts.maxByOrNull { it.balance }
                if (topBank == null) {
                    AIAnswer(query, "Bạn chưa thêm tài khoản ngân hàng nào vào ví.")
                } else {
                    AIAnswer(
                        question = query,
                        mainResponse = "Tài khoản có số dư lớn nhất hiện tại là ${topBank.bankName}:",
                        highlightNumber = Formatters.formatMoney(topBank.balance),
                        subNote = "Chiếm ${if (totalBalance > 0) topBank.balance * 100 / totalBalance else 0}% tổng tài sản của bạn."
                    )
                }
            }

            // Thu nhập tháng này?
            q.contains("thu nhap") || q.contains("tien ve") || q.contains("tong thu") || q.contains("luong") -> {
                val monthIncomes = transactions.filter { it.isIncome && it.timestamp >= monthStart }
                val total = monthIncomes.sumOf { it.amount }
                AIAnswer(
                    question = query,
                    mainResponse = "Tổng thu nhập ghi nhận trong tháng này là:",
                    highlightNumber = Formatters.formatMoney(total),
                    subNote = "Đã phân loại thành công ${monthIncomes.size} nguồn thu từ các ngân hàng."
                )
            }

            // Dự báo dòng tiền / Khi nào hết tiền?
            q.contains("du bao") || q.contains("dong tien") || q.contains("het tien") || q.contains("duoc bao lau") -> {
                val insight = analyzeCashflow(
                    totalBalance,
                    transactions.filter { !it.isIncome && it.timestamp >= todayStart }.sumOf { it.amount },
                    transactions
                )
                AIAnswer(
                    question = query,
                    mainResponse = insight.message,
                    highlightNumber = "~${insight.runwayDays} ngày",
                    subNote = "Tốc độ tiêu trung bình: ${Formatters.formatMoney(insight.averageDailySpend)}/ngày."
                )
            }

            // Khoản chi cố định / subscription?
            q.contains("co dinh") || q.contains("dinh ky") || q.contains("dang ky") || q.contains("netflix") -> {
                val items = detectRecurringExpenses(transactions)
                if (items.isEmpty()) {
                    AIAnswer(
                        question = query,
                        mainResponse = "Chưa phát hiện khoản chi định kỳ nào trong lịch sử giao dịch gần đây."
                    )
                } else {
                    val total = items.sumOf { it.estimatedAmount }
                    AIAnswer(
                        question = query,
                        mainResponse = "AI phát hiện ${items.size} khoản chi cố định hàng tháng:",
                        highlightNumber = Formatters.formatMoney(total) + "/tháng",
                        subNote = items.joinToString(", ") { "${it.title}: ${Formatters.formatMoney(it.estimatedAmount)}" }
                    )
                }
            }

            // Mặc định: Tổng quan dòng tiền
            else -> {
                val todayExpense = transactions.filter { !it.isIncome && it.timestamp >= todayStart }.sumOf { it.amount }
                val insight = analyzeCashflow(totalBalance, todayExpense, transactions)
                AIAnswer(
                    question = query,
                    mainResponse = "Tổng số dư: ${Formatters.formatMoney(totalBalance)}. ${insight.message}",
                    highlightNumber = Formatters.formatMoney(totalBalance),
                    subNote = "Gợi ý: bạn có thể hỏi 'Hôm nay tiêu bao nhiêu?', 'Tiền ăn tháng này', 'Ngân hàng nhiều tiền nhất'..."
                )
            }
        }
    }
}
