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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.piggybite.app.model.BankAccount
import com.piggybite.app.ui.theme.DarkButtonBg
import com.piggybite.app.ui.theme.DarkButtonBorder
import com.piggybite.app.ui.theme.DarkTitleBlue
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.SkyBluePrimary
import com.piggybite.app.ui.theme.SkyBlueSoft
import com.piggybite.app.ui.theme.TextMuted
import com.piggybite.app.util.Formatters

@Composable
fun WalletSummaryCard(
    accounts: List<BankAccount>,
    totalBalance: Long,
    isBalanceHidden: Boolean,
    onToggleHideBalance: () -> Unit,
    onAddBank: () -> Unit,
    onEditAccount: (BankAccount) -> Unit,
    onManageAll: () -> Unit
) {
    val isDark = LocalIsDarkMode.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(26.dp))
            .padding(20.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tổng số dư tài khoản",
                        color = TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.2.sp
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .clickable { onToggleHideBalance() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isBalanceHidden) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (isBalanceHidden) "Hiện số dư" else "Ẩn số dư",
                            tint = TextMuted,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = if (isBalanceHidden) "•••••••• đ" else Formatters.formatMoney(totalBalance),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Manage All button
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (isDark) DarkButtonBg else SkyBlueSoft)
                    .border(
                        if (isDark) 1.dp else 0.dp,
                        if (isDark) DarkButtonBorder else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .clickable { onManageAll() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quản lý ví",
                    color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Accounts list with OFFICIAL LOGOS
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (accounts.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Chưa có tài khoản ngân hàng nào",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Thêm ngân hàng hoặc ví đầu tiên để bắt đầu quản lý",
                        color = TextMuted,
                        fontSize = 11.5.sp
                    )
                }
            } else {
                accounts.take(4).forEach { acc ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(16.dp))
                            .clickable { onEditAccount(acc) }
                            .padding(horizontal = 14.dp, vertical = 11.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                    // REAL BANK LOGO
                    BankLogoView(
                        bankId = acc.bankId,
                        size = 38.dp
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = acc.bankName,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (acc.accountNumber.isNotBlank()) {
                            Text(
                                text = "STK: ${acc.accountNumber}",
                                color = TextMuted,
                                fontSize = 11.5.sp
                            )
                        }
                    }

                    Text(
                        text = if (isBalanceHidden) "•••••• đ" else Formatters.formatMoney(acc.balance),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

            // Quick Add Bank Button
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(if (isDark) DarkButtonBg else Color.Transparent)
                    .border(
                        1.dp,
                        if (isDark) DarkButtonBorder else MaterialTheme.colorScheme.outline,
                        RoundedCornerShape(16.dp)
                    )
                    .clickable { onAddBank() }
                    .padding(vertical = 11.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null,
                    tint = if (isDark) DarkTitleBlue else SkyBluePrimary,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Thêm ngân hàng / Ví điện tử",
                    color = if (isDark) DarkTitleBlue else SkyBluePrimary,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
