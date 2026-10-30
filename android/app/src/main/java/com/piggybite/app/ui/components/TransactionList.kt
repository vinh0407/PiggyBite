package com.piggybite.app.ui.components

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
import com.piggybite.app.model.Transaction
import com.piggybite.app.ui.theme.DarkButtonBg
import com.piggybite.app.ui.theme.DarkExpenseBg
import com.piggybite.app.ui.theme.DarkExpenseBorder
import com.piggybite.app.ui.theme.DarkExpenseText
import com.piggybite.app.ui.theme.DarkIncomeBg
import com.piggybite.app.ui.theme.DarkIncomeBorder
import com.piggybite.app.ui.theme.DarkIncomeText
import com.piggybite.app.ui.theme.ExpenseRed
import com.piggybite.app.ui.theme.ExpenseRedSoft
import com.piggybite.app.ui.theme.IncomeGreen
import com.piggybite.app.ui.theme.IncomeGreenSoft
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.SkyBlueSoft
import com.piggybite.app.ui.theme.TextMuted
import com.piggybite.app.util.Formatters

@Composable
fun TransactionList(
    transactions: List<Transaction>,
    onAddClick: () -> Unit
) {
    val isDark = LocalIsDarkMode.current

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "Gần đây",
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (transactions.isEmpty()) {
            // Minimalist Modern Empty State with Piggy Coin
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(24.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(24.dp))
                    .padding(vertical = 40.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(if (isDark) DarkButtonBg else SkyBlueSoft),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "🪙",
                        fontSize = 32.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "Ghi chép giao dịch đầu tiên",
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Chạm vào nút + để bắt đầu quản lý tài chính thông minh",
                    color = TextMuted,
                    fontSize = 12.5.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                transactions.take(8).forEach { tx ->
                    val iconBg = if (isDark) {
                        if (tx.isIncome) DarkIncomeBg else DarkExpenseBg
                    } else {
                        if (tx.isIncome) IncomeGreenSoft else ExpenseRedSoft
                    }
                    val iconBorder = if (isDark) {
                        if (tx.isIncome) DarkIncomeBorder else DarkExpenseBorder
                    } else {
                        Color.Transparent
                    }
                    val accentColor = if (isDark) {
                        if (tx.isIncome) DarkIncomeText else DarkExpenseText
                    } else {
                        if (tx.isIncome) IncomeGreen else ExpenseRed
                    }

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(18.dp))
                            .background(MaterialTheme.colorScheme.surface)
                            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(18.dp))
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(iconBg)
                                .border(if (isDark) 1.dp else 0.dp, iconBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (tx.isIncome) "↓" else "↑",
                                color = accentColor,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = tx.note.ifBlank { tx.category },
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "${tx.bankName} • ${tx.category} • ${Formatters.formatShortDate(tx.timestamp)}",
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }

                        Text(
                            text = if (tx.isIncome) "+${Formatters.formatMoney(tx.amount)}" else "−${Formatters.formatMoney(tx.amount)}",
                            color = accentColor,
                            fontSize = 14.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
