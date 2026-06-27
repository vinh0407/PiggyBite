package com.money.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.data.Fund
import com.money.app.data.Transaction
import com.money.app.ui.MapActivity
import com.money.app.util.AppUtils
import com.money.app.util.FirebaseSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class WalletFragment : Fragment() {

    private lateinit var pieChart: PieChartView
    private lateinit var chartLegend: GridLayout
    private lateinit var recentList: LinearLayout
    private lateinit var fundsContainer: LinearLayout
    private lateinit var lineChart: LineChartView
    private lateinit var tvUserName: TextView
    private lateinit var tvChartTitle: TextView
    private lateinit var tvTotalBalance: TextView
    private lateinit var ivToggleBalance: ImageView
    private lateinit var btnMap: ImageButton
    
    private var isExpenseMode = true
    private var isBalanceVisible = true
    private var actualBalance = 0.0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        tvUserName = view.findViewById(R.id.tvUserName)
        tvChartTitle = view.findViewById(R.id.tvChartTitle)
        pieChart = view.findViewById(R.id.pieChart)
        chartLegend = view.findViewById(R.id.chartLegend)
        recentList = view.findViewById(R.id.recentList)
        fundsContainer = view.findViewById(R.id.fundsContainer)
        lineChart = view.findViewById(R.id.lineChart)
        tvTotalBalance = view.findViewById(R.id.tvTotalBalance)
        ivToggleBalance = view.findViewById(R.id.ivToggleBalance)
        btnMap = view.findViewById(R.id.btnMap)

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val name = prefs.getString("user_name", "")
        tvUserName.text = name?.uppercase() ?: ""

        ivToggleBalance.setOnClickListener {
            isBalanceVisible = !isBalanceVisible
            updateBalanceDisplay()
        }

        setupQuickActions(view)
        setupToggle(view)
        loadData()
    }

    private fun setupToggle(view: View) {
        val rg = view.findViewById<RadioGroup>(R.id.rgChartToggle)
        rg.setOnCheckedChangeListener { _, checkedId ->
            isExpenseMode = (checkedId == R.id.rbExpense)
            val rbExp = view.findViewById<RadioButton>(R.id.rbExpense)
            val rbInc = view.findViewById<RadioButton>(R.id.rbIncome)
            
            if (isExpenseMode) {
                rbExp.setBackgroundResource(R.drawable.bg_pill_white)
                rbExp.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
                rbInc.background = null
                rbInc.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
            } else {
                rbInc.setBackgroundResource(R.drawable.bg_pill_white)
                rbInc.setTextColor(ContextCompat.getColor(requireContext(), R.color.income_green))
                rbExp.background = null
                rbExp.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
            }
            loadData()
        }
    }

    private fun setupQuickActions(view: View) {
        view.findViewById<ImageButton>(R.id.btnSettings).setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
        
        view.findViewById<ImageButton>(R.id.btnMap).setOnClickListener {
            startActivity(Intent(requireContext(), MapActivity::class.java))
        }

        // btnViewAllTrans
        view.findViewById<TextView>(R.id.btnViewAllTrans).setOnClickListener {
            startActivity(Intent(requireContext(), AllTransactionsActivity::class.java))
        }

        // btnAddFund
        view.findViewById<TextView>(R.id.btnAddFund).setOnClickListener {
            startActivity(Intent(requireContext(), AddFundActivity::class.java))
        }

        // Quick action pills
        val add = view.findViewById<View>(R.id.actionAdd)
        add.findViewById<ImageView>(R.id.actionIcon).setImageResource(android.R.drawable.ic_input_add)
        add.findViewById<TextView>(R.id.actionText).text = "Thêm/Add"
        add.setOnClickListener { startActivity(Intent(requireContext(), AddTransactionActivity::class.java)) }

        val report = view.findViewById<View>(R.id.actionReport)
        report.findViewById<ImageView>(R.id.actionIcon).setImageResource(R.drawable.ic_chart)
        report.findViewById<TextView>(R.id.actionText).text = "Báo cáo/Stats"
        report.setOnClickListener { startActivity(Intent(requireContext(), StatisticsActivity::class.java)) }

        val chat = view.findViewById<View>(R.id.actionAIChat)
        chat.findViewById<ImageView>(R.id.actionIcon).setImageResource(R.drawable.ic_brain)
        chat.findViewById<TextView>(R.id.actionText).text = "AI Chat"
        chat.setOnClickListener { startActivity(Intent(requireContext(), AIChatActivity::class.java)) }
        
        val goals = view.findViewById<View>(R.id.actionGoals)
        goals.findViewById<ImageView>(R.id.actionIcon).setImageResource(R.drawable.ic_check_circle)
        goals.findViewById<TextView>(R.id.actionText).text = "Mục tiêu/Goals"
        goals.setOnClickListener { startActivity(Intent(requireContext(), AllFundsActivity::class.java)) }
    }

    private fun loadData() {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        tvChartTitle.text = "Phân tích tháng $currentMonth"

        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val all = withContext(Dispatchers.IO) { db.transactionDao().getAllTransactions() }
            
            var totalInc = 0.0
            var totalExp = 0.0
            all.forEach {
                val amt = AppUtils.parseAmount(it.amount)
                if (it.isExpense) totalExp += amt else totalInc += amt
            }
            actualBalance = totalInc - totalExp
            updateBalanceDisplay()

            updateDonutChart(all)
            updateLineChart(all)
            renderRecent(all.take(10))
            renderFunds()
        }
    }

    private fun updateDonutChart(transactions: List<Transaction>) {
        val filtered = transactions.filter { it.isExpense == isExpenseMode }
        if (filtered.isEmpty()) {
            pieChart.setSlices(emptyList())
            chartLegend.removeAllViews()
            return
        }

        val categoryTotals = filtered.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { AppUtils.parseAmount(it.amount) } }

        val colors = if (isExpenseMode) {
            listOf(0xFF4A5BCC.toInt(), 0xFFEA5455.toInt(), 0xFFD131F5.toInt(), 0xFFFF9F43.toInt(), 0xFF00CFE8.toInt())
        } else {
            listOf(0xFF28C76F.toInt(), 0xFF48DA89.toInt(), 0xFF10AC84.toInt(), 0xFF00FF00.toInt(), 0xFF20BF6B.toInt())
        }

        val slices = categoryTotals.entries.sortedByDescending { it.value }.take(5).mapIndexed { index, entry ->
            PieChartView.Slice(entry.value.toFloat(), colors[index % colors.size], entry.key)
        }

        pieChart.setSlices(slices)
        chartLegend.removeAllViews()
        slices.forEach { slice ->
            val v = TextView(requireContext())
            v.text = "● ${slice.label}: ${AppUtils.formatCurrency(slice.value.toDouble())}"
            v.setTextColor(slice.color)
            v.textSize = 11f
            v.setPadding(8, 4, 8, 4)
            chartLegend.addView(v)
        }
    }

    private fun updateLineChart(transactions: List<Transaction>) {
        val spendArr = FloatArray(7) { 0f }
        val incomeArr = FloatArray(7) { 0f }
        val cal = Calendar.getInstance()
        val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)

        transactions.forEach { t ->
            cal.timeInMillis = t.timestamp
            if (cal.get(Calendar.WEEK_OF_YEAR) == currentWeek) {
                // Calendar.DAY_OF_WEEK: Sun=1, Mon=2...
                var dayIdx = cal.get(Calendar.DAY_OF_WEEK) - 2 // Mon=0
                if (dayIdx < 0) dayIdx = 6 // Sun=6
                
                val amt = AppUtils.parseAmount(t.amount).toFloat()
                if (t.isExpense) spendArr[dayIdx] += amt else incomeArr[dayIdx] += amt
            }
        }
        lineChart.setData(spendArr, incomeArr)
    }

    private fun updateBalanceDisplay() {
        if (isBalanceVisible) {
            tvTotalBalance.text = AppUtils.formatCurrency(actualBalance)
            ivToggleBalance.setImageResource(R.drawable.ic_eye)
        } else {
            tvTotalBalance.text = "********"
            ivToggleBalance.setImageResource(R.drawable.ic_eye_off)
        }
    }

    private fun renderRecent(list: List<Transaction>) {
        recentList.removeAllViews()
        if (list.isEmpty()) {
            val emptyTv = TextView(requireContext())
            emptyTv.text = "Chưa có giao dịch nào/No transactions"
            emptyTv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
            emptyTv.gravity = android.view.Gravity.CENTER
            emptyTv.setPadding(0, 40, 0, 40)
            recentList.addView(emptyTv)
            return
        }
        list.forEach { trans ->
            val item = LayoutInflater.from(requireContext()).inflate(R.layout.item_stats_entry, recentList, false)
            
            // Logic: Show description if available, else show category
            val title = if (trans.description.isNullOrEmpty()) trans.category else trans.description
            item.findViewById<TextView>(R.id.tvTitle).text = title
            
            val amountVal = AppUtils.parseAmount(trans.amount)
            item.findViewById<TextView>(R.id.tvAmount).text = "${if (trans.isExpense) "-" else "+"}${AppUtils.formatCurrency(amountVal)}"
            item.findViewById<TextView>(R.id.tvAmount).setTextColor(
                ContextCompat.getColor(requireContext(), if (trans.isExpense) R.color.expense_red else R.color.income_green)
            )
            
            item.findViewById<TextView>(R.id.tvDateTime).text = trans.date

            recentList.addView(item)
        }
    }

    private fun renderFunds() {
        fundsContainer.removeAllViews()
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val funds = withContext(Dispatchers.IO) { db.fundDao().getAllFunds() }
            funds.forEach { fund ->
                val item = LayoutInflater.from(requireContext()).inflate(R.layout.item_fund_premium, fundsContainer, false)
                item.findViewById<TextView>(R.id.tvGoalName).text = fund.name
                item.findViewById<TextView>(R.id.tvGoalProgress).text = "${AppUtils.formatCurrency(fund.currentAmount)} / ${AppUtils.formatCurrency(fund.targetAmount)}"
                val percent = if (fund.targetAmount > 0) (fund.currentAmount / fund.targetAmount * 100).toInt() else 0
                item.findViewById<TextView>(R.id.tvGoalPercent).text = "$percent%"
                item.findViewById<ProgressBar>(R.id.pbGoal).progress = percent.coerceIn(0, 100)
                
                val btnPin = item.findViewById<ImageButton>(R.id.btnDeleteFund)
                btnPin.setImageResource(if (fund.isPinned) R.drawable.ic_heart else R.drawable.ic_heart) // Needs ic_heart_filled for better UX
                btnPin.setColorFilter(if (fund.isPinned) ContextCompat.getColor(requireContext(), R.color.expense_red) else ContextCompat.getColor(requireContext(), R.color.text_hint))
                
                btnPin.setOnClickListener {
                    fund.isPinned = !fund.isPinned
                    lifecycleScope.launch(Dispatchers.IO) { db.fundDao().update(fund) }
                    renderFunds()
                }

                item.setOnLongClickListener {
                    showEditDeleteDialog(fund)
                    true
                }

                item.findViewById<Button>(R.id.btnDeposit).setOnClickListener {
                    showAmountDialog(fund, isDeposit = true)
                }

                item.findViewById<Button>(R.id.btnWithdraw).setOnClickListener {
                    showAmountDialog(fund, isDeposit = false)
                }

                fundsContainer.addView(item)
            }
        }
    }

    private fun showEditDeleteDialog(fund: Fund) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_vivid_options, null)
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
        dialog.setContentView(view)

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val isOwner = fund.ownerId == currentUserId

        val deleteContainer = view.findViewById<LinearLayout>(R.id.btnDeleteOption)
        val deleteText = deleteContainer.getChildAt(1) as? TextView
        
        if (!isOwner) {
            deleteText?.text = "Rời khỏi quỹ/Leave"
        } else {
            deleteText?.text = "Giải thể quỹ/Disband"
        }

        view.findViewById<LinearLayout>(R.id.btnEditOption).setOnClickListener {
            dialog.dismiss()
            showUpdateFundDialog(fund)
        }

        view.findViewById<LinearLayout>(R.id.btnAddMemberOption).setOnClickListener {
            dialog.dismiss()
            showAddMemberDialog(fund)
        }

        view.findViewById<LinearLayout>(R.id.btnViewContributionsOption).setOnClickListener {
            dialog.dismiss()
            showContributionsDialog(fund)
        }

        deleteContainer.setOnClickListener {
            dialog.dismiss()
            if (isOwner) confirmDeleteFund(fund)
            else confirmLeaveFund(fund)
        }

        dialog.show()
    }

    private fun confirmLeaveFund(fund: Fund) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Rời khỏi quỹ")
            .setMessage("Bạn có chắc muốn rời khỏi quỹ '${fund.name}'? Số tiền bạn đã đóng sẽ được hoàn lại vào tài khoản của bạn.")
            .setPositiveButton("Rời & Hoàn tiền") { _, _ ->
                leaveFundAndRefund(fund)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun leaveFundAndRefund(fund: Fund) {
        lifecycleScope.launch(Dispatchers.IO) {
            val syncManager = FirebaseSyncManager(requireContext())
            syncManager.leaveFund(fund)
            
            // Sync to remove from local
            syncManager.syncFunds()
            
            withContext(Dispatchers.Main) {
                loadData()
                Toast.makeText(context, "Bạn đã rời quỹ và nhận lại tiền", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun confirmDeleteFund(fund: Fund) {
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Giải thể quỹ")
            .setMessage("Bạn có chắc muốn xóa quỹ '${fund.name}'? Số tiền mỗi thành viên đã đóng sẽ được hoàn lại vào tài khoản của họ.")
            .setPositiveButton("Xóa & Hoàn tiền") { _, _ ->
                deleteFundAndRefund(fund)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun deleteFundAndRefund(fund: Fund) {
        lifecycleScope.launch(Dispatchers.IO) {
            val syncManager = FirebaseSyncManager(requireContext())
            syncManager.deleteFundAndRefund(fund)
            
            // Sync to remove from local
            syncManager.syncFunds()
            
            withContext(Dispatchers.Main) {
                loadData()
                Toast.makeText(context, "Quỹ đã được giải thể và hoàn tiền", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showContributionsDialog(fund: Fund) {
        val container = LinearLayout(requireContext())
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(60, 40, 60, 40)

        lifecycleScope.launch {
            val dbRef = FirebaseDatabase.getInstance().reference
            
            fund.memberContributions.forEach { (uid, amount) ->
                val snapshot = dbRef.child("users").child(uid).child("profile").get().await()
                val name = snapshot.child("name").value as? String ?: "Người dùng ẩn"
                
                val tv = TextView(requireContext())
                tv.text = "$name: ${AppUtils.formatCurrency(amount)}"
                tv.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_main))
                tv.textSize = 16f
                tv.setPadding(0, 10, 0, 10)
                container.addView(tv)
            }

            if (fund.memberContributions.isEmpty()) {
                val tv = TextView(requireContext())
                tv.text = "Chưa có đóng góp nào."
                container.addView(tv)
            }

            withContext(Dispatchers.Main) {
                android.app.AlertDialog.Builder(requireContext())
                    .setTitle("Chi tiết đóng góp")
                    .setView(container)
                    .setPositiveButton("Đóng", null)
                    .show()
            }
        }
    }

    private fun showAddMemberDialog(fund: Fund) {
        val etEmail = EditText(requireContext())
        etEmail.hint = "Email thành viên gia đình"
        etEmail.setPadding(40, 40, 40, 40)
        
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Thêm người vào quỹ")
            .setMessage("Nhập email người bạn muốn mời vào quỹ '${fund.name}':")
            .setView(etEmail)
            .setPositiveButton("Thêm") { _, _ ->
                val email = etEmail.text.toString().trim()
                if (email.isNotEmpty()) {
                    lifecycleScope.launch {
                        val syncManager = FirebaseSyncManager(requireContext())
                        syncManager.shareFund(fund.syncId, email)
                        Toast.makeText(requireContext(), "Đã gửi lời mời tới $email", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showUpdateFundDialog(fund: Fund) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_category, null) // Reusing layout for simplicity
        val etName = view.findViewById<EditText>(R.id.etCategoryName)
        etName.setText(fund.name)
        
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Cập nhật quỹ")
            .setView(view)
            .setPositiveButton("Lưu") { _, _ ->
                val newName = etName.text.toString()
                if (newName.isNotEmpty()) {
                    val updatedFund = fund.copy(name = newName)
                    lifecycleScope.launch(Dispatchers.IO) {
                        AppDatabase.getDatabase(requireContext()).fundDao().update(updatedFund)
                        withContext(Dispatchers.Main) { renderFunds() }
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showAmountDialog(fund: Fund, isDeposit: Boolean) {
        val et = EditText(requireContext())
        et.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        et.hint = "Nhập số tiền..."
        
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(if (isDeposit) "Góp vào quỹ" else "Rút từ quỹ")
            .setView(et)
            .setPositiveButton("Xác nhận") { _, _ ->
                val amount = et.text.toString().toDoubleOrNull() ?: 0.0
                if (amount > 0) {
                    processFundTransaction(fund, amount, isDeposit)
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun processFundTransaction(fund: Fund, amount: Double, isDeposit: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            val syncManager = FirebaseSyncManager(requireContext())
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            
            // 1. Update Fund Amount and Member Contributions
            if (isDeposit) {
                fund.currentAmount += amount
                val currentContrib = fund.memberContributions[currentUserId] ?: 0.0
                val newMap = fund.memberContributions.toMutableMap()
                newMap[currentUserId] = currentContrib + amount
                fund.memberContributions = newMap
            } else {
                fund.currentAmount -= amount
                val currentContrib = fund.memberContributions[currentUserId] ?: 0.0
                val newMap = fund.memberContributions.toMutableMap()
                newMap[currentUserId] = (currentContrib - amount).coerceAtLeast(0.0)
                fund.memberContributions = newMap
            }
            
            db.fundDao().update(fund)
            syncManager.createFund(fund) // createFund updates the whole object in RTDB

            // 2. Create Transaction
            val trans = Transaction(
                amount = amount.toLong().toString(),
                category = if (isDeposit) "Góp quỹ" else "Rút tiền quỹ",
                description = "${if (isDeposit) "Góp vào" else "Rút từ"} quỹ ${fund.name}",
                date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                isExpense = isDeposit, // Deposit is expense from main wallet, Withdraw is income to main wallet
                timestamp = System.currentTimeMillis()
            )
            db.transactionDao().insert(trans)
            syncManager.saveTransaction(trans)

            withContext(Dispatchers.Main) {
                loadData()
                Toast.makeText(context, "Thao tác thành công", Toast.LENGTH_SHORT).show()
            }
        }
    }
}