package com.money.app.ui

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.money.app.R
import com.money.app.data.AppDatabase
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

/**
 * BottomSheetDialogFragment: Hộp thoại lựa chọn hạng mục giao dịch (Thu nhập/Chi tiêu).
 * Tính năng chính:
 * - Hiển thị danh sách hạng mục phân loại theo nhóm (Cố định, Thiết yếu, Giải trí, Giáo dục...).
 * - Hiển thị tổng số tiền đã chi tiêu cho từng nhóm trong tháng hiện tại ngay trên tiêu đề nhóm.
 * - Cho phép người dùng tạo thêm hạng mục tùy chỉnh kèm Emoji.
 * - Hỗ trợ chọn nhiều hạng mục cùng lúc.
 */
class CategorySelectionBottomSheet : BottomSheetDialogFragment() {

    // Giao diện để truyền hạng mục đã chọn về Activity/Fragment gọi nó
    interface OnCategorySelectedListener {
        fun onCategorySelected(categories: List<String>)
    }

    private var listener: OnCategorySelectedListener? = null
    private var isExpense: Boolean = true // Chế độ Chi tiêu hoặc Thu nhập
    private var initialSelectedCategories: List<String>? = null // Các hạng mục đã chọn sẵn

    private lateinit var groupOther: ChipGroup
    private lateinit var headerOther: View
    private lateinit var layoutExpense: View
    private lateinit var layoutIncome: View
    
    // Các tiêu đề nhóm để hiển thị tổng tiền
    private lateinit var headerFixed: TextView
    private lateinit var headerEssential: TextView
    private lateinit var headerFun: TextView
    private lateinit var headerEducation: TextView
    private lateinit var headerIncomeFixed: TextView
    private lateinit var headerIncomeFlex: TextView
    
    private lateinit var tvCurrentMonth: TextView
    private lateinit var tvTitle: TextView
    
    private val df = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
    private val displayedMonth = Calendar.getInstance()

    companion object {
        fun newInstance(isExpense: Boolean, selected: ArrayList<String>): CategorySelectionBottomSheet {
            val fragment = CategorySelectionBottomSheet()
            val args = Bundle()
            args.putBoolean("isExpense", isExpense)
            args.putStringArrayList("selected", selected)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnCategorySelectedListener) {
            listener = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_category_selection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Nhận tham số truyền vào
        isExpense = arguments?.getBoolean("isExpense") ?: true
        initialSelectedCategories = arguments?.getStringArrayList("selected")

        tvTitle = view.findViewById(R.id.tvTitle)
        tvTitle.text = if (isExpense) "Danh mục chi tiêu" else "Danh mục thu nhập"

        // Hiển thị layout tương ứng với chế độ Thu nhập/Chi tiêu
        layoutExpense = view.findViewById(R.id.layoutExpense)
        layoutIncome = view.findViewById(R.id.layoutIncome)
        layoutExpense.visibility = if (isExpense) View.VISIBLE else View.GONE
        layoutIncome.visibility = if (isExpense) View.GONE else View.VISIBLE

        groupOther = view.findViewById(R.id.groupOther)
        headerOther = view.findViewById(R.id.headerOther)
        
        headerFixed = view.findViewById(R.id.headerFixed)
        headerEssential = view.findViewById(R.id.headerEssential)
        headerFun = view.findViewById(R.id.headerFun)
        headerEducation = view.findViewById(R.id.headerEducation)
        headerIncomeFixed = view.findViewById(R.id.headerIncomeFixed)
        headerIncomeFlex = view.findViewById(R.id.headerIncomeFlex)
        
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth)
        val btnPrevMonth: ImageView = view.findViewById(R.id.btnPrevMonth)
        val btnNextMonth: ImageView = view.findViewById(R.id.btnNextMonth)
        val btnClose: ImageButton = view.findViewById(R.id.btnClose)
        val btnDone: ImageButton = view.findViewById(R.id.btnDone)
        val btnCreateNew: Button = view.findViewById(R.id.btnCreateNew)

        loadCustomCategories() // Tải các hạng mục do người dùng tự tạo
        setupExistingCategoryLongClicks() // Nhấn giữ để xóa hạng mục
        updateMonthDisplay() // Cập nhật nhãn tháng
        calculateAndDisplayTotals() // Tính toán tổng tiền theo nhóm
        
        // Khôi phục trạng thái các Chip đã chọn trước đó
        initialSelectedCategories?.let { restoreSelection(it) }

        btnClose.setOnClickListener { dismiss() }

        btnDone.setOnClickListener {
            val selectedChips = findAllSelectedChips()
            listener?.onCategorySelected(selectedChips)
            dismiss()
        }

        btnCreateNew.setOnClickListener { showCreateCategoryDialog() }

        // Cho phép chuyển đổi tháng để xem báo cáo nhanh ngay trong hộp thoại này
        btnPrevMonth.setOnClickListener {
            displayedMonth.add(Calendar.MONTH, -1)
            updateMonthDisplay()
            calculateAndDisplayTotals()
        }

        btnNextMonth.setOnClickListener {
            displayedMonth.add(Calendar.MONTH, 1)
            updateMonthDisplay()
            calculateAndDisplayTotals()
        }
    }

