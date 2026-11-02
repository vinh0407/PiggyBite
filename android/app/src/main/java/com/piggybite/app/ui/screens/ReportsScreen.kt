package com.piggybite.app.ui.screens

import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SyncAlt
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piggybite.app.model.BankAccount
import com.piggybite.app.model.Transaction
import com.piggybite.app.ui.components.BankLogoView
import com.piggybite.app.ui.theme.DarkTextMain
import com.piggybite.app.ui.theme.DarkTextMuted
import com.piggybite.app.ui.theme.DarkTextSub
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.PiggyBorderDark
import com.piggybite.app.ui.theme.PiggyBorderLight
import com.piggybite.app.ui.theme.PiggyGreen
import com.piggybite.app.ui.theme.PiggyGreenSoftDark
import com.piggybite.app.ui.theme.PiggyGreenSoftLight
import com.piggybite.app.ui.theme.PiggyOrange
import com.piggybite.app.ui.theme.PiggyOrangeSoftDark
import com.piggybite.app.ui.theme.PiggyOrangeSoftLight
import com.piggybite.app.ui.theme.PiggyPrimary
import com.piggybite.app.ui.theme.PiggyPrimarySoftDark
import com.piggybite.app.ui.theme.PiggyPrimarySoftLight
import com.piggybite.app.ui.theme.PiggyRed
import com.piggybite.app.ui.theme.PiggyRedSoftDark
import com.piggybite.app.ui.theme.PiggyRedSoftLight
import com.piggybite.app.ui.theme.PiggySurfaceDark
import com.piggybite.app.ui.theme.PiggySurfaceLight
import com.piggybite.app.ui.theme.PiggySurfaceSoftDark
import com.piggybite.app.ui.theme.PiggySurfaceSoftLight
import com.piggybite.app.ui.theme.PiggyViolet
import com.piggybite.app.ui.theme.PiggyVioletSoftDark
import com.piggybite.app.ui.theme.PiggyVioletSoftLight
import com.piggybite.app.ui.theme.TextMuted
import com.piggybite.app.util.Formatters
import java.util.Calendar
import java.util.Locale

enum class ReportTab {
    OVERVIEW,
    CALENDAR
}

