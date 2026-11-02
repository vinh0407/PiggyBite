package com.piggybite.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Coffee
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piggybite.app.model.Transaction
import com.piggybite.app.ui.theme.DarkTextMuted
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
import com.piggybite.app.ui.theme.PiggyViolet
import com.piggybite.app.ui.theme.PiggyVioletSoftDark
import com.piggybite.app.ui.theme.PiggyVioletSoftLight
import com.piggybite.app.ui.theme.TextMuted
import com.piggybite.app.util.Formatters

@Composable
fun TransactionList(
    transactions: List<Transaction>,
    onAddClick: () -> Unit,
    onSeeAllClick: () -> Unit = {}
) {
    val isDark = LocalIsDarkMode.current

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
            .padding(18.dp)
    ) {
        // Section Header (Figma: section-heading)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "MỚI NHẤT",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFF8997AA) else TextMuted,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Giao dịch gần đây",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            Row(
                modifier = Modifier
                    .clickable { onSeeAllClick() }
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Xem tất cả",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = PiggyPrimary
                )
                Spacer(modifier = Modifier.width(3.dp))
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = PiggyPrimary,
                    modifier = Modifier.size(13.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (transactions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(if (isDark) PiggyPrimarySoftDark else PiggyPrimarySoftLight),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🪙", fontSize = 24.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Chưa có giao dịch nào",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Ghi chép giao dịch đầu tiên để bắt đầu theo dõi",
                    fontSize = 11.5.sp,
                    color = if (isDark) DarkTextMuted else TextMuted
                )
            }
        } else {
            val displayItems = transactions.take(5)
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                displayItems.forEachIndexed { index, tx ->
                    TransactionRowItem(tx = tx)
                    if (index < displayItems.size - 1) {
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
}

@Composable
fun TransactionRowItem(tx: Transaction) {
    val isDark = LocalIsDarkMode.current

    val (icon, bg, tint) = getCategoryVisuals(tx.category, tx.isIncome, isDark)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon squircle badge (Figma: transaction-icon)
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

        // Main info: Title + Meta
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = if (tx.note.isNotBlank()) tx.note else tx.category,
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${tx.category} · ${tx.bankName}",
                fontSize = 11.sp,
                color = if (isDark) DarkTextMuted else TextMuted
            )
        }

        // Amount + Time (Figma: transaction-amount)
        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = (if (tx.isIncome) "+" else "−") + Formatters.formatMoney(tx.amount),
                fontSize = 13.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (tx.isIncome) PiggyGreen else MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = Formatters.formatTime(tx.timestamp),
                fontSize = 10.5.sp,
                color = if (isDark) DarkTextMuted else TextMuted
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