    private fun updateMonthDisplay() {
        val sdf = SimpleDateFormat("'Tháng' MM, yyyy", Locale("vi", "VN"))
        tvCurrentMonth.text = sdf.format(displayedMonth.time)
    }

    /**
     * Tự động duyệt qua tất cả các Chip có sẵn trong Layout để gán sự kiện nhấn giữ
     */
    private fun setupExistingCategoryLongClicks() {
        fun findAndSetup(view: View) {
            if (view is Chip) {
                view.setOnLongClickListener {
                    showDeleteConfirmation(view.text.toString(), isBuiltIn = true, view = view)
                    true
                }
            } else if (view is ViewGroup) {
                for (i in 0 until view.childCount) findAndSetup(view.getChildAt(i))
            }
        }
        findAndSetup(requireView())
    }

    /**
     * Truy vấn Database Room để tính tổng số tiền của từng nhóm hạng mục trong tháng
     */
    private fun calculateAndDisplayTotals() {
        viewLifecycleOwner.lifecycleScope.launch {
            val context = context ?: return@launch
            val db = AppDatabase.getDatabase(context)
            
            // Xác định khoảng thời gian của tháng đang hiển thị
            val calendar = displayedMonth.clone() as Calendar
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.set(Calendar.HOUR_OF_DAY, 0); calendar.set(Calendar.MINUTE, 0); calendar.set(Calendar.SECOND, 0)
            val startTime = calendar.timeInMillis
            
            calendar.add(Calendar.MONTH, 1); calendar.add(Calendar.SECOND, -1)
            val endTime = calendar.timeInMillis

            val transactions = db.transactionDao().getTransactionsInTimeRange(startTime, endTime)
            val filteredTransactions = transactions.filter { it.isExpense == isExpense }
            
            // Cập nhật nhãn tiêu đề kèm tổng tiền
            if (isExpense) {
                val fixedTotal = calculateGroupTotal(filteredTransactions, listOf("🏠 Thuê nhà", "❤️ Hiếu hỉ"))
                headerFixed.text = "Cố định (${df.format(fixedTotal)}đ)"

                val essentialTotal = calculateGroupTotal(filteredTransactions, listOf("☕ Cafe", "🥐 Ăn uống", "🥦 Đi chợ", "💡 Điện/ Nước", "🛵 Di chuyển", "⛽ Xăng"))
                headerEssential.text = "Nhu cầu thiết yếu (${df.format(essentialTotal)}đ)"

                val funTotal = calculateGroupTotal(filteredTransactions, listOf("🛍️ Mua sắm", "🤪 Shoppee/ Tiktok", "🛫 Du lịch"))
                headerFun.text = "Hưởng thụ & Giải trí (${df.format(funTotal)}đ)"

                val educationTotal = calculateGroupTotal(filteredTransactions, listOf("📚 Học tập"))
                headerEducation.text = "Giáo dục (${df.format(educationTotal)}đ)"
            } else {
                val incomeFixed = calculateGroupTotal(filteredTransactions, listOf("💵 Lương", "🤝 Cho thuê nhà"))
                headerIncomeFixed.text = "Cố định (${df.format(incomeFixed)}đ)"

                val incomeFlex = calculateGroupTotal(filteredTransactions, listOf("💹 Đầu tư", "💻 Freelance", "🌹 Tiếp thị liên kết", "💰 Thu nhập khác"))
                headerIncomeFlex.text = "Linh hoạt (${df.format(incomeFlex)}đ)"
            }
        }
    }

    private fun calculateGroupTotal(transactions: List<com.money.app.data.Transaction>, categoryNames: List<String>): Double {
        var total = 0.0
        transactions.forEach { trans ->
            val transCats = trans.category.split(",").map { it.trim() }
            if (transCats.any { categoryNames.contains(it) }) {
                total += trans.amount
            }
        }
        return total
    }

