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
    onPasteStk: () -> Unit,
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
            // Thẻ Tổng số dư & Liệt kê tài khoản ngân hàng duy nhất
            WalletSummaryCard(
                accounts = accounts,
                totalBalance = totalBalance,
                isBalanceHidden = isBalanceHidden,
                onToggleHideBalance = onToggleHideBalance,
                onAddBank = onAddBank,
                onEditAccount = onEditAccount,
                onManageAll = onManageAllWallets
            )

            // Cụm phím hành động (Chuyển khoản ngân hàng + Ghi tay)
            ActionButtonsRow(
                onScanQr = onScanQr,
                onPasteStk = onPasteStk,
                onAddManual = onAddManual,
                onMyQr = onMyQr,
                onOpenNotificationSimulator = onOpenNotificationSimulator
            )

            // Giao dịch gần đây
            TransactionList(
                transactions = transactions,
                onAddClick = onAddManual
            )

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
