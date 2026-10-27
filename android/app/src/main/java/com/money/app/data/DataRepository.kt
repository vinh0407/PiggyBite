package com.money.app.data

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.money.app.model.Bank
import com.money.app.model.BankAccount
import com.money.app.model.BankDirectory
import com.money.app.model.Transaction
import com.money.app.service.ExpenseClassifierAI
import com.money.app.service.IncomeClassifierAI
import com.money.app.service.NotificationParser
import com.money.app.service.ParsedNotification
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

data class NotificationEvent(
    val bankName: String,
    val amount: Long,
    val isIncome: Boolean,
    val note: String,
    val newBalance: Long,
    val aiCategory: String = if (isIncome) "Thu nhập" else "Chi tiêu"
)

class DataRepository private constructor(private val appContext: Context) {

    private val prefs: SharedPreferences = appContext.getSharedPreferences("piggybite_native_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val incomeClassifier = IncomeClassifierAI.getInstance(appContext)
    private val expenseClassifier = ExpenseClassifierAI.getInstance(appContext)

    private val _accounts = MutableStateFlow<List<BankAccount>>(emptyList())
    val accounts: StateFlow<List<BankAccount>> = _accounts.asStateFlow()

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()

    private val _isBalanceHidden = MutableStateFlow(prefs.getBoolean("hide_balance", false))
    val isBalanceHidden: StateFlow<Boolean> = _isBalanceHidden.asStateFlow()

    private val _isDarkMode = MutableStateFlow(prefs.getBoolean("dark_mode", false))
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _notificationEvents = MutableSharedFlow<NotificationEvent>(replay = 0)
    val notificationEvents: SharedFlow<NotificationEvent> = _notificationEvents.asSharedFlow()

    init {
        loadData()
    }

    private fun loadData() {
        // One-time automatic purge of previous mock/example data so user starts with a clean slate
        val hasPurgedSampleData = prefs.getBoolean("has_purged_sample_data_v3", false)
        if (!hasPurgedSampleData) {
            prefs.edit()
                .remove("bank_accounts")
                .remove("transactions")
                .putBoolean("has_purged_sample_data_v3", true)
                .apply()
        }

        val accJson = prefs.getString("bank_accounts", null)
        val accList = if (!accJson.isNullOrBlank()) {
            val type = object : TypeToken<List<BankAccount>>() {}.type
            gson.fromJson<List<BankAccount>>(accJson, type) ?: emptyList()
        } else {
            emptyList()
        }
        _accounts.value = accList

        val txJson = prefs.getString("transactions", null)
        val txList = if (!txJson.isNullOrBlank()) {
            val type = object : TypeToken<List<Transaction>>() {}.type
            gson.fromJson<List<Transaction>>(txJson, type) ?: emptyList()
        } else {
            emptyList()
        }
        _transactions.value = txList
        saveAccounts(accList)
        saveTransactions(txList)
    }

    private fun saveAccounts(list: List<BankAccount>) {
        prefs.edit().putString("bank_accounts", gson.toJson(list)).apply()
    }

    private fun saveTransactions(list: List<Transaction>) {
        prefs.edit().putString("transactions", gson.toJson(list)).apply()
    }

    fun toggleBalanceVisibility() {
        val next = !_isBalanceHidden.value
        _isBalanceHidden.value = next
        prefs.edit().putBoolean("hide_balance", next).apply()
    }

    fun toggleDarkMode() {
        val next = !_isDarkMode.value
        _isDarkMode.value = next
        prefs.edit().putBoolean("dark_mode", next).apply()
    }

    fun getTotalBalance(): Long {
        return _accounts.value.sumOf { it.balance }
    }

    fun getTodayExpenses(): Long {
        val now = System.currentTimeMillis()
        val startOfDay = now - (now % 86400000L)
        return _transactions.value
            .filter { !it.isIncome && it.timestamp >= startOfDay }
            .sumOf { it.amount }
    }

    fun getTodayIncome(): Long {
        val now = System.currentTimeMillis()
        val startOfDay = now - (now % 86400000L)
        return _transactions.value
            .filter { it.isIncome && it.timestamp >= startOfDay }
            .sumOf { it.amount }
    }

    fun addOrUpdateAccount(account: BankAccount) {
        val current = _accounts.value.toMutableList()
        val idx = current.indexOfFirst { it.id == account.id }
        if (idx >= 0) {
            current[idx] = account
        } else {
            current.add(account)
        }
        _accounts.value = current
        saveAccounts(current)
    }

    fun saveAccountQr(accountId: String, qrPath: String) {
        val current = _accounts.value.toMutableList()
        val idx = current.indexOfFirst { it.id == accountId }
        if (idx >= 0) {
            current[idx] = current[idx].copy(qrImagePath = qrPath)
            _accounts.value = current
            saveAccounts(current)
        }
    }

    fun deleteAccount(accountId: String) {
        val current = _accounts.value.filter { it.id != accountId }
        _accounts.value = current
        saveAccounts(current)
    }

    fun addTransaction(tx: Transaction) {
        val currentTxs = _transactions.value.toMutableList()
        currentTxs.add(0, tx)
        _transactions.value = currentTxs
        saveTransactions(currentTxs)

        // Self-train AI if transaction has custom category and note
        if (tx.isIncome && tx.note.isNotBlank()) {
            incomeClassifier.train(tx.note, tx.category)
        } else if (!tx.isIncome && tx.note.isNotBlank()) {
            expenseClassifier.train(tx.note, tx.category)
        }

        // Adjust corresponding bank account balance
        val currentAccs = _accounts.value.toMutableList()
        val accIdx = currentAccs.indexOfFirst { it.id == tx.bankAccountId }
        if (accIdx >= 0) {
            val acc = currentAccs[accIdx]
            val newBal = if (tx.isIncome) acc.balance + tx.amount else maxOf(0L, acc.balance - tx.amount)
            currentAccs[accIdx] = acc.copy(balance = newBal, lastUpdated = System.currentTimeMillis())
            _accounts.value = currentAccs
            saveAccounts(currentAccs)
        }
    }

    fun processBankNotification(rawText: String): ParsedNotification? {
        val parsed = NotificationParser.parse(rawText) ?: return null

        val currentAccs = _accounts.value.toMutableList()
        var targetAcc = currentAccs.firstOrNull { it.bankId == parsed.bank.id }

        if (targetAcc == null) {
            // Auto create account for this bank if not yet exists
            targetAcc = BankAccount(
                id = "acc_" + parsed.bank.id,
                bankId = parsed.bank.id,
                bankName = parsed.bank.shortName,
                accountNumber = if (parsed.accountNumber.isNotBlank()) parsed.accountNumber else "Auto",
                balance = if (parsed.isIncome) parsed.amount else 0L
            )
            currentAccs.add(targetAcc)
        } else {
            val idx = currentAccs.indexOfFirst { it.id == targetAcc.id }
            val newBal = if (parsed.isIncome) {
                targetAcc.balance + parsed.amount
            } else {
                maxOf(0L, targetAcc.balance - parsed.amount)
            }
            targetAcc = targetAcc.copy(balance = newBal, lastUpdated = System.currentTimeMillis())
            currentAccs[idx] = targetAcc
        }

        _accounts.value = currentAccs
        saveAccounts(currentAccs)

        // AI Categorization for Income & Expense
        var resolvedCategory = if (parsed.isIncome) "Thu nhập khác" else "Chi tiêu khác"
        if (parsed.isIncome) {
            val aiResult = incomeClassifier.classifyIncome(parsed.rawText)
            resolvedCategory = aiResult.category
            // Self-train only on confident predictions to avoid reinforcing mistakes
            if (aiResult.confidence >= IncomeClassifierAI.AUTO_LEARN_THRESHOLD) {
                incomeClassifier.train(parsed.rawText, resolvedCategory, weight = 1.0)
            }
        } else {
            val aiResult = expenseClassifier.classifyExpense(parsed.rawText)
            resolvedCategory = aiResult.category
            if (aiResult.confidence >= ExpenseClassifierAI.AUTO_LEARN_THRESHOLD) {
                expenseClassifier.train(parsed.rawText, resolvedCategory, weight = 1.0)
            }
        }

        // Record transaction
        val tx = Transaction(
            id = "tx_auto_" + UUID.randomUUID().toString().take(8),
            amount = parsed.amount,
            isIncome = parsed.isIncome,
            bankAccountId = targetAcc.id,
            bankName = parsed.bank.shortName,
            note = parsed.note,
            timestamp = System.currentTimeMillis(),
            category = resolvedCategory
        )
        val currentTxs = _transactions.value.toMutableList()
        currentTxs.add(0, tx)
        _transactions.value = currentTxs
        saveTransactions(currentTxs)

        // Broadcast notification event
        scope.launch {
            _notificationEvents.emit(
                NotificationEvent(
                    bankName = parsed.bank.shortName,
                    amount = parsed.amount,
                    isIncome = parsed.isIncome,
                    note = parsed.note,
                    newBalance = targetAcc.balance,
                    aiCategory = resolvedCategory
                )
            )
        }

        return parsed
    }

    fun clearAllData() {
        prefs.edit()
            .remove("bank_accounts")
            .remove("transactions")
            .putBoolean("has_purged_sample_data_v3", true)
            .apply()
        _accounts.value = emptyList()
        _transactions.value = emptyList()
    }

    companion object {
        @Volatile
        private var instance: DataRepository? = null

        fun getInstance(context: Context): DataRepository {
            return instance ?: synchronized(this) {
                instance ?: DataRepository(context.applicationContext).also { instance = it }
            }
        }
    }
}
