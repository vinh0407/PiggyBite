package com.money.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.money.app.model.BankAccount
import com.money.app.model.Transaction
import com.money.app.ui.theme.DarkExpenseText
import com.money.app.ui.theme.DarkIncomeText
import com.money.app.ui.theme.DarkTitleBlue
import com.money.app.ui.theme.ExpenseRed
import com.money.app.ui.theme.IncomeGreen
import com.money.app.ui.theme.LocalIsDarkMode
import com.money.app.ui.theme.SkyBluePrimary
import com.money.app.ui.theme.SkyBlueSoft
import com.money.app.ui.theme.TextMuted
import com.money.app.util.Formatters

@Composable
fun AnalyticsScreen(
    accounts: List<BankAccount>,
    transactions: List<Transaction>
) {
    val isDark = LocalIsDarkMode.current

    val totalExpense = remember(transactions) {
        transactions.filter { !it.isIncome }.sumOf { it.amount }
    }
    val totalIncome = remember(transactions) {
        transactions.filter { it.isIncome }.sumOf { it.amount }
    }
    val categoryBreakdown = remember(transactions) {
        transactions.filter { !it.isIncome }
            .groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }
            .toList()
            .sortedByDescending { it.second }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Tổng hợp & Phân tích chi tiêu",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Báo cáo chi tiết dòng tiền và phân bổ chi tiêu",
                fontSize = 13.sp,
                color = TextMuted
            )
        }

        // Summary Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Total Income
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Text(text = "Tổng thu", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatMoney(totalIncome),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) DarkIncomeText else IncomeGreen
                    )
                }

                // Total Expense
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                        .padding(16.dp)
                ) {
                    Text(text = "Tổng chi", fontSize = 12.sp, color = TextMuted)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = Formatters.formatMoney(totalExpense),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) DarkExpenseText else ExpenseRed
                    )
                }
            }
        }

        // Cash flow net
        item {
            val net = totalIncome - totalExpense
            val netColor = if (net >= 0) {
                if (isDark) DarkTitleBlue else SkyBluePrimary
            } else {
                if (isDark) DarkExpenseText else ExpenseRed
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Text(text = "Dòng tiền ròng tích lũy", fontSize = 12.5.sp, color = TextMuted)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (net >= 0) "+${Formatters.formatMoney(net)}" else "−${Formatters.formatMoney(Math.abs(net))}",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = netColor
                )
            }
        }

        // Category breakdown
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "Cơ cấu chi tiêu theo nhóm",
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                if (categoryBreakdown.isEmpty()) {
                    Text(
                        text = "Chưa có dữ liệu chi tiêu để phân tích.",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                } else {
                    categoryBreakdown.forEach { (cat, amount) ->
                        val percent = if (totalExpense > 0) (amount.toFloat() / totalExpense) else 0f
                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = cat, fontSize = 13.5.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
                                Text(
                                    text = "${Formatters.formatMoney(amount)} (${(percent * 100).toInt()}%)",
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            LinearProgressIndicator(
                                progress = { percent },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp)),
                                color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                trackColor = if (isDark) Color(0xFF1E293B) else SkyBlueSoft
                            )
                        }
                    }
                }
            }
        }

        // Bank Account Balances Distribution
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Text(
                    text = "Phân bổ số dư các ngân hàng & ví",
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(14.dp))

                val totalBal = accounts.sumOf { it.balance }
                if (accounts.isEmpty()) {
                    Text(
                        text = "Chưa có ngân hàng nào được thêm.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                } else {
                    accounts.forEach { acc ->
                    val fraction = if (totalBal > 0) (acc.balance.toFloat() / totalBal) else 0f
                    Column(modifier = Modifier.padding(vertical = 6.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = acc.bankName, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "${Formatters.formatMoney(acc.balance)} (${(fraction * 100).toInt()}%)",
                                fontSize = 13.sp,
                                color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        LinearProgressIndicator(
                            progress = { fraction },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                            trackColor = if (isDark) Color(0xFF1E293B) else SkyBlueSoft
                        )
                    }
                }
            }
        }
    }

        // AI Recurring Expenses & Smart Budgeting
        item {
            val recurring = remember(transactions) {
                com.money.app.service.SmartFinanceAdvisor.detectRecurringExpenses(transactions)
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    .padding(20.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "AI Phân tích Chi phí định kỳ & Ngân sách",
                        fontSize = 16.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (recurring.isNotEmpty()) {
                    Text(
                        text = "KHOẢN CHI CỐ ĐỊNH HÀNG THÁNG:",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextMuted
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    recurring.forEach { item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = "• ${item.title}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                            Text(
                                text = "${Formatters.formatMoney(item.estimatedAmount)}/tháng",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkTitleBlue else SkyBluePrimary
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                }

                // 50/30/20 Budgeting Recommendation
                Text(
                    text = "GỢI Ý PHÂN BỔ NGÂN SÁCH (QUY TẮC 50/30/20):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextMuted
                )
                Spacer(modifier = Modifier.height(6.dp))

                val baseIncome = if (totalIncome > 0) totalIncome else 10_000_000L
                val need50 = (baseIncome * 0.50).toLong()
                val want30 = (baseIncome * 0.30).toLong()
                val save20 = (baseIncome * 0.20).toLong()

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "🏠 50% Thiết yếu (nhà, ăn uống, điện nước)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text = Formatters.formatMoney(need50),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) DarkIncomeText else IncomeGreen
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "🛍️ 30% Linh hoạt (mua sắm, cafe, giải trí)", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text = Formatters.formatMoney(want30),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) DarkTitleBlue else SkyBluePrimary
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "💰 20% Tiết kiệm & Tích lũy", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                        Text(text = Formatters.formatMoney(save20), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
