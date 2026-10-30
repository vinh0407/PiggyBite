package com.piggybite.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.CheckCircle
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
import com.piggybite.app.model.Transaction
import com.piggybite.app.ui.components.ActionButtonsRow
import com.piggybite.app.ui.components.ExpenseCard
import com.piggybite.app.ui.components.TopHeader
import com.piggybite.app.ui.components.TransactionList
import com.piggybite.app.ui.components.WalletSummaryCard
import com.piggybite.app.ui.theme.DarkButtonBg
import com.piggybite.app.ui.theme.DarkButtonBorder
import com.piggybite.app.ui.theme.DarkCardBackground
import com.piggybite.app.ui.theme.DarkCardBorder
import com.piggybite.app.ui.theme.DarkTextMain
import com.piggybite.app.ui.theme.DarkTextMuted
import com.piggybite.app.ui.theme.DarkTitleBlue
import com.piggybite.app.ui.theme.LocalIsDarkMode
import com.piggybite.app.ui.theme.NepGreenPrimary
import com.piggybite.app.ui.theme.SkyBluePrimary

@Composable
fun HomeScreen(
    todayExpense: Long,
    todayIncome: Long,
    totalBalance: Long,
    isBalanceHidden: Boolean,
    isDarkMode: Boolean,
    onToggleHideBalance: () -> Unit,
    onToggleDarkMode: () -> Unit,
    accounts: List<BankAccount>,
    transactions: List<Transaction>,
    onScanQr: () -> Unit,
    onAddManual: () -> Unit,
    onMyQr: () -> Unit,
    onOpenWeeklyReview: () -> Unit,
    onOpenNotificationSimulator: () -> Unit,
    onAddBank: () -> Unit,
    onEditAccount: (BankAccount) -> Unit,
    onManageAllWallets: () -> Unit
) {
    val scrollState = rememberScrollState()
    val isDark = LocalIsDarkMode.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        TopHeader(
            isDarkMode = isDarkMode,
            onToggleDarkMode = onToggleDarkMode,
            onOpenNotificationSimulator = onOpenNotificationSimulator
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nếp Main Expense & Remaining Budget Card
            ExpenseCard(
                todayExpense = todayExpense,
                todayIncome = todayIncome,
                remainingBudget = if (totalBalance > 0) totalBalance else 19784000L,
                monthExpense = 1750000L + todayExpense,
                isBalanceHidden = isBalanceHidden,
                onToggleHideBalance = onToggleHideBalance
            )

            // Primary QR Payment & Action Buttons (Quét QR thanh toán + Ghi tay)
            ActionButtonsRow(
                onScanQr = onScanQr,
                onAddManual = onAddManual,
                onMyQr = onMyQr,
                onOpenNotificationSimulator = onOpenNotificationSimulator
            )

            // "Mỗi tuần, khép sổ một lần" Banner (Exact Nếp Screenshot 4 logic)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (isDark) DarkCardBackground else Color(0xFFF0FDF4))
                    .border(1.dp, if (isDark) DarkCardBorder else Color(0xFFBBF7D0), RoundedCornerShape(18.dp))
                    .clickable { onOpenWeeklyReview() }
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(if (isDark) DarkButtonBg else Color(0xFFDCFCE7)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isDark) DarkTitleBlue else Color(0xFF16A34A),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Mỗi tuần, khép sổ một lần",
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Bù khoản tiền mặt còn sót, biết tuần qua chi bao nhiêu",
                        fontSize = 11.5.sp,
                        color = if (isDark) DarkTextMuted else Color(0xFF6B7280)
                    )
                }
                Text(
                    text = "Khép sổ ›",
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) DarkTitleBlue else Color(0xFF16A34A)
                )
            }

            // Wallet Summary Card (Aggregated accounts)
            WalletSummaryCard(
                accounts = accounts,
                totalBalance = totalBalance,
                isBalanceHidden = isBalanceHidden,
                onToggleHideBalance = onToggleHideBalance,
                onAddBank = onAddBank,
                onEditAccount = onEditAccount,
                onManageAll = onManageAllWallets
            )

            // Recent Transactions (Gần đây)
            TransactionList(
                transactions = transactions,
                onAddClick = onAddManual
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
