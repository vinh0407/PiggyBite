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
import com.money.app.util.CurrencyHelper
import com.money.app.util.FirebaseSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment Trang chủ hiển thị tổng quan về ví của người dùng.
 * Các tính năng:
 * - Tính toán số dư thời gian thực
 * - Biểu đồ phân tích hàng tháng (Tròn & Đường)
 * - Lịch sử giao dịch gần đây
 * - Quản lý quỹ chung/tiết kiệm
 * 
 * Tự động làm mới dữ liệu trong [onResume] để đảm bảo tính nhất quán của UI.
 */
class WalletFragment : Fragment() {

    private var _binding: FragmentWalletBinding? = null
    private val binding get() = _binding!!
    
    private var isExpenseMode = true // Chế độ hiển thị: Chi tiêu (true) hoặc Thu nhập (false)
    private var isBalanceVisible = true // Trạng thái hiển thị/ẩn số dư
    private var actualBalance = 0.0 // Số dư thực tế
    private var analysisMonth = Calendar.getInstance() // Tháng đang được chọn để phân tích

    // Bộ chọn ảnh từ thư viện máy (Photo Picker)
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
        
        // Hiển thị tên người dùng từ SharedPreferences
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val name = prefs.getString("user_name", "")
        binding.tvUserName.text = name?.uppercase() ?: ""

        // Nút ẩn/hiện số dư
        binding.ivToggleBalance.setOnClickListener {
            isBalanceVisible = !isBalanceVisible
            updateBalanceDisplay()
        }

        // Nhấn vào số dư để đổi nhanh chế độ tiền tệ (VND -> USD -> Cả hai)
        binding.tvTotalBalance.setOnClickListener {
            val current = CurrencyHelper.getSelectedCurrency(requireContext())
            val next = when (current) {
                CurrencyHelper.CURRENCY_VND -> CurrencyHelper.CURRENCY_USD
                CurrencyHelper.CURRENCY_USD -> CurrencyHelper.CURRENCY_BOTH
                else -> CurrencyHelper.CURRENCY_VND
            }
            CurrencyHelper.saveCurrency(requireContext(), next)
            loadData() // Tải lại toàn bộ dữ liệu để cập nhật định dạng
            Toast.makeText(requireContext(), "Chế độ hiển thị: $next", Toast.LENGTH_SHORT).show()
        }

        // Click vào ảnh đại diện hoặc khung ảnh để thay đổi
        val clickListener = View.OnClickListener {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }
        binding.flProfile.setOnClickListener(clickListener)
        binding.ivProfile.setOnClickListener(clickListener)

        // Điều khiển chuyển đổi tháng phân tích
        binding.btnPrevAnalysis.setOnClickListener {
            if (analysisMonth.get(Calendar.MONTH) > Calendar.JANUARY) {
                analysisMonth.add(Calendar.MONTH, -1)
                updateAnalysisUI()
            }
        }

        binding.btnNextAnalysis.setOnClickListener {
            if (analysisMonth.get(Calendar.MONTH) < Calendar.DECEMBER) {
                analysisMonth.add(Calendar.MONTH, 1)
                updateAnalysisUI()
            }
        }