@Composable
fun ReportsScreen(
    accounts: List<BankAccount>,
    transactions: List<Transaction>,
    onAddBank: () -> Unit = {},
    onEditAccount: (BankAccount) -> Unit = {},
    onOpenMonthlyWrapped: () -> Unit = {}
) {
    val isDark = LocalIsDarkMode.current
    var activeSubTab by remember { mutableStateOf(ReportTab.OVERVIEW) }
    var monthOffset by remember { mutableIntStateOf(0) }

    val cal = remember(monthOffset) {
        Calendar.getInstance().apply { add(Calendar.MONTH, monthOffset) }
    }

    var selectedDay by remember(monthOffset) {
        val maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        val today = Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        mutableIntStateOf(if (monthOffset == 0) today else 1.coerceAtMost(maxDays))
    }

    val daysInMonth = remember(cal) { cal.getActualMaximum(Calendar.DAY_OF_MONTH) }
    val firstDayOfWeek = remember(cal) {
        val temp = (cal.clone() as Calendar).apply { set(Calendar.DAY_OF_MONTH, 1) }
        (temp.get(Calendar.DAY_OF_WEEK) + 5) % 7
    }

    // Chi tiêu từng ngày trong tháng
    val dailySpendings = remember(transactions, cal) {
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
        dailySpendings.values.sum()
    }
    val avgPerDay = remember(totalMonthExpense, daysInMonth) {
        if (daysInMonth > 0) totalMonthExpense / daysInMonth else 0L
    }

    // Giao dịch trong ngày đã chọn
    val selectedDayTransactions = remember(transactions, selectedDay, cal) {
        transactions.filter { tx ->
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
            txCal.get(Calendar.DAY_OF_MONTH) == selectedDay &&
            txCal.get(Calendar.MONTH) == cal.get(Calendar.MONTH) &&
            txCal.get(Calendar.YEAR) == cal.get(Calendar.YEAR)
        }
    }
    val selectedDayTotal = remember(selectedDayTransactions) {
        selectedDayTransactions.filter { !it.isIncome }.sumOf { it.amount }
    }

    // Tổng quan chi tiêu theo danh mục
    val overviewExpenseTxs = remember(transactions) {
        transactions.filter { !it.isIncome }
    }
    val overviewTotalExpense = remember(overviewExpenseTxs) {
        overviewExpenseTxs.sumOf { it.amount }
    }
    val overviewCategories = remember(overviewExpenseTxs, overviewTotalExpense) {
        if (overviewExpenseTxs.isEmpty()) {
            emptyList<Triple<String, Long, Float>>()
        } else {
            overviewExpenseTxs
                .groupBy { it.category }
                .map { (cat, txs) ->
                    val sum = txs.sumOf { it.amount }
                    val ratio = if (overviewTotalExpense > 0) sum.toFloat() / overviewTotalExpense else 0f
                    Triple(cat, sum, ratio)
                }
                .sortedByDescending { it.second }
        }
    }

    // Tính dòng tiền 6 tháng gần nhất (Figma: cashflow-card bar-chart)
    val sixMonthsCashflow = remember(transactions) {
        val list = mutableListOf<Triple<String, Long, Long>>()
        val now = Calendar.getInstance()
        for (i in 5 downTo 0) {
            val c = (now.clone() as Calendar).apply { add(Calendar.MONTH, -i) }
            val m = c.get(Calendar.MONTH)
            val y = c.get(Calendar.YEAR)
            val label = "T${m + 1}"

            var inc = 0L
            var exp = 0L
            transactions.forEach { tx ->
                val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
                if (txCal.get(Calendar.MONTH) == m && txCal.get(Calendar.YEAR) == y) {
                    if (tx.isIncome) inc += tx.amount else exp += tx.amount
                }
            }
            list.add(Triple(label, inc, exp))
        }
        list
    }

    fun formatCompactK(amount: Long): String {
        return when {
            amount >= 1_000_000 -> String.format(Locale("vi", "VN"), "%.1ftr", amount / 1_000_000.0)
            amount >= 1_000 -> "${amount / 1000}k"
            else -> "$amount"
        }
    }

    val monthDisplayTitle = "Tháng ${cal.get(Calendar.MONTH) + 1}, ${cal.get(Calendar.YEAR)}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Top Header + Segmented Control
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Báo cáo tài chính",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = monthDisplayTitle,
                    fontSize = 12.sp,
                    color = if (isDark) DarkTextMuted else TextMuted
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                // Button Mở Wrapped Tổng kết tháng
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isDark) Color(0xFF1E293B) else Color(0xFFE0F2FE))
                        .border(1.dp, if (isDark) Color(0xFF334155) else Color(0xFFBAE6FD), RoundedCornerShape(12.dp))
                        .clickable { onOpenMonthlyWrapped() }
                        .padding(horizontal = 9.dp, vertical = 7.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "✨ Wrapped",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF38BDF8) else PiggyPrimary
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Segmented Control (Figma: segmented-control)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(14.dp))
                        .background(if (isDark) PiggySurfaceSoftDark else PiggySurfaceSoftLight)
                        .border(1.dp, if (isDark) PiggyBorderDark else PiggyBorderLight, RoundedCornerShape(14.dp))
                        .padding(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                val isOverview = activeSubTab == ReportTab.OVERVIEW
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isOverview) (if (isDark) PiggySurfaceDark else PiggySurfaceLight) else Color.Transparent
                        )
                        .clickable { activeSubTab = ReportTab.OVERVIEW }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PieChart,
                            contentDescription = null,
                            tint = if (isOverview) PiggyPrimary else (if (isDark) DarkTextMuted else TextMuted),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tổng quan",
                            fontSize = 11.5.sp,
                            fontWeight = if (isOverview) FontWeight.Bold else FontWeight.Medium,
                            color = if (isOverview) PiggyPrimary else (if (isDark) DarkTextMuted else TextMuted)
                        )
                    }
                }

                val isCal = activeSubTab == ReportTab.CALENDAR
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isCal) (if (isDark) PiggySurfaceDark else PiggySurfaceLight) else Color.Transparent
                        )
                        .clickable { activeSubTab = ReportTab.CALENDAR }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = if (isCal) PiggyPrimary else (if (isDark) DarkTextMuted else TextMuted),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Lịch chi tiêu",
                            fontSize = 11.5.sp,
                            fontWeight = if (isCal) FontWeight.Bold else FontWeight.Medium,
                            color = if (isCal) PiggyPrimary else (if (isDark) DarkTextMuted else TextMuted)
                        )
                    }
                }
            }
        }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeSubTab == ReportTab.CALENDAR) {
            // ==========================================
            // SUB-TAB 1: LỊCH CHI TIÊU (Figma: calendar-card)
            // ==========================================
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Month switcher: < Tháng 11, 2026 >
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(if (isDark) PiggySurfaceDark else PiggySurfaceLight)
                            .border(1.dp, if (isDark) PiggyBorderDark else PiggyBorderLight, RoundedCornerShape(18.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(if (isDark) PiggySurfaceSoftDark else PiggySurfaceSoftLight)
                                .clickable { monthOffset-- },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Tháng trước",
                                tint = PiggyPrimary,
                                modifier = Modifier.size(17.dp)
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = monthDisplayTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "Tổng chi: ${Formatters.formatMoney(totalMonthExpense)}",
                                fontSize = 11.5.sp,
                                color = if (isDark) DarkTextMuted else TextMuted
                            )
                        }

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(11.dp))
                                .background(if (isDark) PiggySurfaceSoftDark else PiggySurfaceSoftLight)
                                .clickable { monthOffset++ },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Tháng sau",
                                tint = PiggyPrimary,
                                modifier = Modifier.size(17.dp)
                            )
                        }
                    }
                }

                // Calendar Grid Card
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (isDark) PiggySurfaceDark else PiggySurfaceLight)
                            .border(1.dp, if (isDark) PiggyBorderDark else PiggyBorderLight, RoundedCornerShape(22.dp))
                            .padding(14.dp)
                    ) {
                        // Weekdays row
                        val weekDays = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            weekDays.forEach { w ->
                                Text(
                                    text = w,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) DarkTextSub else Color(0xFFA4AFBF),
                                    modifier = Modifier.width(42.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Days Grid
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

                                        Box(
                                            modifier = Modifier
                                                .width(44.dp)
                                                .height(52.dp)
                                                .clip(RoundedCornerShape(11.dp))
                                                .background(
                                                    if (isSelected) PiggyPrimary
                                                    else if (hasExpense) (if (isDark) Color(0xFF142436) else Color(0xFFEAF6FF))
                                                    else Color.Transparent
                                                )
                                                .border(
                                                    1.dp,
                                                    if (isSelected) PiggyPrimary
                                                    else if (hasExpense) (if (isDark) Color(0xFF1E3A5F) else Color(0xFFBAE6FD))
                                                    else (if (isDark) Color(0xFF141F2E) else Color(0xFFF1F5F9)),
                                                    RoundedCornerShape(11.dp)
                                                )
                                                .clickable { selectedDay = dayNum }
                                                .padding(4.dp),
                                            contentAlignment = Alignment.TopStart
                                        ) {
                                            Column {
                                                Text(
                                                    text = "$dayNum",
                                                    fontSize = 11.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                                )
                                                if (hasExpense) {
                                                    Spacer(modifier = Modifier.height(2.dp))
                                                    Text(
                                                        text = formatCompactK(dayExpense),
                                                        fontSize = 9.sp,
                                                        color = if (isSelected) Color(0xFFDCEFFF) else PiggyPrimary,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                            }

                                            if (hasExpense && !isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(5.dp)
                                                        .clip(CircleShape)
                                                        .background(PiggyPrimary)
                                                        .align(Alignment.BottomEnd)
                                                )
                                            }
                                        }
                                    } else {
                                        Spacer(modifier = Modifier.width(44.dp))
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                        }
                    }
                }

                // Selected Day Header & Transactions
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp, bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ngày $selectedDay, Tháng ${cal.get(Calendar.MONTH) + 1}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = Formatters.formatMoney(selectedDayTotal),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = PiggyPrimary
                        )
                    }
                }

                if (selectedDayTransactions.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) PiggySurfaceDark else PiggySurfaceLight)
                                .border(1.dp, if (isDark) PiggyBorderDark else PiggyBorderLight, RoundedCornerShape(16.dp))
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Không có giao dịch trong ngày này",
                                fontSize = 13.sp,
                                color = if (isDark) DarkTextMuted else TextMuted
                            )
                        }
                    }
                } else {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isDark) PiggySurfaceDark else PiggySurfaceLight)
                                .border(1.dp, if (isDark) PiggyBorderDark else PiggyBorderLight, RoundedCornerShape(20.dp))
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            selectedDayTransactions.forEachIndexed { index, tx ->
                                HistoryTransactionRow(tx = tx, isDark = isDark)
                                if (index < selectedDayTransactions.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(0.6.dp)
                                            .background(if (isDark) PiggyBorderDark else PiggyBorderLight)
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        } else {
            // ==========================================
            // SUB-TAB 2: TỔNG QUAN (Figma: category-card + cashflow-card + wallets)
            // ==========================================
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Phân bổ theo danh mục (Figma: category-card)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (isDark) PiggySurfaceDark else PiggySurfaceLight)
                            .border(1.dp, if (isDark) PiggyBorderDark else PiggyBorderLight, RoundedCornerShape(22.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = "PHÂN BỔ THÁNG",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFF8997AA) else TextMuted,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Chi tiêu theo danh mục",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        if (overviewCategories.isEmpty()) {
                            Text(
                                text = "Chưa có dữ liệu chi tiêu để phân bổ",
                                fontSize = 12.5.sp,
                                color = if (isDark) DarkTextMuted else TextMuted
                            )
                        } else {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                overviewCategories.forEach { (cat, amount, ratio) ->
                                    val (icon, bg, tint) = getCategoryVisuals(cat, false, isDark)
                                    val pct = (ratio * 100).toInt()

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(bg),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = icon,
                                                contentDescription = cat,
                                                tint = tint,
                                                modifier = Modifier.size(17.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(
                                                    text = cat,
                                                    fontSize = 13.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                                Text(
                                                    text = Formatters.formatMoney(amount),
                                                    fontSize = 12.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = MaterialTheme.colorScheme.onSurface
                                                )
                                            }

                                            Spacer(modifier = Modifier.height(5.dp))

                                            // Progress Bar
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(6.dp)
                                                    .clip(RoundedCornerShape(999.dp))
                                                    .background(if (isDark) Color(0xFF1E2B3E) else Color(0xFFE5EAF1))
                                            ) {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxWidth(ratio.coerceIn(0.02f, 1f))
                                                        .height(6.dp)
                                                        .clip(RoundedCornerShape(999.dp))
                                                        .background(tint)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(10.dp))

                                        Text(
                                            text = "$pct%",
                                            fontSize = 11.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (isDark) DarkTextMuted else TextMuted
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // 2. Dòng tiền 6 tháng gần nhất (Figma: cashflow-card bar-chart)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (isDark) PiggySurfaceDark else PiggySurfaceLight)
                            .border(1.dp, if (isDark) PiggyBorderDark else PiggyBorderLight, RoundedCornerShape(22.dp))
                            .padding(20.dp)
                    ) {
                        Column {
                            Text(
                                text = "6 THÁNG GẦN NHẤT",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) Color(0xFF8997AA) else TextMuted,
                                letterSpacing = 1.sp
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "Dòng tiền",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        val maxVal = sixMonthsCashflow.maxOfOrNull { maxOf(it.second, it.third) } ?: 1L
                        val safeMax = if (maxVal > 0) maxVal else 1L

                        // Bar Chart Visual
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            sixMonthsCashflow.forEach { (label, inc, exp) ->
                                val incRatio = (inc.toFloat() / safeMax).coerceIn(0.08f, 1f)
                                val expRatio = (exp.toFloat() / safeMax).coerceIn(0.08f, 1f)

                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Bottom
                                ) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                                        verticalAlignment = Alignment.Bottom
                                    ) {
                                        // Income Bar (Sky Blue)
                                        Box(
                                            modifier = Modifier
                                                .width(10.dp)
                                                .height((incRatio * 95).dp)
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                .background(PiggyPrimary)
                                        )

                                        // Expense Bar (Pale Blue / Rose)
                                        Box(
                                            modifier = Modifier
                                                .width(10.dp)
                                                .height((expRatio * 95).dp)
                                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                                .background(if (isDark) Color(0xFF4C2A34) else Color(0xFFBFD5E6))
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        color = if (isDark) DarkTextMuted else TextMuted
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Bar legend
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(PiggyPrimary)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Thu nhập",
                                fontSize = 11.5.sp,
                                color = if (isDark) DarkTextMuted else TextMuted
                            )

                            Spacer(modifier = Modifier.width(18.dp))

                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(if (isDark) Color(0xFF4C2A34) else Color(0xFFBFD5E6))
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Chi tiêu",
                                fontSize = 11.5.sp,
                                color = if (isDark) DarkTextMuted else TextMuted
                            )
                        }
                    }
                }

                // 3. Quản lý tài khoản ví
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (isDark) PiggySurfaceDark else PiggySurfaceLight)
                            .border(1.dp, if (isDark) PiggyBorderDark else PiggyBorderLight, RoundedCornerShape(22.dp))
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "VÍ & TÀI KHOẢN",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) Color(0xFF8997AA) else TextMuted,
                                    letterSpacing = 1.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Danh sách ví nguồn",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }

                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isDark) PiggyPrimarySoftDark else PiggyPrimarySoftLight)
                                    .clickable { onAddBank() }
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = null,
                                    tint = PiggyPrimary,
                                    modifier = Modifier.size(15.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Thêm ví",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PiggyPrimary
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            accounts.forEach { acc ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(if (isDark) PiggySurfaceSoftDark else PiggySurfaceSoftLight)
                                        .border(1.dp, if (isDark) PiggyBorderDark else PiggyBorderLight, RoundedCornerShape(14.dp))
                                        .clickable { onEditAccount(acc) }
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    BankLogoView(bankId = acc.bankId, size = 32.dp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = acc.bankName,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                        if (acc.accountNumber.isNotBlank()) {
                                            Text(
                                                text = "STK: ${acc.accountNumber}",
                                                fontSize = 11.sp,
                                                color = if (isDark) DarkTextMuted else TextMuted
                                            )
                                        }
                                    }
                                    Text(
                                        text = Formatters.formatMoney(acc.balance),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

private fun getCategoryVisuals(
    category: String,
    isIncome: Boolean,
    isDark: Boolean
): Triple<ImageVector, Color, Color> {
    if (isIncome) {
        return Triple(
            Icons.Default.Payments,
            if (isDark) PiggyGreenSoftDark else PiggyGreenSoftLight,
            PiggyGreen
        )
    }

    val lower = category.lowercase()
    return when {
        lower.contains("ăn") || lower.contains("cơm") || lower.contains("nhà hàng") -> Triple(
            Icons.Default.Restaurant,
            if (isDark) PiggyRedSoftDark else PiggyRedSoftLight,
            PiggyRed
        )
        lower.contains("cà phê") || lower.contains("cafe") || lower.contains("nước") -> Triple(
            Icons.Default.Coffee,
            if (isDark) PiggyOrangeSoftDark else PiggyOrangeSoftLight,
            PiggyOrange
        )
        lower.contains("sắm") || lower.contains("đồ") || lower.contains("quần áo") -> Triple(
            Icons.Default.ShoppingBag,
            if (isDark) PiggyVioletSoftDark else PiggyVioletSoftLight,
            PiggyViolet
        )
        lower.contains("xe") || lower.contains("di chuyển") || lower.contains("xăng") -> Triple(
            Icons.Default.DirectionsCar,
            if (isDark) PiggyPrimarySoftDark else PiggyPrimarySoftLight,
            PiggyPrimary
        )
        else -> Triple(
            Icons.Default.SyncAlt,
            if (isDark) PiggyPrimarySoftDark else PiggyPrimarySoftLight,
            PiggyPrimary
        )
    }
}
