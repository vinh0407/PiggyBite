package com.piggybite.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.piggybite.app.data.DataRepository
import com.piggybite.app.data.NotificationEvent
import com.piggybite.app.model.BankAccount
import com.piggybite.app.ui.components.BottomNavBar
import com.piggybite.app.ui.components.FloatingNotificationToast
import com.piggybite.app.ui.components.NavTab
import com.piggybite.app.ui.dialogs.AddBankAccountDialog
import com.piggybite.app.ui.dialogs.AddTransactionDialog
import com.piggybite.app.ui.dialogs.MyQrDialog
import com.piggybite.app.ui.dialogs.NotificationSimulatorDialog
import com.piggybite.app.ui.dialogs.QrPaymentDialog
import com.piggybite.app.ui.dialogs.QrPaymentStep
import com.piggybite.app.ui.dialogs.WeeklyReviewDialog
import com.piggybite.app.ui.screens.AnalyticsScreen
import com.piggybite.app.ui.screens.CalendarScreen
import com.piggybite.app.ui.screens.HistoryScreen
import com.piggybite.app.ui.screens.HomeScreen
import com.piggybite.app.ui.screens.ReportsScreen
import com.piggybite.app.ui.screens.SettingsScreen
import com.piggybite.app.ui.screens.WalletScreen
import com.piggybite.app.ui.theme.PiggyBiteTheme
import com.piggybite.app.service.NotificationAccessHelper
import com.piggybite.app.ui.dialogs.NotificationAccessDialog
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = DataRepository.getInstance(applicationContext)

        setContent {
            val isDarkMode by repository.isDarkMode.collectAsState()

            PiggyBiteTheme(darkTheme = isDarkMode) {
                MainAppContent(repository, isDarkMode)
            }
        }
    }
}

