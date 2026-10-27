package com.money.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material.icons.outlined.Settings
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.money.app.ui.theme.DarkTextSub
import com.money.app.ui.theme.DarkTitleBlue
import com.money.app.ui.theme.LocalIsDarkMode
import com.money.app.ui.theme.SkyBluePrimary
import com.money.app.ui.theme.SkyBlueVibrant
import com.money.app.ui.theme.TextSub

enum class NavTab(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    CALENDAR("Lịch", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    ANALYTICS("Phân tích", Icons.Filled.PieChart, Icons.Outlined.PieChart),
    HOME("Trang chủ", Icons.Filled.Home, Icons.Outlined.Home), // Centered!
    WALLET("Ví / Tài sản", Icons.Filled.AccountBalanceWallet, Icons.Outlined.AccountBalanceWallet),
    SETTINGS("Cài đặt", Icons.Filled.Settings, Icons.Outlined.Settings)
}

@Composable
fun BottomNavBar(
    currentTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkMode.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        // Floating Pill Shaped Container
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(16.dp, RoundedCornerShape(36.dp), spotColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.12f))
                .clip(RoundedCornerShape(36.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(36.dp))
                .padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val orderedTabs = listOf(
                NavTab.CALENDAR,
                NavTab.ANALYTICS,
                NavTab.HOME,
                NavTab.WALLET,
                NavTab.SETTINGS
            )

            orderedTabs.forEach { tab ->
                val isSelected = currentTab == tab

                if (tab == NavTab.HOME) {
                    // Center Highlighted Home Pill Button
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .shadow(8.dp, CircleShape, spotColor = SkyBluePrimary.copy(alpha = 0.4f))
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(SkyBluePrimary, SkyBlueVibrant)
                                )
                            )
                            .clickable { onTabSelected(NavTab.HOME) },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Home,
                            contentDescription = "Trang chủ",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .clickable { onTabSelected(tab) }
                            .padding(vertical = 4.dp, horizontal = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val activeColor = if (isDark) DarkTitleBlue else SkyBluePrimary
                        val inactiveColor = if (isDark) DarkTextSub else TextSub

                        Icon(
                            imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                            contentDescription = tab.title,
                            tint = if (isSelected) activeColor else inactiveColor,
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = tab.title,
                            color = if (isSelected) activeColor else inactiveColor,
                            fontSize = 10.5.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}
