package com.piggybite.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.piggybite.app.model.BankAccount
import com.piggybite.app.model.Transaction
import com.piggybite.app.ui.components.ActionButtonsRow
import com.piggybite.app.ui.components.TopHeader
import com.piggybite.app.ui.components.TransactionList
import com.piggybite.app.ui.components.WalletSummaryCard
import com.piggybite.app.ui.theme.LocalIsDarkMode
import java.util.Calendar

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
    onPasteStk: () -> Unit,
    onAddManual: () -> Unit,
    onMyQr: () -> Unit,
    onOpenWeeklyReview: () -> Unit = {},
    onOpenNotificationSimulator: () -> Unit = {},
    onAddBank: () -> Unit,
    onEditAccount: (BankAccount) -> Unit,
    onManageAllWallets: () -> Unit,
    onSeeAllTransactions: () -> Unit = onManageAllWallets
) {
    val scrollState = rememberScrollState()
    val isDark = LocalIsDarkMode.current

    // Tính toán tổng thu và tổng chi của tháng hiện tại
    val (monthIncome, monthExpense) = remember(transactions) {
        val now = Calendar.getInstance()
        val curMonth = now.get(Calendar.MONTH)
        val curYear = now.get(Calendar.YEAR)

        var inc = 0L
        var exp = 0L

        transactions.forEach { tx ->
            val txCal = Calendar.getInstance().apply { timeInMillis = tx.timestamp }
            if (txCal.get(Calendar.MONTH) == curMonth && txCal.get(Calendar.YEAR) == curYear) {
                if (tx.isIncome) inc += tx.amount else exp += tx.amount
            }
        }
        Pair(inc, exp)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header (Brand Logo + Vietnamese Date + Theme Switcher)
        TopHeader(
            isDarkMode = isDarkMode,
            onToggleDarkMode = onToggleDarkMode,
            onOpenNotificationSimulator = onOpenNotificationSimulator
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // 1. Thẻ Hero Tổng số dư & Dải thẻ Ví của tôi (Figma: balance-card + wallet-grid)
            WalletSummaryCard(
                accounts = accounts,
                totalBalance = totalBalance,
                isBalanceHidden = isBalanceHidden,
                monthIncome = monthIncome,
                monthExpense = monthExpense,
                onToggleHideBalance = onToggleHideBalance,
                onAddBank = onAddBank,
                onEditAccount = onEditAccount,
                onManageAll = onManageAllWallets,
                onScanQr = onScanQr
            )

            // 2. Tiện ích Thao tác nhanh (Figma: quick-grid 4 phím: Quét QR, Dán STK, Ghi thu chi, QR tôi)
            ActionButtonsRow(
                onScanQr = onScanQr,
                onPasteStk = onPasteStk,
                onAddManual = onAddManual,
                onMyQr = onMyQr,
                onOpenNotificationSimulator = onOpenNotificationSimulator
            )

            // 3. Danh sách giao dịch gần đây (Figma: recent-section)
            TransactionList(
                transactions = transactions,
                onAddClick = onAddManual,
                onSeeAllClick = onSeeAllTransactions
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
