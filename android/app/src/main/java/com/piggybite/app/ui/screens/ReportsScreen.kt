package com.piggybite.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piggybite.app.model.BankAccount
import com.piggybite.app.model.Transaction
import com.piggybite.app.ui.theme.DarkButtonBg
import com.piggybite.app.ui.theme.DarkButtonBorder
import com.piggybite.app.ui.theme.DarkCardBackground
import com.piggybite.app.ui.theme.DarkCardBorder
import com.piggybite.app.ui.theme.DarkTextMain
import com.piggybite.app.ui.theme.DarkTextMuted
import com.piggybite.app.ui.theme.DarkTextSub
import com.piggybite.app.ui.theme.DarkTitleBlue
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.NepGreenPrimary
import com.piggybite.app.ui.theme.NepGreenSoft
import com.piggybite.app.ui.theme.SkyBluePrimary
import com.piggybite.app.util.Formatters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class ReportTab {
    OVERVIEW,
    CALENDAR
}

@Composable
fun ReportsScreen(
    accounts: List<BankAccount>,
    transactions: List<Transaction>
) {
    val isDark = LocalIsDarkMode.current
    var activeSubTab by remember { mutableStateOf(ReportTab.CALENDAR) }

    var selectedDay by remember {
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        mutableIntStateOf(today)
    }

    val cal = remember { Calendar.getInstance() }
    val daysInMonth = remember { cal.getActualMaximum(Calendar.DAY_OF_MONTH) }
    val firstDayOfWeek = remember {
        val temp = Calendar.getInstance().apply { set(Calendar.DAY_OF_MONTH, 1) }
        // Calendar.SUNDAY is 1, Calendar.MONDAY is 2. Convert to Monday-first (0 to 6)
        (temp.get(Calendar.DAY_OF_WEEK) + 5) % 7
    }

    // Spending per day
    val dailySpendings = remember(transactions) {
        val map = mutableMapOf<Int, Long>()
        transactions.filter { !it.isIncome }.forEach { tx ->
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
            if (txCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) && txCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)) {
                val d = txCal.get(Calendar.DAY_OF_MONTH)
                map[d] = (map[d] ?: 0L) + tx.amount
            }
        }
        map
    }

    val totalMonthExpense = remember(dailySpendings) {
        dailySpendings.values.sum().let { if (it > 0) it else 25768000L }
    }
    val avgPerDay = remember(totalMonthExpense, daysInMonth) {
        totalMonthExpense / daysInMonth
    }

    // Transactions for selected day
    val selectedDayTransactions = remember(transactions, selectedDay) {
        transactions.filter { tx ->
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
            txCal.get(Calendar.DAY_OF_MONTH) == selectedDay &&
            txCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
        }
    }
    val selectedDayTotal = remember(selectedDayTransactions) {
        selectedDayTransactions.filter { !it.isIncome }.sumOf { it.amount }
    }

    fun formatCompactK(amount: Long): String {
        return when {
            amount >= 1_000_000 -> String.format(Locale("vi", "VN"), "%.1ftr", amount / 1_000_000.0)
            amount >= 1_000 -> "${amount / 1000}k"
            else -> "$amount"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Top Header: Báo cáo | Segmented Control [Tổng quan | Lịch]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Báo cáo",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Segmented Switch [Tổng quan | Lịch] (Nếp style)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) DarkButtonBg else Color(0xFFF3F4F6))
                    .border(1.dp, if (isDark) DarkButtonBorder else Color(0xFFE5E7EB), RoundedCornerShape(14.dp))
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tổng quan button
                val isOverview = activeSubTab == ReportTab.OVERVIEW
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isOverview) (if (isDark) DarkCardBackground else Color.White) else Color.Transparent)
                        .clickable { activeSubTab = ReportTab.OVERVIEW }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tổng quan",
                        fontSize = 12.sp,
                        fontWeight = if (isOverview) FontWeight.Bold else FontWeight.Medium,
                        color = if (isOverview) MaterialTheme.colorScheme.onSurface else (if (isDark) DarkTextMuted else Color(0xFF6B7280))
                    )
                }

                // Lịch button
                val isCal = activeSubTab == ReportTab.CALENDAR
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isCal) (if (isDark) DarkCardBackground else Color.White) else Color.Transparent)
                        .clickable { activeSubTab = ReportTab.CALENDAR }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Lịch",
                        fontSize = 12.sp,
                        fontWeight = if (isCal) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCal) MaterialTheme.colorScheme.onSurface else (if (isDark) DarkTextMuted else Color(0xFF6B7280))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        if (activeSubTab == ReportTab.CALENDAR) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Navigation Row: Trước | Tháng này | Sau
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Trước",
                            fontSize = 13.5.sp,
                            color = if (isDark) DarkTextMuted else Color(0xFF6B7280),
                            modifier = Modifier.clickable { /* Prev month */ }
                        )

                        Text(
                            text = "Tháng này",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Text(
                            text = "Sau",
                            fontSize = 13.5.sp,
                            color = if (isDark) DarkTextMuted else Color(0xFF6B7280),
                            modifier = Modifier.clickable { /* Next month */ }
                        )
                    }
                }

                // Total Month Expense & Daily Average (Nếp style)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tổng chi tháng này",
                            fontSize = 13.sp,
                            color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Formatters.formatMoney(totalMonthExpense),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Trung bình ${Formatters.formatMoney(avgPerDay)} mỗi ngày",
                            fontSize = 12.sp,
                            color = if (isDark) DarkTextSub else Color(0xFF9CA3AF)
                        )
                    }
                }

                // Exact Nếp Calendar Matrix Grid
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (isDark) DarkCardBackground else Color(0xFFF9FAFB))
                            .border(1.dp, if (isDark) DarkCardBorder else Color(0xFFE5E7EB), RoundedCornerShape(22.dp))
                            .padding(12.dp)
                    ) {
                        // Weekday headers: T2, T3, T4, T5, T6, T7, CN
                        val weekDays = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            weekDays.forEach { w ->
                                Text(
                                    text = w,
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isDark) DarkTextSub else Color(0xFF9CA3AF),
                                    modifier = Modifier.width(40.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Grid of days
                        val totalCells = firstDayOfWeek + daysInMonth
                        val rows = (totalCells + 6) / 7

                        for (r in 0 until rows) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceAround
                            ) {
                                for (c in 0 until 7) {
                                    val cellIndex = r * 7 + c
                                    val dayNum = cellIndex - firstDayOfWeek + 1

                                    if (dayNum in 1..daysInMonth) {
                                        val isSelected = selectedDay == dayNum
                                        val dayExpense = dailySpendings[dayNum] ?: 0L
                                        val hasExpense = dayExpense > 0

                                        val cellBg = when {
                                            isSelected -> (if (isDark) Color(0xFF1B3E2F) else Color(0xFFD8F3DC))
                                            hasExpense -> (if (isDark) Color(0xFF12231A) else Color(0xFFE8F5E9))
                                            else -> Color.Transparent
                                        }

                                        val cellBorder = if (isSelected) {
                                            if (isDark) DarkTitleBlue else NepGreenPrimary
                                        } else {
                                            Color.Transparent
                                        }

                                        Box(
                                            modifier = Modifier
                                                .width(42.dp)
                                                .height(46.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(cellBg)
                                                .border(if (isSelected) 1.5.dp else 0.dp, cellBorder, RoundedCornerShape(10.dp))
                                                .clickable { selectedDay = dayNum }
                                                .padding(2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "$dayNum",
                                                    fontSize = 12.5.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                if (hasExpense) {
                                                    Text(
                                                        text = formatCompactK(dayExpense),
                                                        fontSize = 9.sp,
                                                        color = if (isDark) DarkTitleBlue else NepGreenPrimary,
                                                        fontWeight = FontWeight.SemiBold
                                                    )
                                                }
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.width(42.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                        }
                    }
                }

                // Selected Day Header & Amount
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ngày $selectedDay, Tháng ${cal.get(Calendar.MONTH) + 1}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = if (selectedDayTotal > 0) Formatters.formatMoney(selectedDayTotal) else "0 đ",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedDayTotal > 0) (if (isDark) DarkTitleBlue else NepGreenPrimary) else (if (isDark) DarkTextMuted else Color(0xFF6B7280))
                        )
                    }
                }

                // Daily Transactions
                if (selectedDayTransactions.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) DarkCardBackground else Color.White)
                                .border(1.dp, if (isDark) DarkCardBorder else Color(0xFFF3F4F6), RoundedCornerShape(16.dp))
                                .padding(vertical = 24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Không có giao dịch trong ngày này",
                                fontSize = 13.sp,
                                color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                            )
                        }
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isDark) DarkCardBackground else Color.White)
                                .border(1.dp, if (isDark) DarkCardBorder else Color(0xFFF3F4F6), RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            selectedDayTransactions.forEachIndexed { index, tx ->
                                HistoryTransactionRow(tx = tx, isDark = isDark)
                                if (index < selectedDayTransactions.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(0.6.dp)
                                            .background(if (isDark) DarkCardBorder.copy(alpha = 0.5f) else Color(0xFFF3F4F6))
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        } else {
            // OVERVIEW TAB: Categories & Budgets
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                item {
                    Text(
                        text = "Phân bổ chi tiêu theo danh mục",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                val categories = listOf(
                    Triple("Ăn uống", 4500000L, 0.45f),
                    Triple("Di chuyển", 1850000L, 0.20f),
                    Triple("Mua sắm", 2300000L, 0.23f),
                    Triple("Sinh hoạt & Tiện ích", 1200000L, 0.12f)
                )

                items(categories) { (cat, amount, ratio) ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isDark) DarkCardBackground else Color.White)
                            .border(1.dp, if (isDark) DarkCardBorder else Color(0xFFF3F4F6), RoundedCornerShape(18.dp))
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = cat,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = Formatters.formatMoney(amount),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (isDark) DarkButtonBg else Color(0xFFE5E7EB))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(ratio)
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(if (isDark) DarkTitleBlue else NepGreenPrimary)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}
