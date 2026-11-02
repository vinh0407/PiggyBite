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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
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
import com.piggybite.app.ui.theme.PiggySurfaceDark
import com.piggybite.app.ui.theme.PiggySurfaceLight
import com.piggybite.app.ui.theme.PiggyViolet
import com.piggybite.app.ui.theme.PiggyVioletSoftDark
import com.piggybite.app.ui.theme.PiggyVioletSoftLight
import com.piggybite.app.ui.theme.TextMuted

@Composable
fun ActionButtonsRow(
    onScanQr: () -> Unit,
    onPasteStk: () -> Unit,
    onAddManual: () -> Unit,
    onMyQr: () -> Unit,
    onOpenNotificationSimulator: () -> Unit = {}
) {
    val isDark = LocalIsDarkMode.current

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Section Heading (Figma: section-heading)
        Column {
            Text(
                text = "TIỆN ÍCH",
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = if (isDark) Color(0xFF8997AA) else TextMuted,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Thao tác nhanh",
                fontSize = 16.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 2x2 Grid of Quick Actions (Figma: quick-grid)
        Column(
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Row 1: Quét VietQR + Dán số TK
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Quét VietQR",
                    subtitle = "Thanh toán ngay",
                    icon = Icons.Default.QrCodeScanner,
                    iconBg = if (isDark) PiggyPrimarySoftDark else PiggyPrimarySoftLight,
                    iconTint = PiggyPrimary,
                    onClick = onScanQr
                )

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Dán số TK",
                    subtitle = "AI tự nhận diện",
                    icon = Icons.Default.AutoAwesome,
                    iconBg = if (isDark) PiggyVioletSoftDark else PiggyVioletSoftLight,
                    iconTint = PiggyViolet,
                    onClick = onPasteStk
                )
            }

            // Row 2: Ghi thu chi + QR của tôi
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "Ghi thu chi",
                    subtitle = "Thêm thủ công",
                    icon = Icons.Default.Edit,
                    iconBg = if (isDark) PiggyOrangeSoftDark else PiggyOrangeSoftLight,
                    iconTint = PiggyOrange,
                    onClick = onAddManual
                )

                QuickActionCard(
                    modifier = Modifier.weight(1f),
                    title = "QR của tôi",
                    subtitle = "Nhận chuyển khoản",
                    icon = Icons.Default.QrCode,
                    iconBg = if (isDark) PiggyGreenSoftDark else PiggyGreenSoftLight,
                    iconTint = PiggyGreen,
                    onClick = onMyQr
                )
            }
        }
    }
}

@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    onClick: () -> Unit
) {
    val isDark = LocalIsDarkMode.current

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(if (isDark) PiggySurfaceDark else PiggySurfaceLight)
            .border(
                1.dp,
                if (isDark) PiggyBorderDark else PiggyBorderLight,
                RoundedCornerShape(16.dp)
            )
            .clickable { onClick() }
            .padding(13.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Colored Icon Pill
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(iconBg),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = subtitle,
                fontSize = 10.5.sp,
                color = if (isDark) DarkTextMuted else TextMuted
            )
        }

        Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = if (isDark) Color(0xFF5D6C80) else Color(0xFFA4AFBF),
            modifier = Modifier.size(14.dp)
        )
    }
}
