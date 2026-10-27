package com.money.app.util

import android.content.Context
import com.money.app.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Xây dựng bối cảnh tài chính cấu trúc cho AI Copilot:
 * Tổng hợp dữ liệu thực từ Room Database (Thu/Chi, Tài khoản, Ngân sách, Quỹ, Nợ, Đầu tư)
 * thành chuỗi JSON chuẩn để truyền cho AI xử lý chính xác tuyệt đối.
 */
class StructuredFinancialContextBuilder(private val context: Context) {
    private val db = AppDatabase.getDatabase(context)

    suspend fun buildFinancialContextJson(): String = withContext(Dispatchers.IO) {
        val root = JSONObject()

        // 1. Giao dịch & Thu/Chi
        val transactions = db.transactionDao().getAllTransactions()
        var totalIncome = 0.0
        var totalExpense = 0.0
        val categoryExpenseMap = mutableMapOf<String, Double>()

        transactions.forEach { t ->
            if (t.isExpense) {
                totalExpense += t.amount
                val currentCat = categoryExpenseMap.getOrDefault(t.category, 0.0)
                categoryExpenseMap[t.category] = currentCat + t.amount
            } else {
                totalIncome += t.amount
            }
        }

        root.put("totalIncome", totalIncome)
        root.put("totalExpense", totalExpense)
        root.put("netSavings", totalIncome - totalExpense)

        val catArray = JSONArray()
        categoryExpenseMap.forEach { (cat, amt) ->
            val obj = JSONObject()
            obj.put("category", cat)
            obj.put("amount", amt)
            catArray.put(obj)
        }
        root.put("categoryExpenses", catArray)

        // 2. Tài khoản & Giá trị ròng (Net Worth)
        val accounts = db.accountDao().getAllAccounts()
        var totalAssets = 0.0
        var totalLiabilities = 0.0
        val accArray = JSONArray()

        accounts.forEach { acc ->
            if (acc.type == "CREDIT_CARD" || acc.type == "LOAN") {
                totalLiabilities += acc.balance
            } else {
                totalAssets += acc.balance
            }
            val aObj = JSONObject()
            aObj.put("name", acc.name)
            aObj.put("type", acc.type)
            aObj.put("balance", acc.balance)
            accArray.put(aObj)
        }
        root.put("totalAssets", totalAssets)
        root.put("totalLiabilities", totalLiabilities)
        root.put("netWorth", totalAssets - totalLiabilities)
        root.put("accounts", accArray)

        // 3. Quỹ tiết kiệm
        val funds = db.fundDao().getAllFunds()
        val fundArray = JSONArray()
        funds.forEach { f ->
            val fObj = JSONObject()
            fObj.put("name", f.name)
            fObj.put("currentAmount", f.currentAmount)
            fObj.put("targetAmount", f.targetAmount)
            fObj.put("progressPercent", if (f.targetAmount > 0) (f.currentAmount / f.targetAmount * 100).toInt() else 0)
            fundArray.put(fObj)
        }
        root.put("funds", fundArray)

        // 4. Ngân sách
        val budgets = db.budgetDao().getAllBudgets()
        val budgetArray = JSONArray()
        budgets.forEach { b ->
            val spent = categoryExpenseMap.getOrDefault(b.category, 0.0)
            val bObj = JSONObject()
            bObj.put("category", b.category)
            bObj.put("limit", b.amountLimit)
            bObj.put("spent", spent)
            bObj.put("utilizedPercent", if (b.amountLimit > 0) (spent / b.amountLimit * 100).toInt() else 0)
            budgetArray.put(bObj)
        }
        root.put("budgets", budgetArray)

        // 5. Nợ
        val debts = db.debtDao().getAllDebts()
        var owedToMe = 0.0
        var iOwe = 0.0
        debts.forEach { d ->
            if (!d.isSettled) {
                if (d.isOwedToMe) owedToMe += d.amount else iOwe += d.amount
            }
        }
        root.put("totalOwedToMe", owedToMe)
        root.put("totalIOwe", iOwe)

        root.toString(2)
    }
}
