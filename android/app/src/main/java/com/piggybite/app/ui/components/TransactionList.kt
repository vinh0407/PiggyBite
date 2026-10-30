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
import com.piggybite.app.ui.screens.HistoryTransactionRow
import com.piggybite.app.ui.theme.DarkButtonBg
import com.piggybite.app.ui.theme.DarkCardBorder
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
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val recent = transactions.take(6)
                recent.forEachIndexed { index, tx ->
                    HistoryTransactionRow(tx = tx, isDark = isDark)
                    if (index < recent.size - 1) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(0.6.dp)
                                .background(if (isDark) DarkCardBorder.copy(alpha = 0.5f) else Color(0xFFF3F4F6))
                        )
                    }
                }
            }
        }
    }
}
