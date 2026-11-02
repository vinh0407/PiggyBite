package com.piggybite.app.ui.dialogs

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocalAtm
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.piggybite.app.model.Transaction
import com.piggybite.app.ui.theme.PiggyGreen
import com.piggybite.app.ui.theme.PiggyOrange
import com.piggybite.app.ui.theme.PiggyPrimary
import com.piggybite.app.util.Formatters
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun MonthlyWrappedDialog(
    transactions: List<Transaction>,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Phân tích dữ liệu tháng hiện tại
    val now = Calendar.getInstance()
    val curMonth = now.get(Calendar.MONTH)
    val curYear = now.get(Calendar.YEAR)
    val monthTitle = "Tháng ${curMonth + 1}/${curYear}"

    val monthTransactions = remember(transactions) {
        transactions.filter { tx ->
            val cal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
            cal.get(Calendar.MONTH) == curMonth && cal.get(Calendar.YEAR) == curYear
        }
    }

    val totalIncome = remember(monthTransactions) {
        monthTransactions.filter { it.isIncome }.sumOf { it.amount }
    }
    val totalExpense = remember(monthTransactions) {
        monthTransactions.filter { !it.isIncome }.sumOf { it.amount }
    }

    // Tỷ lệ tiết kiệm = (Thu - Chi) / Thu * 100%
    val savingsRate = remember(totalIncome, totalExpense) {
        if (totalIncome > 0) {
            val rate = ((totalIncome - totalExpense).toFloat() / totalIncome) * 100
            rate.toInt()
        } else {
            0
        }
    }

    // Danh mục chi nhiều nhất
    val topCategory = remember(monthTransactions) {
        val expenseTxs = monthTransactions.filter { !it.isIncome }
        val grouped = expenseTxs.groupBy { it.category }
        grouped.maxByOrNull { entry -> entry.value.sumOf { it.amount } }
    }

    // Ngày chi tiêu nhiều nhất
    val topExpenseDay = remember(monthTransactions) {
        val expenseTxs = monthTransactions.filter { !it.isIncome }
        val dayFormat = SimpleDateFormat("EEEE, dd/MM", Locale("vi", "VN"))
        val groupedByDay = expenseTxs.groupBy { tx -> dayFormat.format(Date(tx.timestamp)) }
        groupedByDay.maxByOrNull { entry -> entry.value.sumOf { it.amount } }
    }

    // Danh hiệu tài chính
    val badgeTitle = remember(savingsRate, totalExpense) {
        when {
            savingsRate >= 35 -> "🏆 BẬC THẦY TÍCH LŨY"
            savingsRate in 15..34 -> "🌟 CHI TIÊU THÔNG THÁI"
            savingsRate in 0..14 -> "⚖️ CÂN BẰNG TÀI CHÍNH"
            else -> "⚡ CẦN CHÚ Ý CẮT GIẢM"
        }
    }
    val badgeSubtitle = remember(savingsRate) {
        when {
            savingsRate >= 35 -> "Bạn đang tích lũy cực kỳ xuất sắc, hãy tiếp tục duy trì!"
            savingsRate in 15..34 -> "Tỷ lệ chi tiêu và tiết kiệm của bạn rất lành mạnh."
            savingsRate in 0..14 -> "Mức tiết kiệm vừa phải, có thể tối ưu thêm các khoản ăn uống mua sắm."
            else -> "Chi tiêu tháng này đang vượt quá thu nhập, hãy xem lại các khoản chi lớn."
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .border(1.dp, Color(0xFF38BDF8).copy(alpha = 0.35f), RoundedCornerShape(28.dp)),
                color = Color(0xFF0A1120)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                        .padding(22.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Header Bar (Close button + Title)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color(0xFF38BDF8),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "PIGGYBITE WRAPPED",
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF38BDF8),
                                letterSpacing = 1.2.sp
                            )
                        }

                        IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Đóng",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Tổng kết $monthTitle",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Badge Banner (Danh hiệu)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF1E3A8A), Color(0xFF0284C7))
                                )
                            )
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = badgeTitle,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White,
                                letterSpacing = 0.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = badgeSubtitle,
                                fontSize = 11.5.sp,
                                color = Color(0xFFE0F2FE),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Highlights Grid
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // 1. Tỷ lệ tiết kiệm
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF131D2E))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(PiggyGreen.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = PiggyGreen,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "TỶ LỆ TIẾT KIỆM",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8)
                                )
                                Text(
                                    text = if (savingsRate >= 0) "+$savingsRate%" else "$savingsRate%",
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = if (savingsRate >= 0) PiggyGreen else Color(0xFFF43F5E)
                                )
                            }
                        }

                        // 2. Danh mục tiêu tốn nhất
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF131D2E))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(PiggyOrange.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Restaurant,
                                    contentDescription = null,
                                    tint = PiggyOrange,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "TIÊU TỐN NHIỀU NHẤT",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8)
                                )
                                val topCatName = topCategory?.key ?: "Chưa có"
                                val topCatAmount = topCategory?.value?.sumOf { it.amount } ?: 0L
                                Text(
                                    text = "$topCatName • ${Formatters.formatMoney(topCatAmount)}",
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        // 3. Ngày chi tiêu đỉnh điểm
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF131D2E))
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF38BDF8).copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CalendarMonth,
                                    contentDescription = null,
                                    tint = Color(0xFF38BDF8),
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "NGÀY CHI TIÊU ĐỈNH ĐIỂM",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF94A3B8)
                                )
                                val topDayName = topExpenseDay?.key ?: "Chưa có"
                                val topDayAmount = topExpenseDay?.value?.sumOf { it.amount } ?: 0L
                                Text(
                                    text = "$topDayName • ${Formatters.formatMoney(topDayAmount)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(22.dp))

                    // Nút Chia sẻ Infographic
                    Button(
                        onClick = {
                            val shareText = """
                                📊 PIGGYBITE WRAPPED • $monthTitle
                                🏆 Danh hiệu: $badgeTitle
                                📈 Tỷ lệ tiết kiệm: $savingsRate%
                                🍽️ Chi nhiều nhất: ${topCategory?.key ?: "N/A"} (${Formatters.formatMoney(topCategory?.value?.sumOf { it.amount } ?: 0L)})
                                📅 Ngày chi đỉnh điểm: ${topExpenseDay?.key ?: "N/A"}
                                💵 Tổng thu: +${Formatters.formatMoney(totalIncome)}
                                💸 Tổng chi: -${Formatters.formatMoney(totalExpense)}
                                ---
                                Quản lý tài chính cá nhân an toàn, 100% Offline với PiggyBite!
                            """.trimIndent()

                            val sendIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, "PiggyBite Wrapped $monthTitle")
                                putExtra(Intent.EXTRA_TEXT, shareText)
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Chia sẻ tổng kết tài chính"))
                        },
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PiggyPrimary,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 13.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(19.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Chia sẻ Infographic",
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
