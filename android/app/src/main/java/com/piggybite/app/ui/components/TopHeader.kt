package com.piggybite.app.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piggybite.app.R
import com.piggybite.app.ui.theme.DarkCardBackground
import com.piggybite.app.ui.theme.DarkCardBorder
import com.piggybite.app.ui.theme.DarkTextMain
import com.piggybite.app.ui.theme.DarkTextMuted
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.PiggyAmber
import com.piggybite.app.ui.theme.PiggyBorderDark
import com.piggybite.app.ui.theme.PiggyBorderLight
import com.piggybite.app.ui.theme.PiggyPrimary
import com.piggybite.app.ui.theme.PiggySurfaceDark
import com.piggybite.app.ui.theme.PiggySurfaceLight
import com.piggybite.app.ui.theme.TextMuted
import com.piggybite.app.util.Formatters

@Composable
fun TopHeader(
    isDarkMode: Boolean,
    onToggleDarkMode: () -> Unit,
    onOpenNotificationSimulator: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Identity Brand Mark + Greetings & Date
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Brand Squircle Gradient Mark (Figma: brand-mark)
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(13.dp),
                        spotColor = PiggyPrimary.copy(alpha = 0.25f)
                    )
                    .clip(RoundedCornerShape(13.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF15A7EA), Color(0xFF0B72CA))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Savings,
                    contentDescription = "PiggyBite",
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.width(11.dp))

            Column {
                Text(
                    text = "PiggyBite",
                    fontSize = 17.5.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.4).sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = Formatters.getVietnameseTodayString(),
                    fontSize = 11.5.sp,
                    color = if (isDarkMode) DarkTextMuted else TextMuted
                )
            }
        }

        // Top Actions (Dark Mode Toggle + Profile Avatar)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Dark / Light Mode Button
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkMode) PiggySurfaceDark else PiggySurfaceLight)
                    .border(
                        1.dp,
                        if (isDarkMode) PiggyBorderDark else PiggyBorderLight,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onToggleDarkMode() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isDarkMode) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                    contentDescription = "Đổi giao diện",
                    tint = if (isDarkMode) PiggyAmber else PiggyPrimary,
                    modifier = Modifier.size(19.dp)
                )
            }

            // Notification Bell (Bản cũ: Mở thông báo số dư & Huấn luyện AI)
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDarkMode) PiggySurfaceDark else PiggySurfaceLight)
                    .border(
                        1.dp,
                        if (isDarkMode) PiggyBorderDark else PiggyBorderLight,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onOpenNotificationSimulator() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Notifications,
                    contentDescription = "Thông báo biến động số dư",
                    tint = if (isDarkMode) Color(0xFF38BDF8) else PiggyPrimary,
                    modifier = Modifier.size(20.dp)
                )
                // Pulse dot indicator
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .size(7.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF22C55E))
                )
            }
        }
    }
}
