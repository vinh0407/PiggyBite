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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.money.app.ui.theme.DarkExpenseBg
import com.money.app.ui.theme.DarkExpenseBorder
import com.money.app.ui.theme.DarkExpenseText
import com.money.app.ui.theme.DarkIncomeBg
import com.money.app.ui.theme.DarkIncomeBorder
import com.money.app.ui.theme.DarkIncomeText
import com.money.app.ui.theme.DarkTextMuted
import com.money.app.ui.theme.DarkTitleBlue
import com.money.app.ui.theme.ExpenseRed
import com.money.app.ui.theme.ExpenseRedSoft
import com.money.app.ui.theme.IncomeGreen
import com.money.app.ui.theme.IncomeGreenSoft
import com.money.app.ui.theme.LocalIsDarkMode
import com.money.app.ui.theme.SkyBluePrimary
import com.money.app.ui.theme.SkyBlueSoft
import com.money.app.ui.theme.TextMuted
import com.money.app.util.Formatters

@Composable
fun ExpenseCard(
    todayExpense: Long,
    todayIncome: Long,
    isBalanceHidden: Boolean,
    onToggleHideBalance: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = LocalIsDarkMode.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(26.dp), spotColor = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.06f))
            .clip(RoundedCornerShape(26.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(26.dp))
            .padding(22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Chi tiêu tháng này",
                    color = if (isDark) DarkTextMuted else TextMuted,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.width(8.dp))

                // Eye Toggle Button
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable { onToggleHideBalance() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (isBalanceHidden) "Hiện số dư" else "Ẩn số dư",
                        tint = if (isDark) DarkTextMuted else TextMuted,
                        modifier = Modifier.size(17.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isDark) androidx.compose.ui.graphics.Color(0xFF1E293B) else SkyBlueSoft)
                    .border(
                        1.dp,
                        if (isDark) androidx.compose.ui.graphics.Color(0xFF334155) else androidx.compose.ui.graphics.Color.Transparent,
                        RoundedCornerShape(10.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "Tháng 10",
                    color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isBalanceHidden) "•••••••• đ" else Formatters.formatMoney(todayExpense),
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 32.sp,
            fontWeight = FontWeight.ExtraBold
        )

        Spacer(modifier = Modifier.height(18.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Chi tiêu pill (Được tối ưu cho Dark Mode: màu tối dịu mắt, không chói)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) DarkExpenseBg else ExpenseRedSoft)
                    .border(
                        1.dp,
                        if (isDark) DarkExpenseBorder else androidx.compose.ui.graphics.Color.Transparent,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Chi tiêu",
                        color = if (isDark) DarkTextMuted else TextMuted,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (isBalanceHidden) "•••• đ" else (if (todayExpense > 0) "−${Formatters.formatMoney(todayExpense)}" else "−0 đ"),
                        color = if (isDark) DarkExpenseText else ExpenseRed,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Thu nhập pill (Được tối ưu cho Dark Mode: màu tối dịu mắt, không chói)
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) DarkIncomeBg else IncomeGreenSoft)
                    .border(
                        1.dp,
                        if (isDark) DarkIncomeBorder else androidx.compose.ui.graphics.Color.Transparent,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Thu nhập",
                        color = if (isDark) DarkTextMuted else TextMuted,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = if (isBalanceHidden) "•••• đ" else (if (todayIncome > 0) "+${Formatters.formatMoney(todayIncome)}" else "+0 đ"),
                        color = if (isDark) DarkIncomeText else IncomeGreen,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