        loadAvatar() // Tải ảnh đại diện đã lưu
        setupQuickActions() // Thiết lập các sự kiện nút chức năng nhanh
        setupToggle() // Thiết lập thanh gạt Chi tiêu/Thu nhập
        updateAnalysisUI() // Cập nhật giao diện phân tích theo tháng hiện tại
    }

    override fun onResume() {
        super.onResume()
        // Làm mới dữ liệu mỗi khi người dùng quay lại màn hình chính từ các Activity khác
        loadData()
        
        // Cập nhật lại tên trong trường hợp nó đã được thay đổi ở màn hình Profile
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val name = prefs.getString("user_name", "")
        binding.tvUserName.text = name?.uppercase() ?: ""
    }

    // Lưu URI ảnh đại diện vào SharedPreferences
    private fun saveAvatarUri(uri: Uri) {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("avatar_uri", uri.toString()).apply()
    }

    // Sao chép ảnh được chọn vào bộ nhớ trong của ứng dụng để tránh mất quyền truy cập sau này
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

    // Tải và hiển thị ảnh đại diện từ đường dẫn đã lưu
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
                // Nếu là content URI, thử hiển thị trực tiếp (có thể thất bại nếu hết quyền)
                try {
                    binding.ivProfile.setImageURI(uri)
                } catch (e: Exception) {
                    binding.ivProfile.setImageResource(R.drawable.ic_piggy_bank)
                }
            }
        }
    }

    // Cài đặt thanh chuyển đổi Thu nhập/Chi tiêu cho biểu đồ tròn
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

    // Gán các sự kiện chuyển màn hình cho các nút chức năng
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

        // Các nút bấm nhanh (Quick action pills)
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
        binding.actionGoals.actionIcon.setImageResource(R.drawable.ic_piggy_bank)
        binding.actionGoals.actionText.text = "Mục tiêu/Goals"

        // Nút thêm giao dịch khi biểu đồ trống
        binding.btnEmptyAdd.setOnClickListener {
            startActivity(Intent(requireContext(), AddTransactionActivity::class.java))
        }
    }

    // Cập nhật giao diện phần phân tích tháng (tiêu đề và nút ẩn/hiện)
    private fun updateAnalysisUI() {
        val monthNum = analysisMonth.get(Calendar.MONTH) + 1
        binding.tvChartTitle.text = "Phân tích tháng $monthNum"
        
        // Giới hạn trong năm hiện tại
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        analysisMonth.set(Calendar.YEAR, currentYear)

        val isFirstMonth = analysisMonth.get(Calendar.MONTH) == Calendar.JANUARY
        val isLastMonth = analysisMonth.get(Calendar.MONTH) == Calendar.DECEMBER
        
        binding.btnPrevAnalysis.visibility = if (isFirstMonth) View.INVISIBLE else View.VISIBLE
        binding.btnNextAnalysis.visibility = if (isLastMonth) View.INVISIBLE else View.VISIBLE

        loadData()
    }

    private var loadDataJob: kotlinx.coroutines.Job? = null
    // Tải dữ liệu từ Room và cập nhật toàn bộ giao diện ví
    private fun loadData() {
        loadDataJob?.cancel()
        
        val selectedMonth = analysisMonth.get(Calendar.MONTH) + 1
        val selectedYear = analysisMonth.get(Calendar.YEAR)
        val monthStr = String.format("%02d/%04d", selectedMonth, selectedYear)

        loadDataJob = viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            db.transactionDao().getAllTransactionsFlow().collect { all ->
                // Lọc giao dịch theo tháng được chọn để hiển thị biểu đồ
                val filteredForMonth = all.filter { it.date.endsWith(monthStr) }
                
                var totalInc = 0.0
                var totalExp = 0.0
                // Tính toán tổng thu nhập và chi tiêu để ra số dư
                all.forEach {
                    val amt = it.amount
                    if (it.isExpense) totalExp += amt else totalInc += amt
                }
                actualBalance = totalInc - totalExp
                
                withContext(Dispatchers.Main) {
                    updateBalanceDisplay()
                    updateDonutChart(filteredForMonth)
                    updateLineChart(all)
                    renderRecent(all.take(10))
                    renderFunds()
                }
            }
        }
    }

    // Vẽ biểu đồ tròn (Donut) dựa trên các hạng mục chi tiêu/thu nhập
    private fun updateDonutChart(transactions: List<Transaction>) {
        val filtered = transactions.filter { it.isExpense == isExpenseMode }
        if (filtered.isEmpty()) {
            binding.pieChart.visibility = View.GONE
            binding.chartLegend.visibility = View.GONE
            binding.layoutEmptyAnalysis.visibility = View.VISIBLE
            binding.pieChart.setSlices(emptyList())
            binding.chartLegend.removeAllViews()
            return
        }

        binding.pieChart.visibility = View.VISIBLE
        binding.chartLegend.visibility = View.VISIBLE
        binding.layoutEmptyAnalysis.visibility = View.GONE

        // Nhóm theo hạng mục và tính tổng tiền của từng hạng mục
        val categoryTotals = filtered.groupBy { it.category }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val colors = if (isExpenseMode) {
            listOf(0xFF4A5BCC.toInt(), 0xFFEA5455.toInt(), 0xFFD131F5.toInt(), 0xFFFF9F43.toInt(), 0xFF00CFE8.toInt())
        } else {
            listOf(0xFF28C76F.toInt(), 0xFF48DA89.toInt(), 0xFF10AC84.toInt(), 0xFF00FF00.toInt(), 0xFF20BF6B.toInt())
        }

        // Lấy 5 hạng mục có tổng tiền cao nhất để hiển thị
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

    // Vẽ biểu đồ đường hiển thị biến động trong tuần hiện tại
    private fun updateLineChart(transactions: List<Transaction>) {
        val spendArr = FloatArray(7) { 0f }
        val incomeArr = FloatArray(7) { 0f }
        val cal = Calendar.getInstance()
        val currentWeek = cal.get(Calendar.WEEK_OF_YEAR)

        transactions.forEach { t ->
            cal.timeInMillis = t.timestamp
            if (cal.get(Calendar.WEEK_OF_YEAR) == currentWeek) {
                // Calendar.DAY_OF_WEEK: Chủ nhật=1, Thứ hai=2...
                var dayIdx = cal.get(Calendar.DAY_OF_WEEK) - 2 // Chuyển về Thứ hai=0
                if (dayIdx < 0) dayIdx = 6 // Chủ nhật=6
                
                val amt = t.amount.toFloat()
                if (t.isExpense) spendArr[dayIdx] += amt else incomeArr[dayIdx] += amt
            }
        }
        binding.lineChart.setData(spendArr, incomeArr)
    }

    // Cập nhật số dư hiển thị, xử lý việc ẩn số dư bằng dấu *
    private fun updateBalanceDisplay() {
        if (isBalanceVisible) {
            binding.tvTotalBalance.text = AppUtils.formatCurrency(actualBalance, requireContext())
            binding.ivToggleBalance.setImageResource(R.drawable.ic_eye)
        } else {
            binding.tvTotalBalance.text = "********"
            binding.ivToggleBalance.setImageResource(R.drawable.ic_eye_off)
        }
    }

    // Hiển thị danh sách các giao dịch gần nhất (tối đa 10)
    private fun renderRecent(list: List<Transaction>) {
        binding.recentList.removeAllViews()
        if (list.isEmpty()) {
            val emptyView = LayoutInflater.from(requireContext()).inflate(R.layout.item_empty_state, binding.recentList, false)
            binding.recentList.addView(emptyView)
            return
        }
        list.forEach { trans ->
            val item = LayoutInflater.from(requireContext()).inflate(R.layout.item_stats_entry, binding.recentList, false)
            
            // Hiển thị mô tả nếu có, nếu không thì hiện tên hạng mục
            val title = if (trans.description.isNullOrEmpty()) trans.category else trans.description
            item.findViewById<TextView>(R.id.tvTitle).text = title
            
            val amountVal = trans.amount
            item.findViewById<TextView>(R.id.tvAmount).text = "${if (trans.isExpense) "-" else "+"}${AppUtils.formatCurrency(amountVal, requireContext())}"
            item.findViewById<TextView>(R.id.tvAmount).setTextColor(
                ContextCompat.getColor(requireContext(), if (trans.isExpense) R.color.expense_red else R.color.income_green)
            )
            
            val timeSdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            item.findViewById<TextView>(R.id.tvDateTime).text = timeSdf.format(Date(trans.timestamp))

            binding.recentList.addView(item)
        }
    }

    // Hiển thị danh sách các quỹ mục tiêu / quỹ chung
    private fun renderFunds() {
        binding.fundsContainer.removeAllViews()
        viewLifecycleOwner.lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            val funds = withContext(Dispatchers.IO) { db.fundDao().getAllFunds() }
            funds.forEach { fund ->
                val item = LayoutInflater.from(requireContext()).inflate(R.layout.item_fund_premium, binding.fundsContainer, false)
                item.findViewById<TextView>(R.id.tvGoalName).text = fund.name
                item.findViewById<TextView>(R.id.tvGoalProgress).text = "${AppUtils.formatCurrency(fund.currentAmount, requireContext())} / ${AppUtils.formatCurrency(fund.targetAmount, requireContext())}"
                item.findViewById<ImageView>(R.id.ivGoalIcon).setImageResource(R.drawable.ic_piggy_bank)
                
                val percent = if (fund.targetAmount > 0) (fund.currentAmount / fund.targetAmount * 100).toInt() else 0
                item.findViewById<TextView>(R.id.tvGoalPercent).text = "$percent%"
                item.findViewById<ProgressBar>(R.id.pbGoal).progress = percent.coerceIn(0, 100)
                
                // Nút yêu thích (ghim quỹ)
                val btnPin = item.findViewById<ImageButton>(R.id.btnDeleteFund)
                btnPin.setImageResource(R.drawable.ic_heart)
                btnPin.setColorFilter(if (fund.isPinned) ContextCompat.getColor(requireContext(), R.color.expense_red) else ContextCompat.getColor(requireContext(), R.color.text_hint))
                
                btnPin.setOnClickListener {
                    fund.isPinned = !fund.isPinned
                    lifecycleScope.launch(Dispatchers.IO) { db.fundDao().update(fund) }
                    renderFunds()
                }

                // Nút chia sẻ (mời thành viên)
                item.findViewById<ImageButton>(R.id.btnShareFund).setOnClickListener {
                    showAddMemberDialog(fund)
                }

                // Nhấn giữ để xem menu tùy chọn xóa/sửa
                item.setOnLongClickListener {
                    showEditDeleteDialog(fund)
                    true
                }

                // Nộp tiền vào quỹ
                item.findViewById<Button>(R.id.btnDeposit).setOnClickListener {
                    showAmountDialog(fund, isDeposit = true)
                }

                // Rút tiền từ quỹ
                item.findViewById<Button>(R.id.btnWithdraw).setOnClickListener {
                    showAmountDialog(fund, isDeposit = false)
                }

                binding.fundsContainer.addView(item)
            }
        }
    }

    // Hiển thị BottomSheet các tùy chọn cho quỹ (Sửa, Thêm thành viên, Xem đóng góp, Xóa)
    private fun showEditDeleteDialog(fund: Fund) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_vivid_options, null)
        val dialog = com.google.android.material.bottomsheet.BottomSheetDialog(requireContext(), R.style.CustomBottomSheetDialog)
        dialog.setContentView(view)

        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val isOwner = fund.ownerId == currentUserId

        val deleteContainer = view.findViewById<LinearLayout>(R.id.btnDeleteOption)
        val deleteText = deleteContainer.getChildAt(1) as? TextView
        
        // Phân biệt văn bản giữa chủ quỹ và thành viên được mời
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

    // Hộp thoại xác nhận rời khỏi quỹ chung
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

    // Xử lý rời quỹ và hoàn lại tiền đóng góp vào ví chính
    private fun leaveFundAndRefund(fund: Fund) {
        lifecycleScope.launch(Dispatchers.IO) {
            val syncManager = FirebaseSyncManager(requireContext())
            syncManager.leaveFund(fund)
            
            // Đồng bộ lại để xóa khỏi máy địa phương
            syncManager.syncFunds()
            
            withContext(Dispatchers.Main) {
                loadData()
                Toast.makeText(context, "Bạn đã rời quỹ và nhận lại tiền", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Hộp thoại xác nhận giải thể quỹ
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

    // Xử lý giải thể quỹ và hoàn tiền cho tất cả mọi người thông qua FirebaseSyncManager
    private fun deleteFundAndRefund(fund: Fund) {
        lifecycleScope.launch(Dispatchers.IO) {
            val syncManager = FirebaseSyncManager(requireContext())
            syncManager.deleteFundAndRefund(fund)
            
            // Đồng bộ lại để xóa khỏi máy địa phương
            syncManager.syncFunds()
            
            withContext(Dispatchers.Main) {
                loadData()
                Toast.makeText(context, "Quỹ đã được giải thể và hoàn tiền", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Hiển thị danh sách số tiền đóng góp của từng thành viên
    private fun showContributionsDialog(fund: Fund) {
        val container = LinearLayout(requireContext())
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(60, 40, 60, 40)

        lifecycleScope.launch {
            val dbRef = FirebaseDatabase.getInstance().reference
            
            fund.memberContributions.forEach { (uid, amount) ->
                // Lấy tên người dùng từ Firebase
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

    // Mời người khác vào quỹ chung qua Email
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

    // Cập nhật thông tin quỹ (ví dụ: đổi tên)
    private fun showUpdateFundDialog(fund: Fund) {
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_category, null) // Dùng lại layout cũ để tối ưu
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

    // Hộp thoại nhập số tiền khi Nộp hoặc Rút khỏi quỹ
    private fun showAmountDialog(fund: Fund, isDeposit: Boolean) {
        val layout = LinearLayout(requireContext())
        layout.orientation = LinearLayout.HORIZONTAL
        layout.setPadding(50, 40, 50, 10)

        val et = EditText(requireContext())
        et.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
        et.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
        et.hint = "Nhập số tiền..."
        
        val tvCurrency = TextView(requireContext())
        val savedCurrency = CurrencyHelper.getSelectedCurrency(requireContext())
        var currentCurrency = if (savedCurrency == CurrencyHelper.CURRENCY_BOTH) CurrencyHelper.CURRENCY_VND else savedCurrency
        tvCurrency.text = currentCurrency
        tvCurrency.setPadding(20, 0, 20, 0)
        tvCurrency.textSize = 16f
        tvCurrency.setTextColor(ContextCompat.getColor(requireContext(), R.color.primary_blue))
        tvCurrency.isClickable = true
        tvCurrency.setOnClickListener {
            currentCurrency = if (currentCurrency == CurrencyHelper.CURRENCY_VND) CurrencyHelper.CURRENCY_USD else CurrencyHelper.CURRENCY_VND
            tvCurrency.text = currentCurrency
        }

        layout.addView(et)
        layout.addView(tvCurrency)
        
        android.app.AlertDialog.Builder(requireContext())
            .setTitle(if (isDeposit) "Góp vào quỹ" else "Rút từ quỹ")
            .setView(layout)
            .setPositiveButton("Xác nhận") { _, _ ->
                val inputAmount = et.text.toString().toDoubleOrNull() ?: 0.0
                if (inputAmount > 0) {
                    val finalAmount = if (currentCurrency == CurrencyHelper.CURRENCY_USD) {
                        inputAmount * CurrencyHelper.EXCHANGE_RATE_USD_TO_VND
                    } else {
                        inputAmount
                    }
                    processFundTransaction(fund, finalAmount, isDeposit)
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    // Xử lý logic nghiệp vụ khi có giao dịch liên quan đến quỹ
    private fun processFundTransaction(fund: Fund, amount: Double, isDeposit: Boolean) {
        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            val syncManager = FirebaseSyncManager(requireContext())
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
            
            // 1. Cập nhật số tiền trong quỹ và ghi nhận đóng góp cá nhân
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
            syncManager.createFund(fund) // Cập nhật trạng thái quỹ lên Realtime Database

            // 2. Tạo bản ghi giao dịch trong ví chính
            val trans = Transaction(
                amount = amount,
                category = if (isDeposit) "Góp quỹ" else "Rút tiền quỹ",
                description = "${if (isDeposit) "Góp vào" else "Rút từ"} quỹ ${fund.name}",
                date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                isExpense = isDeposit, // Góp quỹ được tính là 1 khoản chi từ ví chính, Rút là thu nhập
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
