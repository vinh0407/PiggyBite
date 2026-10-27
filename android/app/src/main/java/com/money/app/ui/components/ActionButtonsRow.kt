package com.money.app.ui.components

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.money.app.ui.theme.CardBackground
import com.money.app.ui.theme.CardBorder
import com.money.app.ui.theme.DarkButtonBg
import com.money.app.ui.theme.DarkButtonBorder
import com.money.app.ui.theme.DarkButtonIconBg
import com.money.app.ui.theme.DarkButtonText
import com.money.app.ui.theme.DarkTitleBlue
import com.money.app.ui.theme.LocalIsDarkMode
import com.money.app.ui.theme.SkyBluePrimary
import com.money.app.ui.theme.SkyBlueSoft
import com.money.app.ui.theme.SkyBlueVibrant
import com.money.app.ui.theme.TextDeepNavy
import com.money.app.ui.theme.TextMuted

@Composable
fun ActionButtonsRow(
    onScanQr: () -> Unit,
    onAddManual: () -> Unit,
    onMyQr: () -> Unit,
    onOpenNotificationSimulator: () -> Unit
) {
    val isDark = LocalIsDarkMode.current

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // Modern Sky Blue QR Payment Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .shadow(6.dp, RoundedCornerShape(22.dp), spotColor = SkyBluePrimary.copy(alpha = 0.35f))
                .clip(RoundedCornerShape(22.dp))
                .background(
                    Brush.horizontalGradient(
                        colors = if (isDark) {
                            listOf(androidx.compose.ui.graphics.Color(0xFF0369A1), androidx.compose.ui.graphics.Color(0xFF0284C7))
                        } else {
                            listOf(SkyBluePrimary, SkyBlueVibrant)
                        }
                    )
                )
                .border(
                    1.dp,
                    if (isDark) DarkTitleBlue.copy(alpha = 0.3f) else androidx.compose.ui.graphics.Color.Transparent,
                    RoundedCornerShape(22.dp)
                )
                .clickable { onScanQr() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.QrCodeScanner,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Quét QR thanh toán",
                    color = Color.White,
                    fontSize = 16.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Sub Actions Row: "+ Ghi tay" & "QR của tôi"
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // + Ghi tay (Tối ưu nút màu tối, sắc nét)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDark) DarkButtonBg else CardBackground)
                    .border(1.dp, if (isDark) DarkButtonBorder else CardBorder, RoundedCornerShape(18.dp))
                    .clickable { onAddManual() }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isDark) DarkButtonIconBg else SkyBlueSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Ghi tay",
                    color = if (isDark) DarkButtonText else TextDeepNavy,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // QR của tôi (Tối ưu nút màu tối, sắc nét)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .height(50.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDark) DarkButtonBg else CardBackground)
                    .border(1.dp, if (isDark) DarkButtonBorder else CardBorder, RoundedCornerShape(18.dp))
                    .clickable { onMyQr() }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(if (isDark) DarkButtonIconBg else SkyBlueSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "QR của tôi",
                    color = if (isDark) DarkButtonText else TextDeepNavy,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Auto-Notification Sync Pill (Tối ưu nút màu tối, sắc nét)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(if (isDark) DarkButtonBg else CardBackground)
                .border(1.dp, if (isDark) DarkButtonBorder else CardBorder, RoundedCornerShape(16.dp))
                .clickable { onOpenNotificationSimulator() }
                .padding(horizontal = 14.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(9.dp)
                    .clip(CircleShape)
                    .background(if (isDark) DarkTitleBlue else SkyBluePrimary)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = "Tự động cập nhật số dư từ SMS & App ngân hàng",
                color = if (isDark) DarkButtonText else TextDeepNavy,
                fontSize = 12.5.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = "›",
                color = if (isDark) DarkTitleBlue else TextMuted,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
