package com.money.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.money.app.model.BankAccount
import com.money.app.model.Transaction
import com.money.app.ui.components.ActionButtonsRow
import com.money.app.ui.components.ExpenseCard
import com.money.app.ui.components.TopHeader
import com.money.app.ui.components.TransactionList
import com.money.app.ui.components.WalletSummaryCard

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
    onOpenNotificationSimulator: () -> Unit,
    onAddBank: () -> Unit,
    onEditAccount: (BankAccount) -> Unit,
    onManageAllWallets: () -> Unit
) {
    val scrollState = rememberScrollState()

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
            // Expense Card
            ExpenseCard(
                todayExpense = todayExpense,
                todayIncome = todayIncome,
                isBalanceHidden = isBalanceHidden,
                onToggleHideBalance = onToggleHideBalance
            )

            // Primary QR Payment & Action Buttons
            ActionButtonsRow(
                onScanQr = onScanQr,
                onAddManual = onAddManual,
                onMyQr = onMyQr,
                onOpenNotificationSimulator = onOpenNotificationSimulator
            )

            // Wallet Summary Card (Multi-bank aggregated balances)
            WalletSummaryCard(
                accounts = accounts,
                totalBalance = totalBalance,
                isBalanceHidden = isBalanceHidden,
                onToggleHideBalance = onToggleHideBalance,
                onAddBank = onAddBank,
                onEditAccount = onEditAccount,
                onManageAll = onManageAllWallets
            )

            // AI Financial Assistant & Cashflow Insights
            com.money.app.ui.components.AiAssistantCard(
                totalBalance = totalBalance,
                todayExpense = todayExpense,
                accounts = accounts,
                transactions = transactions
            )

            // Recent Transactions / Empty State
            TransactionList(
                transactions = transactions,
                onAddClick = onAddManual
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