@Composable
fun MainAppContent(repository: DataRepository, isDarkMode: Boolean) {
    val accounts by repository.accounts.collectAsState()
    val transactions by repository.transactions.collectAsState()
    val receivedNotifications by repository.receivedNotifications.collectAsState()
    val isBalanceHidden by repository.isBalanceHidden.collectAsState()

    var activeTab by remember { mutableStateOf(NavTab.HOME) }
    var toastEvent by remember { mutableStateOf<NotificationEvent?>(null) }

    // Dialogs state
    var qrInitialStep by remember { mutableStateOf(QrPaymentStep.SCAN_CAMERA) }
    var showQrPaymentDialog by remember { mutableStateOf(false) }
    var showWeeklyReviewDialog by remember { mutableStateOf(false) }
    var showNotificationSimulator by remember { mutableStateOf(false) }
    var showAddTransactionDialog by remember { mutableStateOf(false) }
    var showAddBankDialog by remember { mutableStateOf(false) }
    var accountToEdit by remember { mutableStateOf<BankAccount?>(null) }
    var showMyQrDialog by remember { mutableStateOf(false) }

    // Notification access: re-check each time app returns to foreground (e.g. back from Settings)
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var hasNotificationAccess by remember { mutableStateOf(NotificationAccessHelper.isGranted(context)) }
    var accessPromptDismissed by remember { mutableStateOf(false) }
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasNotificationAccess = NotificationAccessHelper.isGranted(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Collect live notification events from background service
    LaunchedEffect(Unit) {
        repository.notificationEvents.collect { event ->
            toastEvent = event
            delay(5000)
            if (toastEvent == event) {
                toastEvent = null
            }
        }
    }

    val totalBalance = remember(accounts) {
        accounts.sumOf { it.balance }
    }
    val todayExpense = remember(transactions) {
        repository.getTodayExpenses()
    }
    val todayIncome = remember(transactions) {
        repository.getTodayIncome()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            // Floating Pill Shaped Navigation Bar (Nếp style with 5 tabs)
            BottomNavBar(
                currentTab = activeTab,
                onTabSelected = { activeTab = it },
                onScanQrClick = {
                    qrInitialStep = QrPaymentStep.SCAN_CAMERA
                    showQrPaymentDialog = true
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(innerPadding)
        ) {
            when (activeTab) {
                NavTab.HOME -> {
                    HomeScreen(
                        todayExpense = todayExpense,
                        todayIncome = todayIncome,
                        totalBalance = totalBalance,
                        isBalanceHidden = isBalanceHidden,
                        isDarkMode = isDarkMode,
                        onToggleHideBalance = { repository.toggleBalanceVisibility() },
                        onToggleDarkMode = { repository.toggleDarkMode() },
                        accounts = accounts,
                        transactions = transactions,
                        onScanQr = {
                            qrInitialStep = QrPaymentStep.SCAN_CAMERA
                            showQrPaymentDialog = true
                        },
                        onPasteStk = {
                            qrInitialStep = QrPaymentStep.PASTE_ACCOUNT
                            showQrPaymentDialog = true
                        },
                        onAddManual = { showAddTransactionDialog = true },
                        onMyQr = { showMyQrDialog = true },
                        onOpenWeeklyReview = { showWeeklyReviewDialog = true },
                        onOpenNotificationSimulator = { showNotificationSimulator = true },
                        onAddBank = {
                            accountToEdit = null
                            showAddBankDialog = true
                        },
                        onEditAccount = { acc ->
                            accountToEdit = acc
                            showAddBankDialog = true
                        },
                        onManageAllWallets = { activeTab = NavTab.REPORTS }
                    )
                }
                NavTab.HISTORY -> {
                    HistoryScreen(
                        transactions = transactions,
                        onAddTransaction = { showAddTransactionDialog = true }
                    )
                }
                NavTab.SCAN_QR -> {
                    // Open QR Scanner directly
                    HomeScreen(
                        todayExpense = todayExpense,
                        todayIncome = todayIncome,
                        totalBalance = totalBalance,
                        isBalanceHidden = isBalanceHidden,
                        isDarkMode = isDarkMode,
                        onToggleHideBalance = { repository.toggleBalanceVisibility() },
                        onToggleDarkMode = { repository.toggleDarkMode() },
                        accounts = accounts,
                        transactions = transactions,
                        onScanQr = {
                            qrInitialStep = QrPaymentStep.SCAN_CAMERA
                            showQrPaymentDialog = true
                        },
                        onPasteStk = {
                            qrInitialStep = QrPaymentStep.PASTE_ACCOUNT
                            showQrPaymentDialog = true
                        },
                        onAddManual = { showAddTransactionDialog = true },
                        onMyQr = { showMyQrDialog = true },
                        onOpenWeeklyReview = { showWeeklyReviewDialog = true },
                        onOpenNotificationSimulator = { showNotificationSimulator = true },
                        onAddBank = {
                            accountToEdit = null
                            showAddBankDialog = true
                        },
                        onEditAccount = { acc ->
                            accountToEdit = acc
                            showAddBankDialog = true
                        },
                        onManageAllWallets = { activeTab = NavTab.REPORTS }
                    )
                }
                NavTab.REPORTS -> {
                    ReportsScreen(
                        accounts = accounts,
                        transactions = transactions,
                        onAddBank = {
                            accountToEdit = null
                            showAddBankDialog = true
                        },
                        onEditAccount = { acc ->
                            accountToEdit = acc
                            showAddBankDialog = true
                        }
                    )
                }
                NavTab.SETTINGS -> {
                    SettingsScreen(
                        onOpenNotificationSimulator = { showNotificationSimulator = true },
                        onResetData = { repository.clearAllData() }
                    )
                }
            }

            // Top Floating Notification Toast
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter
            ) {
                FloatingNotificationToast(
                    event = toastEvent,
                    onDismiss = { toastEvent = null }
                )
            }
        }
    }

    // Modal Dialogs
    if (showWeeklyReviewDialog) {
        WeeklyReviewDialog(
            transactions = transactions,
            onDismiss = { showWeeklyReviewDialog = false },
            onAddManual = {
                showWeeklyReviewDialog = false
                showAddTransactionDialog = true
            }
        )
    }

    if (showQrPaymentDialog) {
        QrPaymentDialog(
            accounts = accounts,
            initialStep = qrInitialStep,
            onDismiss = { showQrPaymentDialog = false },
            onPaymentConfirmed = { tx ->
                repository.addTransaction(tx)
            }
        )
    }

    if (showNotificationSimulator) {
        NotificationSimulatorDialog(
            notifications = receivedNotifications,
            onDismiss = { showNotificationSimulator = false },
            onProcessText = { text ->
                repository.processBankNotification(text)
            },
            onClearAll = { repository.clearReceivedNotifications() },
            onDeleteItem = { id -> repository.deleteReceivedNotification(id) }
        )
    }

    if (showAddTransactionDialog) {
        AddTransactionDialog(
            accounts = accounts,
            onDismiss = { showAddTransactionDialog = false },
            onAddTransaction = { tx ->
                repository.addTransaction(tx)
            }
        )
    }

    if (showAddBankDialog) {
        AddBankAccountDialog(
            initialAccount = accountToEdit,
            onDismiss = {
                showAddBankDialog = false
                accountToEdit = null
            },
            onSaveAccount = { acc ->
                repository.addOrUpdateAccount(acc)
            },
            onDeleteAccount = { accId ->
                repository.deleteAccount(accId)
            }
        )
    }

    if (showMyQrDialog) {
        MyQrDialog(
            accounts = accounts,
            onSaveAccountQr = { accId, path ->
                repository.saveAccountQr(accId, path)
            },
            onDismiss = { showMyQrDialog = false }
        )
    }

    if (!hasNotificationAccess && !accessPromptDismissed) {
        NotificationAccessDialog(
            onOpenSettings = {
                accessPromptDismissed = true
                NotificationAccessHelper.openSettings(context)
            },
            onDismiss = { accessPromptDismissed = true }
        )
    }
}
