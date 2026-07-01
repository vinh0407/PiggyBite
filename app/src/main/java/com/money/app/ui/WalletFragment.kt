package com.money.app.ui

import com.money.app.databinding.FragmentWalletBinding
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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.PickVisualMediaRequest
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
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

/**
 * Dashboard Fragment representing the user's primary wallet overview.
 * Features:
 * - Real-time balance calculation
 * - Monthly analysis charts (Donut & Line)
 * - Recent transaction history
 * - Shared fund management
 * 
 * Automatically refreshes data in [onResume] to ensure UI consistency.
 */
class WalletFragment : Fragment() {

    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!
    
    private var isExpenseMode = true
    private var isBalanceVisible = true
    private var actualBalance = 0.0

    private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } else {
            Toast.makeText(requireContext(), "Quyền truy cập ảnh bị từ chối", Toast.LENGTH_SHORT).show()
        }
    }

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            val localFile = saveImageToInternalStorage(uri)
            if (localFile != null) {
                saveAvatarUri(Uri.fromFile(localFile))
                binding.ivProfile.setImageURI(Uri.fromFile(localFile))
            } else {
                saveAvatarUri(uri)
                binding.ivProfile.setImageURI(uri)
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val name = prefs.getString("user_name", "")
        binding.tvUserName.text = name?.uppercase() ?: ""

        binding.ivToggleBalance.setOnClickListener {
            isBalanceVisible = !isBalanceVisible
            updateBalanceDisplay()
        }

        val clickListener = View.OnClickListener {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
                // On Android 13+, PickVisualMedia doesn't need permissions, but we can request READ_MEDIA_IMAGES if we want to be safe
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                // On older versions, request READ_EXTERNAL_STORAGE
                if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                } else {
                    requestPermissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }

        binding.flProfile.setOnClickListener(clickListener)
        binding.ivProfile.setOnClickListener(clickListener)

        loadAvatar()
        setupQuickActions()
        setupToggle()
        loadData()
    }

    override fun onResume() {
        super.onResume()
        // Refresh data every time user returns to home screen
        loadData()
        
        // Refresh name in case it was changed in ProfileActivity
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val name = prefs.getString("user_name", "")
        binding.tvUserName.text = name?.uppercase() ?: ""
    }

    private fun saveAvatarUri(uri: Uri) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("avatar_uri", uri.toString()).apply()
    }

    private fun saveImageToInternalStorage(uri: Uri): File? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(uri) ?: return null
            val file = File(requireContext().filesDir, "user_avatar.jpg")
            val outputStream = FileOutputStream(file)
            inputStream.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun loadAvatar() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val uriString = prefs.getString("avatar_uri", null)
        if (uriString != null) {
            val uri = Uri.parse(uriString)
            if (uri.scheme == "file") {
                val file = File(uri.path ?: "")
                if (file.exists()) {
                    binding.ivProfile.setImageURI(uri)
                } else {
                    binding.ivProfile.setImageResource(R.drawable.ic_piggy_bank)
                }
            } else {
                binding.ivProfile.setImageURI(uri)
            }
        }
    }

    private fun setupToggle() {
        binding.rgChartToggle.setOnCheckedChangeListener { _, checkedId ->
            isExpenseMode = (checkedId == R.id.rbExpense)
            
            if (isExpenseMode) {
                binding.rbExpense.setBackgroundResource(R.drawable.bg_pill_white)
                binding.rbExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
                binding.rbIncome.background = null
                binding.rbIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
            } else {
                binding.rbIncome.setBackgroundResource(R.drawable.bg_pill_white)
                binding.rbIncome.setTextColor(ContextCompat.getColor(requireContext(), R.color.income_green))
                binding.rbExpense.background = null
                binding.rbExpense.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_hint))
            }
            loadData()
        }
    }

    private fun setupQuickActions() {
        binding.btnSettings.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }
        
        binding.btnMap.setOnClickListener {
            startActivity(Intent(requireContext(), MapActivity::class.java))
        }

        binding.btnViewFlowMore.setOnClickListener {
            startActivity(Intent(requireContext(), StatisticsActivity::class.java))
        }

        binding.btnViewAllTrans.setOnClickListener {
            startActivity(Intent(requireContext(), AllTransactionsActivity::class.java))
        }

        binding.btnAddFund.setOnClickListener {
            startActivity(Intent(requireContext(), AddFundActivity::class.java))
        }

        // Quick action pills
        binding.actionAdd.root.setOnClickListener { startActivity(Intent(requireContext(), AddTransactionActivity::class.java)) }
        binding.actionAdd.actionIcon.setImageResource(android.R.drawable.ic_input_add)
        binding.actionAdd.actionText.text = "Thêm/Add"

        binding.actionReport.root.setOnClickListener { startActivity(Intent(requireContext(), StatisticsActivity::class.java)) }
        binding.actionReport.actionIcon.setImageResource(R.drawable.ic_chart)
        binding.actionReport.actionText.text = "Báo cáo/Stats"

        binding.actionAIChat.root.setOnClickListener { startActivity(Intent(requireContext(), AIChatActivity::class.java)) }
        binding.actionAIChat.actionIcon.setImageResource(R.drawable.ic_brain)
        binding.actionAIChat.actionText.text = "AI Chat"
        
        binding.actionGoals.root.setOnClickListener { startActivity(Intent(requireContext(), AllFundsActivity::class.java)) }
        binding.actionGoals.actionIcon.setImageResource(R.drawable.ic_check_circle)
        binding.actionGoals.actionText.text = "Mục tiêu/Goals"
    }

    private fun loadData() {
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
        binding.tvChartTitle.text = "Phân tích tháng $currentMonth"

        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val all = withContext(Dispatchers.IO) { db.transactionDao().getAllTransactions() }
            
            var totalInc = 0.0
            var totalExp = 0.0
            all.forEach {
                val amt = it.amount
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
            binding.pieChart.setSlices(emptyList())
            binding.chartLegend.removeAllViews()
            return
        }

        val categoryTotals = filtered.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val colors = if (isExpenseMode) {
            listOf(0xFF4A5BCC.toInt(), 0xFFEA5455.toInt(), 0xFFD131F5.toInt(), 0xFFFF9F43.toInt(), 0xFF00CFE8.toInt())
        } else {
            listOf(0xFF28C76F.toInt(), 0xFF48DA89.toInt(), 0xFF10AC84.toInt(), 0xFF00FF00.toInt(), 0xFF20BF6B.toInt())
        }

        val slices = categoryTotals.entries.sortedByDescending { it.value }.take(5).mapIndexed { index, entry ->
            PieChartView.Slice(entry.value.toFloat(), colors[index % colors.size], entry.key)
        }

        binding.pieChart.setSlices(slices)
        binding.chartLegend.removeAllViews()
        slices.forEach { slice ->
            val v = TextView(requireContext())
            v.text = "● ${slice.label}: ${AppUtils.formatCurrency(slice.value.toDouble(), requireContext())}"
            v.setTextColor(slice.color)
            v.textSize = 11f
            v.setPadding(8, 4, 8, 4)
            binding.chartLegend.addView(v)
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
                
                val amt = t.amount.toFloat()
                if (t.isExpense) spendArr[dayIdx] += amt else incomeArr[dayIdx] += amt
            }
        }
        binding.lineChart.setData(spendArr, incomeArr)
    }

    private fun updateBalanceDisplay() {
        if (isBalanceVisible) {
            binding.tvTotalBalance.text = AppUtils.formatCurrency(actualBalance, requireContext())
            binding.ivToggleBalance.setImageResource(R.drawable.ic_eye)
        } else {
            binding.tvTotalBalance.text = "********"
            binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_off)
        }
    }

    private fun renderRecent(list: List<Transaction>) {
        binding.recentList.removeAllViews()
        if (list.isEmpty()) {
            val emptyView = LayoutInflater.from(requireContext()).inflate(R.layout.item_empty_state, binding.recentList, false)
            binding.recentList.addView(emptyView)
            return
        }
        list.forEach { trans ->
            val item = LayoutInflater.from(requireContext()).inflate(R.layout.item_stats_entry, binding.recentList, false)
            
            // Logic: Show description if available, else show category
            val title = if (trans.description.isNullOrEmpty()) trans.category else trans.description
            item.findViewById<TextView>(R.id.tvTitle).text = title
            
            val amountVal = trans.amount
            item.findViewById<TextView>(R.id.tvAmount).text = "${if (trans.isExpense) "-" else "+"}${AppUtils.formatCurrency(amountVal, requireContext())}"
            item.findViewById<TextView>(R.id.tvAmount).setTextColor(
                ContextCompat.getColor(requireContext(), if (trans.isExpense) R.color.expense_red else R.color.income_green)
            )
            
            item.findViewById<TextView>(R.id.tvDateTime).text = trans.date

            binding.recentList.addView(item)
        }
    }

    private fun renderFunds() {
        binding.fundsContainer.removeAllViews()
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val funds = withContext(Dispatchers.IO) { db.fundDao().getAllFunds() }
            funds.forEach { fund ->
                val item = LayoutInflater.from(requireContext()).inflate(R.layout.item_fund_premium, binding.fundsContainer, false)
                item.findViewById<TextView>(R.id.tvGoalName).text = fund.name
                item.findViewById<TextView>(R.id.tvGoalProgress).text = "${AppUtils.formatCurrency(fund.currentAmount, requireContext())} / ${AppUtils.formatCurrency(fund.targetAmount, requireContext())}"
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

                binding.fundsContainer.addView(item)
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
                tv.text = "$name: ${AppUtils.formatCurrency(amount, requireContext())}"
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
                amount = amount,
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}