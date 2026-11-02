package com.piggybite.app.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piggybite.app.ui.theme.DarkCardBackground
import com.piggybite.app.ui.theme.DarkCardBorder
import com.piggybite.app.ui.theme.DarkTextMuted
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.PiggyPrimary
import com.piggybite.app.ui.theme.PiggyPrimarySoftDark
import com.piggybite.app.ui.theme.PiggyPrimarySoftLight
import com.piggybite.app.ui.theme.TextMuted

enum class NavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("Tổng quan", Icons.Filled.Home, Icons.Outlined.Home),
    HISTORY("Lịch sử", Icons.Filled.Schedule, Icons.Outlined.Schedule),
    SCAN_QR("Quét QR", Icons.Filled.Home, Icons.Outlined.Home), // Giữ lại enum để tương thích
    REPORTS("Báo cáo", Icons.Filled.PieChart, Icons.Outlined.PieChart),
    SETTINGS("Cài đặt", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun BottomNavBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    onScanQrClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkMode.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 18.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating Nav Pill (Chuẩn giao diện Figma PiggyBite)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 18.dp,
                    shape = RoundedCornerShape(26.dp),
                    spotColor = if (isDark) Color(0xFF03070E) else Color(0xFF0E85D4).copy(alpha = 0.12f)
                )
                .clip(RoundedCornerShape(26.dp))
                .background(if (isDark) DarkCardBackground else MaterialTheme.colorScheme.surface)
                .border(1.dp, if (isDark) DarkCardBorder else MaterialTheme.colorScheme.outline, RoundedCornerShape(26.dp))
                .padding(horizontal = 8.dp, vertical = 7.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val leftTabs = listOf(
                NavTab.HOME,
                NavTab.HISTORY
            )
            val rightTabs = listOf(
                NavTab.REPORTS,
                NavTab.SETTINGS
            )

            // 1. Nhóm 2 tab bên trái (Tổng quan, Lịch sử)
            leftTabs.forEach { tab ->
                val isSelected = currentTab == tab

                val tintColor by animateColorAsState(
                    targetValue = if (isSelected) PiggyPrimary else if (isDark) DarkTextMuted else TextMuted,
                    animationSpec = tween(180),
                    label = "tabTint"
                )

                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        if (isDark) PiggyPrimarySoftDark else PiggyPrimarySoftLight
                    } else Color.Transparent,
                    animationSpec = tween(180),
                    label = "tabBg"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(bgColor)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.title,
                            tint = tintColor,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = tab.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = tintColor,
                            letterSpacing = (-0.2).sp
                        )
                    }
                }
            }

            // 2. NÚT QUÉT QR Ở CHÍNH GIỮA 4 NÚT
            Box(
                modifier = Modifier
                    .weight(1.1f)
                    .clickable { onScanQrClick() }
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .shadow(
                                elevation = 6.dp,
                                shape = RoundedCornerShape(12.dp),
                                spotColor = PiggyPrimary.copy(alpha = 0.35f)
                            )
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF15A7EA), Color(0xFF0B72CA))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.QrCodeScanner,
                            contentDescription = "Quét QR",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = "Quét QR",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isDark) Color(0xFF38BDF8) else PiggyPrimary,
                        letterSpacing = (-0.2).sp
                    )
                }
            }

            // 3. Nhóm 2 tab bên phải (Báo cáo, Cài đặt)
            rightTabs.forEach { tab ->
                val isSelected = currentTab == tab

                val tintColor by animateColorAsState(
                    targetValue = if (isSelected) PiggyPrimary else if (isDark) DarkTextMuted else TextMuted,
                    animationSpec = tween(180),
                    label = "tabTint"
                )

                val bgColor by animateColorAsState(
                    targetValue = if (isSelected) {
                        if (isDark) PiggyPrimarySoftDark else PiggyPrimarySoftLight
                    } else Color.Transparent,
                    animationSpec = tween(180),
                    label = "tabBg"
                )

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(16.dp))
                        .background(bgColor)
                        .clickable { onTabSelected(tab) }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.title,
                            tint = tintColor,
                            modifier = Modifier.size(20.dp)
                        )

                        Spacer(modifier = Modifier.height(3.dp))

                        Text(
                            text = tab.title,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = tintColor,
                            letterSpacing = (-0.2).sp
                        )
                    }
                }
            }
        }
    }
}
