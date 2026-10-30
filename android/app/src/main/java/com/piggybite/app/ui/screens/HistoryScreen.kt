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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.piggybite.app.model.Transaction
import com.piggybite.app.ui.theme.DarkButtonBg
import com.piggybite.app.ui.theme.DarkButtonBorder
import com.piggybite.app.ui.theme.DarkCardBackground
import com.piggybite.app.ui.theme.DarkCardBorder
import com.piggybite.app.ui.theme.DarkExpenseText
import com.piggybite.app.ui.theme.DarkIncomeText
import com.piggybite.app.ui.theme.DarkTextMain
import com.piggybite.app.ui.theme.DarkTextMuted
import com.piggybite.app.ui.theme.DarkTextSub
import com.piggybite.app.ui.theme.DarkTitleBlue
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.NepGreenPrimary
import com.piggybite.app.ui.theme.SkyBluePrimary
import com.piggybite.app.ui.theme.SkyBlueSoft
import com.piggybite.app.util.Formatters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class HistoryFilter(val label: String) {
    ALL("Tất cả"),
    EXPENSE("Chi"),
    INCOME("Thu"),
    PENDING("Chờ xác nhận •")
}

@Composable
fun HistoryScreen(
    transactions: List<Transaction>,
    onAddTransaction: () -> Unit
) {
    val isDark = LocalIsDarkMode.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(HistoryFilter.ALL) }

    // Filter transactions
    val filteredList = remember(transactions, searchQuery, selectedFilter) {
        transactions.filter { tx ->
            val matchQuery = searchQuery.isBlank() ||
                    tx.note.contains(searchQuery, ignoreCase = true) ||
                    tx.category.contains(searchQuery, ignoreCase = true) ||
                    tx.bankName.contains(searchQuery, ignoreCase = true) ||
                    tx.amount.toString().contains(searchQuery)

            val matchFilter = when (selectedFilter) {
                HistoryFilter.ALL -> true
                HistoryFilter.EXPENSE -> !tx.isIncome
                HistoryFilter.INCOME -> tx.isIncome
                HistoryFilter.PENDING -> false // Optional pending transactions
            }

            matchQuery && matchFilter
        }
    }

    // Group transactions by Date (Hôm nay, Hôm qua, 21/10...)
    val groupedByDate = remember(filteredList) {
        val todayCal = Calendar.getInstance()
        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }

        val formatDayMonth = SimpleDateFormat("dd/MM", Locale("vi", "VN"))

        filteredList.groupBy { tx ->
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
            when {
                txCal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                txCal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR) -> "Hôm nay"

                txCal.get(Calendar.YEAR) == yesterdayCal.get(Calendar.YEAR) &&
                txCal.get(Calendar.DAY_OF_YEAR) == yesterdayCal.get(Calendar.DAY_OF_YEAR) -> "Hôm qua"

                else -> formatDayMonth.format(Date(tx.timestamp))
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
        // Title (Nếp style: Large, bold, left-aligned)
        Text(
            text = "Lịch sử",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar (Exact Nếp design)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Tìm người nhận, ngày, số tiền, ghi chú...",
                    fontSize = 13.5.sp,
                    color = if (isDark) DarkTextMuted else Color(0xFF9CA3AF)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Tìm kiếm",
                    tint = if (isDark) DarkTextMuted else Color(0xFF6B7280),
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Xoá",
                            tint = if (isDark) DarkTextMuted else Color(0xFF6B7280),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = if (isDark) DarkTitleBlue else SkyBluePrimary,
                unfocusedBorderColor = if (isDark) DarkCardBorder else Color(0xFFE5E7EB),
                focusedContainerColor = if (isDark) DarkCardBackground else Color(0xFFF9FAFB),
                unfocusedContainerColor = if (isDark) DarkCardBackground else Color(0xFFF9FAFB),
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Filter Pills Row (Tất cả, Chi, Thu, Chờ xác nhận •)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HistoryFilter.values().forEach { filter ->
                val isSelected = selectedFilter == filter
                val activeBg = if (isDark) NepGreenPrimary else NepGreenPrimary
                val inactiveBg = if (isDark) DarkButtonBg else Color(0xFFF3F4F6)
                val activeBorder = if (isDark) DarkButtonBorder else Color.Transparent
                val inactiveBorder = if (isDark) DarkButtonBorder else Color(0xFFE5E7EB)

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) activeBg else inactiveBg)
                        .border(1.dp, if (isSelected) activeBorder else inactiveBorder, RoundedCornerShape(20.dp))
                        .clickable { selectedFilter = filter }
                        .padding(horizontal = 14.dp, vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter.label,
                        fontSize = 12.5.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                        color = if (isSelected) Color(0xFFEDEDED) else (if (isDark) DarkTextMuted else Color(0xFF4B5563))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grouped Transactions List
        if (groupedByDate.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Không tìm thấy giao dịch nào",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Thử tìm kiếm với từ khoá khác hoặc ghi chép mới",
                    fontSize = 12.5.sp,
                    color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedByDate.forEach { (dateHeader, txsInGroup) ->
                    item {
                        // Section Date Header (Hôm nay, Hôm qua, 21/10...)
                        Text(
                            text = dateHeader,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) DarkTextMuted else Color(0xFF6B7280),
                            modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                        )

                        // Card enclosing items for this date group
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isDark) DarkCardBackground else Color.White)
                                .border(1.dp, if (isDark) DarkCardBorder else Color(0xFFF3F4F6), RoundedCornerShape(20.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            txsInGroup.forEachIndexed { index, tx ->
                                HistoryTransactionRow(tx = tx, isDark = isDark)
                                if (index < txsInGroup.size - 1) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(0.8.dp)
                                            .background(if (isDark) DarkCardBorder.copy(alpha = 0.5f) else Color(0xFFF3F4F6))
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

@Composable
fun HistoryTransactionRow(
    tx: Transaction,
    isDark: Boolean
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale("vi", "VN"))
    val formattedTime = timeFormat.format(Date(tx.timestamp))

    // Select category icon
    val icon: ImageVector = when {
        tx.category.contains("Di chuyển", ignoreCase = true) || tx.note.contains("Grab", ignoreCase = true) -> Icons.Default.DirectionsCar
        tx.category.contains("Ăn uống", ignoreCase = true) || tx.note.contains("Cơm", ignoreCase = true) -> Icons.Default.Restaurant
        tx.category.contains("Cà phê", ignoreCase = true) || tx.note.contains("Highlands", ignoreCase = true) -> Icons.Default.LocalCafe
        tx.category.contains("Mua sắm", ignoreCase = true) || tx.note.contains("Shopee", ignoreCase = true) -> Icons.Default.ShoppingBag
        tx.category.contains("Sinh hoạt", ignoreCase = true) || tx.note.contains("Siêu thị", ignoreCase = true) -> Icons.Default.Home
        tx.isIncome -> Icons.Default.Payments
        else -> Icons.Default.CreditCard
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Circular Icon
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .background(if (isDark) DarkButtonBg else Color(0xFFF3F4F6))
                .border(1.dp, if (isDark) DarkButtonBorder else Color.Transparent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isDark) DarkTextMuted else Color(0xFF4B5563),
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Center: Title & Subtitle (Di chuyển • Tiền mặt)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = tx.note.ifBlank { tx.category },
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) DarkTextMain else Color(0xFF111827)
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${tx.category} · ${tx.bankName}",
                fontSize = 12.sp,
                color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
            )
        }

        // Right: Amount and Time
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = if (tx.isIncome) "+${Formatters.formatMoney(tx.amount)}" else "−${Formatters.formatMoney(tx.amount)}",
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (tx.isIncome) (if (isDark) DarkIncomeText else Color(0xFF10B981)) else (if (isDark) DarkTextMain else Color(0xFF111827))
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formattedTime,
                fontSize = 11.5.sp,
                color = if (isDark) DarkTextSub else Color(0xFF9CA3AF)
            )
        }
    }
}
