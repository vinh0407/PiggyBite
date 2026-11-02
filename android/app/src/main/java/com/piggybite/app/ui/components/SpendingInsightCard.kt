package com.piggybite.app.ui.components

import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piggybite.app.ui.theme.DarkTextMuted
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.PiggyBorderDark
import com.piggybite.app.ui.theme.PiggyBorderLight
import com.piggybite.app.ui.theme.PiggyGreen
import com.piggybite.app.ui.theme.PiggyGreenSoftDark
import com.piggybite.app.ui.theme.PiggyGreenSoftLight
import com.piggybite.app.ui.theme.PiggyPrimary
import com.piggybite.app.ui.theme.PiggyPrimaryDark
import com.piggybite.app.ui.theme.PiggyPrimarySoftDark
import com.piggybite.app.ui.theme.PiggyPrimarySoftLight
import com.piggybite.app.ui.theme.PiggySurfaceDark
import com.piggybite.app.ui.theme.PiggySurfaceLight
import com.piggybite.app.ui.theme.TextMuted
import com.piggybite.app.util.Formatters

@Composable
fun SpendingInsightCard(
    monthExpense: Long,
    totalBalance: Long
) {
    val isDark = LocalIsDarkMode.current

    // Giả định ngân sách hợp lý = Chi tiêu + Số dư (hoặc tối thiểu 10 triệu)
    val budget = maxOf(10_000_000L, monthExpense + maxOf(0L, totalBalance / 2))
    val remaining = maxOf(0L, budget - monthExpense)
    val percentage = if (budget > 0) ((monthExpense.toFloat() / budget) * 100).toInt().coerceIn(0, 100) else 0

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. SPENDING CARD (Figma: spending-card)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(22.dp))
                .background(if (isDark) PiggySurfaceDark else PiggySurfaceLight)
                .border(
                    1.dp,
                    if (isDark) PiggyBorderDark else PiggyBorderLight,
                    RoundedCornerShape(22.dp)
                )
                .padding(20.dp)
        ) {
            // Header: Kicker + Title + Trend Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "NGÂN SÁCH THÁNG NÀY",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF8997AA) else TextMuted,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Chi tiêu của bạn",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Trend badge (-8.2%)
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(999.dp))
                        .background(if (isDark) PiggyGreenSoftDark else PiggyGreenSoftLight)
                        .padding(horizontal = 9.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowDownward,
                        contentDescription = null,
                        tint = PiggyGreen,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = "8.2%",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = PiggyGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Chart area: Donut + Legend
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Donut Circular Chart
                Box(
                    modifier = Modifier.size(88.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(88.dp)) {
                        val strokeWidth = 10.dp.toPx()
                        // Background track
                        drawCircle(
                            color = if (isDark) Color(0xFF1E2B3E) else Color(0xFFDAE4EE),
                            style = Stroke(width = strokeWidth)
                        )
                        // Progress arc
                        drawArc(
                            color = PiggyPrimary,
                            startAngle = -90f,
                            sweepAngle = (percentage / 100f) * 360f,
                            useCenter = false,
                            style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$percentage%",
                            fontSize = 16.5.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "đã dùng",
                            fontSize = 9.sp,
                            color = if (isDark) DarkTextMuted else TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.width(22.dp))

                // Chart Legend
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(PiggyPrimary)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Đã chi",
                                fontSize = 10.sp,
                                color = if (isDark) DarkTextMuted else TextMuted
                            )
                            Text(
                                text = Formatters.formatMoney(monthExpense),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(if (isDark) Color(0xFF2A3A50) else Color(0xFFD8E3ED))
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "Còn lại",
                                fontSize = 10.sp,
                                color = if (isDark) DarkTextMuted else TextMuted
                            )
                            Text(
                                text = Formatters.formatMoney(remaining),
                                fontSize = 12.5.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // AI Insight Pill (Figma: insight)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) PiggyPrimarySoftDark else PiggyPrimarySoftLight)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = PiggyPrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Dữ liệu offline an toàn. Bạn đang kiểm soát ngân sách rất tốt!",
                    fontSize = 11.5.sp,
                    color = if (isDark) Color(0xFF7DD3FC) else PiggyPrimaryDark,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        // 2. DAILY FINANCIAL TIP CARD (Figma: tip-card Quy tắc 50/30/20)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(20.dp))
                .background(
                    if (isDark) {
                        Brush.linearGradient(listOf(Color(0xFF31291C), Color(0xFF1E1B16)))
                    } else {
                        Brush.linearGradient(listOf(Color(0xFFF8EDDA), Color(0xFFFFF9EE)))
                    }
                )
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(11.dp))
                    .background(if (isDark) Color(0xFF4A381C) else Color(0xFFF1CF8E)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Bolt,
                    contentDescription = null,
                    tint = Color(0xFFA76A09),
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = "MẸO NHỎ HÔM NAY",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFA76A09),
                    letterSpacing = 0.8.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Quy tắc tài chính 50/30/20",
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFFF1DFBF) else Color(0xFF48351D)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Dành 50% thu nhập cho nhu cầu thiết yếu, 30% cho mong muốn cá nhân và 20% tích lũy tiết kiệm.",
                    fontSize = 11.5.sp,
                    lineHeight = 16.sp,
                    color = if (isDark) Color(0xFFD4C2A3) else Color(0xFF6B5336)
                )
            }
        }
    }
}