    /**
     * Tải các hạng mục tùy chỉnh từ SharedPreferences
     */
    private fun loadCustomCategories() {
        val prefs = requireContext().getSharedPreferences("custom_categories", Context.MODE_PRIVATE)
        val categories = prefs.getStringSet("categories", emptySet()) ?: emptySet()
        
        groupOther.removeAllViews()
        if (categories.isNotEmpty()) {
            headerOther.visibility = View.VISIBLE
            categories.forEach { addCategoryChip(it) }
        } else {
            headerOther.visibility = View.GONE
        }
    }

    private fun saveCustomCategory(category: String) {
        val prefs = requireContext().getSharedPreferences("custom_categories", Context.MODE_PRIVATE)
        val categories = prefs.getStringSet("categories", emptySet())?.toMutableSet() ?: mutableSetOf()
        categories.add(category)
        prefs.edit().putStringSet("categories", categories).apply()
        loadCustomCategories()
    }

    private fun deleteCustomCategory(category: String) {
        val prefs = requireContext().getSharedPreferences("custom_categories", Context.MODE_PRIVATE)
        val categories = prefs.getStringSet("categories", emptySet())?.toMutableSet() ?: mutableSetOf()
        categories.remove(category)
        prefs.edit().putStringSet("categories", categories).apply()
        loadCustomCategories()
    }

    private fun addCategoryChip(text: String) {
        val chip = Chip(ContextThemeWrapper(requireContext(), R.style.CategoryChipStyle))
        chip.text = text
        chip.isCheckable = true
        chip.setOnLongClickListener {
            showDeleteConfirmation(text, isBuiltIn = false)
            true
        }
        groupOther.addView(chip)
    }

    private fun showDeleteConfirmation(category: String, isBuiltIn: Boolean, view: View? = null) {
        AlertDialog.Builder(requireContext())
            .setTitle("Xóa danh mục")
            .setMessage("Bạn có chắc muốn ẩn/xóa '$category'?")
            .setPositiveButton("Xóa") { _, _ -> 
                if (isBuiltIn) view?.visibility = View.GONE
                else deleteCustomCategory(category)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun restoreSelection(selected: List<String>) {
        fun findAndCheck(view: View) {
            if (view is Chip) {
                if (selected.contains(view.text.toString())) view.isChecked = true
            } else if (view is ViewGroup) {
                for (i in 0 until view.childCount) findAndCheck(view.getChildAt(i))
            }
        }
        findAndCheck(requireView())
    }

    private fun findAllSelectedChips(): List<String> {
        val selected = mutableListOf<String>()
        fun findIn(view: View) {
            if (view is Chip && view.isChecked) selected.add(view.text.toString())
            else if (view is ViewGroup) {
                for (i in 0 until view.childCount) findIn(view.getChildAt(i))
            }
        }
        findIn(requireView())
        return selected
    }

    /**
     * Hiển thị hộp thoại tạo hạng mục mới kèm bộ chọn Emoji
     */
    private fun showCreateCategoryDialog() {
        val bottomSheetDialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_category, null)
        bottomSheetDialog.setContentView(view)

        val btnSave = view.findViewById<Button>(R.id.btnSaveCategory)
        val btnSelectEmoji = view.findViewById<TextView>(R.id.btnSelectEmoji)
        val etCategoryName = view.findViewById<EditText>(R.id.etCategoryName)

        var selectedEmoji: String = "💡"
        btnSelectEmoji.text = selectedEmoji
        btnSelectEmoji.setOnClickListener {
            showEmojiPicker { emoji ->
                selectedEmoji = emoji
                btnSelectEmoji.text = emoji
            }
        }

        btnSave.setOnClickListener {
            val name = etCategoryName.text.toString()
            if (name.isNotEmpty()) {
                saveCustomCategory("$selectedEmoji $name")
                bottomSheetDialog.dismiss()
            } else {
                Toast.makeText(requireContext(), "Vui lòng nhập tên", Toast.LENGTH_SHORT).show()
            }
        }
        bottomSheetDialog.show()
    }

    private fun showEmojiPicker(onEmojiSelected: (String) -> Unit) {
        val dialog = BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_emoji_picker, null)
        dialog.setContentView(view)

        val gridView: GridView = view.findViewById(R.id.emojiGrid)
        val emojis = resources.getStringArray(R.array.emojis)
        val adapter = object : ArrayAdapter<String>(requireContext(), R.layout.item_emoji, emojis) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val tv = super.getView(position, convertView, parent) as TextView
                tv.text = getItem(position)
                return tv
            }
        }
        gridView.adapter = adapter
        gridView.setOnItemClickListener { _, _, position, _ ->
            onEmojiSelected(emojis[position])
            dialog.dismiss()
        }
        dialog.show()
    }
}
