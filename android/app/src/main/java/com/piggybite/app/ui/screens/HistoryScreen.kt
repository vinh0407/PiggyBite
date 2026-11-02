package com.piggybite.app.ui.screens

import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.piggybite.app.model.Transaction
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
import com.piggybite.app.ui.theme.PiggyPrimaryDark
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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

enum class HistoryFilter(val label: String) {
    ALL("Tất cả"),
    EXPENSE("Khoản chi"),
    INCOME("Khoản thu")
}

@Composable
fun HistoryScreen(
    transactions: List<Transaction>,
    onAddTransaction: () -> Unit = {},
    onDeleteTransaction: (String) -> Unit = {},
    onRefundTransaction: (Transaction) -> Unit = {}
) {
    val context = LocalContext.current
    val isDark = LocalIsDarkMode.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(HistoryFilter.ALL) }
    var selectedTxForAction by remember { mutableStateOf<Transaction?>(null) }

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
            }

            matchQuery && matchFilter
        }
    }

    // Totals for summary card
    val totalIncome = remember(filteredList) {
        filteredList.filter { it.isIncome }.sumOf { it.amount }
    }
    val totalExpense = remember(filteredList) {
        filteredList.filter { !it.isIncome }.sumOf { it.amount }
    }
    val netBalance = totalIncome - totalExpense

    // Group transactions by Date
    val groupedByDate = remember(filteredList) {
        val todayCal = Calendar.getInstance()
        val yesterdayCal = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        val formatDayMonth = SimpleDateFormat("EEEE, dd/MM", Locale("vi", "VN"))

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
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        // Top Title + Subtitle
        Column {
            Text(
                text = "Lịch sử giao dịch",
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Theo dõi mọi biến động chi tiêu và thu nhập",
                fontSize = 12.sp,
                color = if (isDark) DarkTextMuted else TextMuted
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Search Bar (Figma: search-box)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = {
                Text(
                    text = "Tìm nội dung, danh mục, số tiền...",
                    fontSize = 13.sp,
                    color = if (isDark) DarkTextMuted else Color(0xFFA4AFBF)
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Tìm kiếm",
                    tint = if (isDark) DarkTextMuted else TextMuted,
                    modifier = Modifier.size(19.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Xoá",
                            tint = if (isDark) DarkTextMuted else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PiggyPrimary,
                unfocusedBorderColor = if (isDark) PiggyBorderDark else PiggyBorderLight,
                focusedContainerColor = if (isDark) PiggySurfaceSoftDark else PiggySurfaceSoftLight,
                unfocusedContainerColor = if (isDark) PiggySurfaceSoftDark else PiggySurfaceSoftLight,
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurface
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Segmented Filter Tabs (Figma: filter-tabs)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(if (isDark) PiggySurfaceSoftDark else PiggySurfaceSoftLight)
                .border(1.dp, if (isDark) PiggyBorderDark else PiggyBorderLight, RoundedCornerShape(14.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            HistoryFilter.values().forEach { filter ->
                val isSelected = selectedFilter == filter
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (isSelected) {
                                if (isDark) PiggySurfaceDark else PiggySurfaceLight
                            } else Color.Transparent
                        )
                        .clickable { selectedFilter = filter }
                        .padding(vertical = 7.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = filter.label,
                        fontSize = 12.5.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = if (isSelected) {
                            PiggyPrimary
                        } else {
                            if (isDark) DarkTextMuted else TextMuted
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 3-Column Summary Card (Figma: history-summary: Tổng thu | Tổng chi | Chênh lệch)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) PiggySurfaceSoftDark else PiggySurfaceSoftLight)
                .border(1.dp, if (isDark) PiggyBorderDark else PiggyBorderLight, RoundedCornerShape(16.dp))
                .padding(vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tổng thu
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tổng thu",
                    fontSize = 10.5.sp,
                    color = if (isDark) DarkTextMuted else TextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "+${Formatters.formatMoney(totalIncome)}",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PiggyGreen
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(if (isDark) PiggyBorderDark else PiggyBorderLight)
            )

            // Tổng chi
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Tổng chi",
                    fontSize = 10.5.sp,
                    color = if (isDark) DarkTextMuted else TextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "−${Formatters.formatMoney(totalExpense)}",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Box(
                modifier = Modifier
                    .width(1.dp)
                    .height(24.dp)
                    .background(if (isDark) PiggyBorderDark else PiggyBorderLight)
            )

            // Chênh lệch
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Chênh lệch",
                    fontSize = 10.5.sp,
                    color = if (isDark) DarkTextMuted else TextMuted
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = (if (netBalance >= 0) "+" else "−") + Formatters.formatMoney(Math.abs(netBalance)),
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = PiggyPrimary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

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
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Thử tìm kiếm với từ khoá khác hoặc ghi chép mới",
                    fontSize = 12.sp,
                    color = if (isDark) DarkTextMuted else TextMuted
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                groupedByDate.forEach { (dateHeader, txsInGroup) ->
                    item {
                        val groupTotal = txsInGroup.sumOf { if (it.isIncome) it.amount else -it.amount }

                        // Date Group Heading (Figma: date-heading)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 4.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = dateHeader,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Text(
                                text = (if (groupTotal >= 0) "+" else "−") + Formatters.formatMoney(Math.abs(groupTotal)),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (groupTotal >= 0) PiggyGreen else if (isDark) DarkTextMuted else TextMuted
                            )
                        }

                        // Date Group Card Container
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(20.dp))
                                .background(if (isDark) PiggySurfaceDark else PiggySurfaceLight)
                                .border(
                                    1.dp,
                                    if (isDark) PiggyBorderDark else PiggyBorderLight,
                                    RoundedCornerShape(20.dp)
                                )
                                .padding(horizontal = 16.dp, vertical = 4.dp)
                        ) {
                            txsInGroup.forEachIndexed { index, tx ->
                                HistoryTransactionRow(
                                    tx = tx,
                                    isDark = isDark,
                                    onLongClick = { selectedTxForAction = tx }
                                )
                                if (index < txsInGroup.size - 1) {
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
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }

    // Dialog khi nhấn giữ lâu giao dịch (Xóa hoặc Báo lỗi hoàn tiền)
    if (selectedTxForAction != null) {
        val tx = selectedTxForAction!!
        Dialog(onDismissRequest = { selectedTxForAction = null }) {
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Tùy chọn giao dịch",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Card tóm tắt giao dịch
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(if (isDark) Color(0xFF1E293B) else Color(0xFFF1F5F9))
                            .padding(14.dp)
                    ) {
                        Text(
                            text = if (tx.note.isNotBlank()) tx.note else tx.category,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "${tx.category} • ${tx.bankName}",
                                fontSize = 12.sp,
                                color = if (isDark) DarkTextMuted else TextMuted
                            )
                            Text(
                                text = (if (tx.isIncome) "+" else "−") + Formatters.formatMoney(tx.amount),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (tx.isIncome) PiggyGreen else PiggyRed
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Nút 1: Báo giao dịch thất bại để hoàn lại tiền vào ví
                    if (!tx.isIncome) {
                        Button(
                            onClick = {
                                onRefundTransaction(tx)
                                selectedTxForAction = null
                                Toast.makeText(context, "Đã hoàn trả ${Formatters.formatMoney(tx.amount)} vào ${tx.bankName}!", Toast.LENGTH_SHORT).show()
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDark) Color(0xFF1E3A5F) else Color(0xFFE0F2FE),
                                contentColor = if (isDark) Color(0xFF38BDF8) else PiggyPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(Icons.Default.Replay, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Giao dịch thất bại (Hoàn lại tiền)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    // Nút 2: Xóa lịch sử giao dịch này
                    Button(
                        onClick = {
                            onDeleteTransaction(tx.id)
                            selectedTxForAction = null
                            Toast.makeText(context, "Đã xóa bản ghi giao dịch!", Toast.LENGTH_SHORT).show()
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDark) Color(0xFF3F1D24) else Color(0xFFFEE2E2),
                            contentColor = PiggyRed
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "Xóa lịch sử giao dịch này",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Nút Đóng
                    Button(
                        onClick = { selectedTxForAction = null },
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Transparent,
                            contentColor = if (isDark) DarkTextMuted else TextMuted
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Hủy", fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HistoryTransactionRow(
    tx: Transaction,
    isDark: Boolean,
    onLongClick: () -> Unit = {}
) {
    val timeFormat = SimpleDateFormat("HH:mm", Locale("vi", "VN"))
    val formattedTime = timeFormat.format(Date(tx.timestamp))

    val (icon, bg, tint) = getCategoryVisuals(tx.category, tx.isIncome, isDark)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .combinedClickable(
                onClick = {},
                onLongClick = onLongClick
            )
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon squircle badge
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(bg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = tx.category,
                tint = tint,
                modifier = Modifier.size(19.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // Center: Title & Subtitle
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (tx.note.isNotBlank()) tx.note else tx.category,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${tx.category} · ${tx.bankName}",
                fontSize = 11.5.sp,
                color = if (isDark) DarkTextMuted else TextMuted
            )
        }

        // Right: Amount and Time
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = (if (tx.isIncome) "+" else "−") + Formatters.formatMoney(tx.amount),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = if (tx.isIncome) PiggyGreen else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = formattedTime,
                fontSize = 10.5.sp,
                color = if (isDark) DarkTextSub else Color(0xFFA4AFBF)
            )
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
