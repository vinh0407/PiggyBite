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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountBalanceWallet
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
import com.piggybite.app.ui.components.BankLogoView
import com.piggybite.app.ui.theme.CardBorder
import com.piggybite.app.ui.theme.DarkButtonBg
import com.piggybite.app.ui.theme.DarkButtonBorder
import com.piggybite.app.ui.theme.DarkCardBackground
import com.piggybite.app.ui.theme.DarkCardBorder
import com.piggybite.app.ui.theme.DarkHighlightNavyBg
import com.piggybite.app.ui.theme.DarkHighlightNavyBorder
import com.piggybite.app.ui.theme.DarkHighlightNavyText
import com.piggybite.app.ui.theme.DarkTextMain
import com.piggybite.app.ui.theme.DarkTextMuted
import com.piggybite.app.ui.theme.DarkTextSub
import com.piggybite.app.ui.theme.DarkTitleBlue
import com.piggybite.app.ui.theme.HighlightBlueLightBg
import com.piggybite.app.ui.theme.HighlightBlueLightBorder
import com.piggybite.app.ui.theme.HighlightBlueLightText
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.SkyBluePrimary
import com.piggybite.app.ui.theme.SkyBlueSoft
import com.piggybite.app.ui.theme.TextMuted
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
    transactions: List<Transaction>,
    onAddBank: () -> Unit = {},
    onEditAccount: (BankAccount) -> Unit = {}
) {
    val isDark = LocalIsDarkMode.current
    var activeSubTab by remember { mutableStateOf(ReportTab.CALENDAR) }

    // Quản lý chuyển tháng qua lại
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

    fun formatCompactK(amount: Long): String {
        return when {
            amount >= 1_000_000 -> String.format(Locale("vi", "VN"), "%.1ftr", amount / 1_000_000.0)
            amount >= 1_000 -> "${amount / 1000}k"
            else -> "$amount"
        }
    }

    val monthDisplayTitle = if (monthOffset == 0) "Tháng này" else "Tháng ${cal.get(Calendar.MONTH) + 1}, ${cal.get(Calendar.YEAR)}"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Top Header: Báo cáo | Segmented Switch [Tổng quan | Lịch]
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

            // Segmented Switch [Tổng quan | Lịch]
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(if (isDark) DarkButtonBg else Color(0xFFF3F4F6))
                    .border(1.dp, if (isDark) DarkButtonBorder else CardBorder, RoundedCornerShape(14.dp))
                    .padding(3.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Tổng quan button
                val isOverview = activeSubTab == ReportTab.OVERVIEW
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isOverview) (if (isDark) DarkHighlightNavyBg else Color.White) else Color.Transparent
                        )
                        .border(
                            1.dp,
                            if (isOverview) (if (isDark) DarkHighlightNavyBorder else CardBorder) else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { activeSubTab = ReportTab.OVERVIEW }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tổng quan",
                        fontSize = 12.sp,
                        fontWeight = if (isOverview) FontWeight.Bold else FontWeight.Medium,
                        color = if (isOverview) (if (isDark) DarkTitleBlue else Color(0xFF111827)) else (if (isDark) DarkTextMuted else Color(0xFF6B7280))
                    )
                }

                // Lịch button
                val isCal = activeSubTab == ReportTab.CALENDAR
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isCal) (if (isDark) DarkHighlightNavyBg else Color.White) else Color.Transparent
                        )
                        .border(
                            1.dp,
                            if (isCal) (if (isDark) DarkHighlightNavyBorder else CardBorder) else Color.Transparent,
                            RoundedCornerShape(10.dp)
                        )
                        .clickable { activeSubTab = ReportTab.CALENDAR }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Lịch",
                        fontSize = 12.sp,
                        fontWeight = if (isCal) FontWeight.Bold else FontWeight.Medium,
                        color = if (isCal) (if (isDark) DarkTitleBlue else Color(0xFF111827)) else (if (isDark) DarkTextMuted else Color(0xFF6B7280))
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
                // Nút chuyển tháng: Trước | Tháng này | Sau
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) DarkCardBackground else Color(0xFFF9FAFB))
                            .border(1.dp, if (isDark) DarkCardBorder else CardBorder, RoundedCornerShape(16.dp))
                            .padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "‹ Trước",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                            modifier = Modifier
                                .clickable { monthOffset-- }
                                .padding(4.dp)
                        )

                        Text(
                            text = monthDisplayTitle,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) DarkTextMain else Color(0xFF111827)
                        )

                        Text(
                            text = "Sau ›",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                            modifier = Modifier
                                .clickable { monthOffset++ }
                                .padding(4.dp)
                        )
                    }
                }

                // Tổng chi tháng & Trung bình ngày
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Tổng chi $monthDisplayTitle",
                            fontSize = 13.sp,
                            color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = Formatters.formatMoney(totalMonthExpense),
                            fontSize = 32.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isDark) DarkTextMain else Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Trung bình ${Formatters.formatMoney(avgPerDay)} mỗi ngày",
                            fontSize = 12.sp,
                            color = if (isDark) DarkTextSub else Color(0xFF9CA3AF)
                        )
                    }
                }

                // Ma trận Lịch 7 cột (Hiển thị rõ ràng các khoảng chi tiêu mỗi ngày)
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(22.dp))
                            .background(if (isDark) DarkCardBackground else Color(0xFFF9FAFB))
                            .border(1.dp, if (isDark) DarkCardBorder else CardBorder, RoundedCornerShape(22.dp))
                            .padding(12.dp)
                    ) {
                        // Thứ trong tuần: T2, T3, T4, T5, T6, T7, CN
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
                                    modifier = Modifier.width(42.dp),
                                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Lưới các ngày
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
                                            isSelected -> (if (isDark) DarkHighlightNavyBg else HighlightBlueLightBg)
                                            hasExpense -> (if (isDark) Color(0xFF161E28) else Color(0xFFF0F9FF))
                                            else -> Color.Transparent
                                        }

                                        val cellBorder = when {
                                            isSelected -> (if (isDark) DarkTitleBlue else SkyBluePrimary)
                                            hasExpense -> (if (isDark) DarkHighlightNavyBorder else HighlightBlueLightBorder)
                                            else -> Color.Transparent
                                        }

                                        Box(
                                            modifier = Modifier
                                                .width(42.dp)
                                                .height(48.dp)
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(cellBg)
                                                .border(if (isSelected) 1.5.dp else (if (hasExpense) 1.dp else 0.dp), cellBorder, RoundedCornerShape(10.dp))
                                                .clickable { selectedDay = dayNum }
                                                .padding(2.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(
                                                    text = "$dayNum",
                                                    fontSize = 12.5.sp,
                                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                    color = if (isSelected) (if (isDark) DarkTitleBlue else SkyBluePrimary) else (if (isDark) DarkTextMain else Color(0xFF111827))
                                                )
                                                if (hasExpense) {
                                                    Text(
                                                        text = formatCompactK(dayExpense),
                                                        fontSize = 9.5.sp,
                                                        color = if (isDark) Color(0xFF38BDF8) else Color(0xFF0284C7),
                                                        fontWeight = FontWeight.Bold
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

                // Chi tiết giao dịch ngày đã chọn
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
                            color = if (isDark) DarkTextMain else Color(0xFF111827)
                        )
                        Text(
                            text = if (selectedDayTotal > 0) Formatters.formatMoney(selectedDayTotal) else "0 đ",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedDayTotal > 0) (if (isDark) DarkTitleBlue else SkyBluePrimary) else (if (isDark) DarkTextMuted else Color(0xFF6B7280))
                        )
                    }
                }

                if (selectedDayTransactions.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) DarkCardBackground else Color.White)
                                .border(1.dp, if (isDark) DarkCardBorder else CardBorder, RoundedCornerShape(16.dp))
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
                                .border(1.dp, if (isDark) DarkCardBorder else CardBorder, RoundedCornerShape(20.dp))
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
                                            .background(if (isDark) DarkCardBorder else CardBorder)
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
            // TAB TỔNG QUAN: Báo cáo danh mục + Quản lý ví & Danh sách ngân hàng
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(18.dp)
            ) {
                // PHẦN 1: BÁO CÁO PHÂN BỔ CHI TIÊU
                item {
                    Text(
                        text = "Phân bổ chi tiêu theo danh mục",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) DarkTextMain else Color(0xFF111827)
                    )
                }

                if (overviewCategories.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isDark) DarkCardBackground else Color.White)
                                .border(1.dp, if (isDark) DarkCardBorder else CardBorder, RoundedCornerShape(18.dp))
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Chưa có dữ liệu chi tiêu",
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isDark) DarkTextMain else Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Các khoản chi tiêu được ghi nhận sẽ tự động phân bổ tại đây",
                                fontSize = 11.5.sp,
                                color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                            )
                        }
                    }
                } else {
                    items(overviewCategories) { (cat, amount, ratio) ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isDark) DarkCardBackground else Color.White)
                                .border(1.dp, if (isDark) DarkCardBorder else CardBorder, RoundedCornerShape(18.dp))
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
                                    color = if (isDark) DarkTextMain else Color(0xFF111827)
                                )
                                Text(
                                    text = Formatters.formatMoney(amount),
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) DarkTextMain else Color(0xFF111827)
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
                                        .background(if (isDark) DarkTitleBlue else SkyBluePrimary)
                                )
                            }
                        }
                    }
                }

                // PHẦN 2: QUẢN LÝ VÍ & DANH SÁCH NGÂN HÀNG
                item {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AccountBalanceWallet,
                                contentDescription = null,
                                tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Quản lý ví & Tài khoản ngân hàng",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkTextMain else Color(0xFF111827)
                            )
                        }
                    }
                }

                // Danh sách ngân hàng
                if (accounts.isEmpty()) {
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(18.dp))
                                .background(if (isDark) DarkCardBackground else Color.White)
                                .border(1.dp, if (isDark) DarkCardBorder else CardBorder, RoundedCornerShape(18.dp))
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Chưa có tài khoản ngân hàng nào",
                                fontSize = 13.5.sp,
                                color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                            )
                        }
                    }
                } else {
                    items(accounts) { acc ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isDark) DarkCardBackground else Color.White)
                                .border(1.dp, if (isDark) DarkCardBorder else CardBorder, RoundedCornerShape(16.dp))
                                .clickable { onEditAccount(acc) }
                                .padding(horizontal = 14.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            BankLogoView(bankId = acc.bankId, size = 38.dp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = acc.bankName,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isDark) DarkTextMain else Color(0xFF111827)
                                )
                                if (acc.accountNumber.isNotBlank()) {
                                    Text(
                                        text = "STK: ${acc.accountNumber}",
                                        fontSize = 12.sp,
                                        color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                                    )
                                }
                            }
                            Text(
                                text = Formatters.formatMoney(acc.balance),
                                fontSize = 14.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) DarkTextMain else Color(0xFF111827)
                            )
                        }
                    }
                }

                // Nút Thêm ngân hàng / Ví điện tử
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isDark) DarkButtonBg else Color(0xFFF9FAFB))
                            .border(1.dp, if (isDark) DarkButtonBorder else CardBorder, RoundedCornerShape(16.dp))
                            .clickable { onAddBank() }
                            .padding(vertical = 12.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Thêm ngân hàng / Ví điện tử",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) DarkTitleBlue else SkyBluePrimary
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}
