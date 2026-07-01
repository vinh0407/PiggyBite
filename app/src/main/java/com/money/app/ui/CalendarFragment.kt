package com.money.app.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.data.Transaction
import com.money.app.util.AppUtils
import com.money.app.util.FirebaseSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment Lịch (Calendar): Hiển thị giao dịch dưới dạng lịch biểu hàng tháng.
 * Tính năng chính:
 * - Xem tổng quan thu/chi theo từng ngày trên lưới lịch.
 * - Xem chi tiết danh sách giao dịch của một ngày cụ thể khi nhấn chọn.
 * - Xuất và Nhập dữ liệu giao dịch dưới định dạng CSV (Sao lưu thủ công).
 */
class CalendarFragment : Fragment() {

    private lateinit var rvDailyList: RecyclerView
    private lateinit var rvCalendarGrid: RecyclerView
    private lateinit var tvIncVal: TextView
    private lateinit var tvExpVal: TextView
    private lateinit var tvBalVal: TextView
    private lateinit var tvCalendarMonth: TextView
    private lateinit var tvSelectedDateLabel: TextView
    private lateinit var tvDailyCount: TextView

    private var allTransactions = listOf<Transaction>()
    private val displayedMonth = Calendar.getInstance() // Tháng đang hiển thị trên lịch
    private var selectedDate = Calendar.getInstance() // Ngày đang được người dùng chọn
    
    // Model dữ liệu cho từng ô ngày trên lịch
    data class CalendarDay(
        val day: Int, 
        val month: Int, 
        val year: Int, 
        val isCurrentMonth: Boolean, // Đánh dấu ngày thuộc tháng hiện tại hay tháng lân cận
        var totalAmount: Double = 0.0, // Tổng số dư (Thu - Chi) của ngày đó
        var hasTransactions: Boolean = false
    )

    // Xử lý tạo file CSV để xuất dữ liệu
    private val createDocument = registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        uri?.let { exportToCsv(it) }
    }

    // Xử lý mở file CSV để nhập dữ liệu
    private val openDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { importFromCsv(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Khởi tạo các View
        rvDailyList = view.findViewById(R.id.rvDailyList)
        rvCalendarGrid = view.findViewById(R.id.rvCalendarGrid)
        tvCalendarMonth = view.findViewById(R.id.tvCalendarMonth)
        tvSelectedDateLabel = view.findViewById(R.id.tvSelectedDateLabel)
        tvDailyCount = view.findViewById(R.id.tvDailyCount)

        rvDailyList.layoutManager = LinearLayoutManager(requireContext())
        rvCalendarGrid.layoutManager = androidx.recyclerview.widget.GridLayoutManager(requireContext(), 7) // Lịch 7 cột
        
        // Cài đặt nhãn cho phần tóm tắt tháng
        val summaryInc = view.findViewById<View>(R.id.summaryInc)
        val summaryExp = view.findViewById<View>(R.id.summaryExp)
        val summaryBal = view.findViewById<View>(R.id.summaryBal)
        
        summaryInc.findViewById<TextView>(R.id.tvLabel).text = "Thu"
        summaryExp.findViewById<TextView>(R.id.tvLabel).text = "Chi"
        summaryBal.findViewById<TextView>(R.id.tvLabel).text = "Còn lại"
        
        tvIncVal = summaryInc.findViewById(R.id.tvValue)
        tvExpVal = summaryExp.findViewById(R.id.tvValue)
        tvBalVal = summaryBal.findViewById(R.id.tvValue)

        // Nút Xuất/Nhập file
        view.findViewById<View>(R.id.btnExport)?.setOnClickListener {
            showExportImportDialog()
        }

        // Điều hướng tháng
        view.findViewById<View>(R.id.btnPrevMonth)?.setOnClickListener {
            displayedMonth.add(Calendar.MONTH, -1)
            updateCalendar()
        }

        view.findViewById<View>(R.id.btnNextMonth)?.setOnClickListener {
            displayedMonth.add(Calendar.MONTH, 1)
            updateCalendar()
        }

        loadMonthData()
    }

    override fun onResume() {
        super.onResume()
        loadMonthData()
    }

    /**
     * Cập nhật giao diện lưới lịch và các thông số tóm tắt của tháng đang xem
     */
    private fun updateCalendar() {
        val sdf = SimpleDateFormat("'Tháng' MM 'năm' yyyy", Locale("vi", "VN"))
        tvCalendarMonth.text = sdf.format(displayedMonth.time)
        
        val days = generateCalendarDays(displayedMonth)
        
        // Tính toán tổng số tiền cho từng ngày dựa trên danh sách giao dịch
        days.forEach { day ->
            val dateStr = String.format("%02d/%02d/%04d", day.day, day.month + 1, day.year)
            val dailyTrans = allTransactions.filter { it.date == dateStr }
            if (dailyTrans.isNotEmpty()) {
                day.hasTransactions = true
                var total = 0.0
                dailyTrans.forEach { total += if (it.isExpense) -it.amount else it.amount }
                day.totalAmount = total
            }
        }
        
        rvCalendarGrid.adapter = CalendarGridAdapter(days)
        
        // Cập nhật tóm tắt thu chi cho cả tháng
        val monthStr = SimpleDateFormat("/MM/yyyy", Locale.getDefault()).format(displayedMonth.time)
        val filtered = allTransactions.filter { it.date.endsWith(monthStr) }
        var totalInc = 0.0
        var totalExp = 0.0
        filtered.forEach {
            if (it.isExpense) totalExp += it.amount else totalInc += it.amount
        }
        tvIncVal.text = AppUtils.formatCurrency(totalInc, requireContext())
        tvExpVal.text = AppUtils.formatCurrency(totalExp, requireContext())
        tvBalVal.text = AppUtils.formatCurrency(totalInc - totalExp, requireContext())
        
        val currentMonthHeader = SimpleDateFormat("MMMM, yyyy", Locale("vi", "VN")).format(displayedMonth.time)
        view?.findViewById<TextView>(R.id.tvCurrentMonth)?.text = currentMonthHeader.replaceFirstChar { it.uppercase() }
    }

    /**
     * Thuật toán tạo danh sách các ngày để hiển thị lên lưới 7x6 (42 ô)
     */
    private fun generateCalendarDays(month: Calendar): List<CalendarDay> {
        val days = mutableListOf<CalendarDay>()
        val cal = month.clone() as Calendar
        cal.set(Calendar.DAY_OF_MONTH, 1)
        
        // Xác định thứ của ngày đầu tiên trong tháng (điều chỉnh để Thứ 2 là cột đầu tiên)
        var firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 2
        if (firstDayOfWeek < 0) firstDayOfWeek = 6 // Chủ nhật
        
        // Thêm các ngày của tháng trước để lấp đầy hàng đầu tiên
        val prevMonth = cal.clone() as Calendar
        prevMonth.add(Calendar.MONTH, -1)
        val daysInPrevMonth = prevMonth.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in firstDayOfWeek - 1 downTo 0) {
            days.add(CalendarDay(daysInPrevMonth - i, prevMonth.get(Calendar.MONTH), prevMonth.get(Calendar.YEAR), false))
        }
        
        // Thêm các ngày của tháng hiện tại
        val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
        for (i in 1..daysInMonth) {
            days.add(CalendarDay(i, cal.get(Calendar.MONTH), cal.get(Calendar.YEAR), true))
        }
        
        // Thêm các ngày của tháng sau để lấp đầy lưới 42 ô
        val nextMonth = cal.clone() as Calendar
        nextMonth.add(Calendar.MONTH, 1)
        val remaining = 42 - days.size
        for (i in 1..remaining) {
            days.add(CalendarDay(i, nextMonth.get(Calendar.MONTH), nextMonth.get(Calendar.YEAR), false))
        }
        
        return days
    }

    /**
     * Adapter cho lưới lịch
     */
    inner class CalendarGridAdapter(private val days: List<CalendarDay>) : RecyclerView.Adapter<CalendarGridAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_calendar_day, parent, false)
            return ViewHolder(v)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val d = days[position]
            holder.tvDay.text = d.day.toString()
            // Làm mờ các ngày không thuộc tháng hiện tại
            holder.tvDay.alpha = if (d.isCurrentMonth) 1.0f else 0.3f
            
            // Hiển thị số tiền tổng của ngày (nếu có giao dịch)
            if (d.hasTransactions) {
                holder.tvAmount.visibility = View.VISIBLE
                val formatted = AppUtils.formatCurrency(Math.abs(d.totalAmount), requireContext())
                holder.tvAmount.text = "${if (d.totalAmount >= 0) "+" else "-"}$formatted"
                holder.tvAmount.setTextColor(ContextCompat.getColor(requireContext(), 
                    if (d.totalAmount >= 0) R.color.income_green else R.color.expense_red))
            } else {
                holder.tvAmount.visibility = View.INVISIBLE
            }
            
            // Hiển thị vòng tròn chọn ngày
            val isSelected = d.day == selectedDate.get(Calendar.DAY_OF_MONTH) && 
                             d.month == selectedDate.get(Calendar.MONTH) && 
                             d.year == selectedDate.get(Calendar.YEAR)
            holder.viewSelection.visibility = if (isSelected) View.VISIBLE else View.GONE
            
            holder.itemView.setOnClickListener {
                selectedDate.set(d.year, d.month, d.day)
                notifyDataSetChanged()
                val cal = Calendar.getInstance()
                cal.set(d.year, d.month, d.day)
                showDailyTransactions(cal.time) // Cập nhật danh sách giao dịch chi tiết bên dưới
            }
        }
        override fun getItemCount() = days.size
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvDay = v.findViewById<TextView>(R.id.tvDay)
            val tvAmount = v.findViewById<TextView>(R.id.tvDailyAmount)
            val viewSelection = v.findViewById<View>(R.id.viewSelection)
        }
    }

    /**
     * Hiển thị menu chọn Xuất hoặc Nhập file CSV
     */
    private fun showExportImportDialog() {
        val options = arrayOf("Xuất dữ liệu (CSV)", "Nhập dữ liệu (CSV)")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Dữ liệu chi tiêu")
            .setItems(options) { _, which ->
                if (which == 0) {
                    val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val userName = prefs.getString("user_name", "User") ?: "User"
                    val exportDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                    
                    val firstDate = if (allTransactions.isNotEmpty()) {
                        val sorted = allTransactions.sortedBy { it.timestamp }
                        val d = sorted.first().date // dd/MM/yyyy
                        d.replace("/", "")
                    } else "00000000"

                    val fileName = "${userName}_${firstDate}_$exportDate.csv"
                    createDocument.launch(fileName)
                } else {
                    openDocument.launch(arrayOf("text/*"))
                }
            }
            .show()
    }

    /**
     * Logic xuất toàn bộ giao dịch ra file CSV
     */
    private fun exportToCsv(uri: android.net.Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                requireContext().contentResolver.openOutputStream(uri)?.use { outputStream ->
                    val writer = outputStream.bufferedWriter()
                    writer.write("date,type,amount,category,note\n")
                    
                    val inFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val outFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

                    allTransactions.forEach { t ->
                        val type = if (t.isExpense) "expense" else "income"
                        val amount = t.amount.toLong()
                        
                        val formattedDate = try {
                            val d = inFormat.parse(t.date)
                            if (d != null) outFormat.format(d) else t.date
                        } catch (e: Exception) {
                            t.date
                        }
                        
                        writer.write("$formattedDate,$type,$amount,${t.category},${t.description ?: ""}\n")
                    }
                    writer.flush()
                }
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Đã xuất file thành công!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Lỗi khi xuất file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Logic nhập dữ liệu từ file CSV vào ứng dụng và đồng bộ lên Firebase
     */
    private fun importFromCsv(uri: android.net.Uri) {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val db = AppDatabase.getDatabase(requireContext())
                requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                    val reader = inputStream.bufferedReader()
                    val lines = reader.readLines()
                    if (lines.size > 1) {
                        val inFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                        val outFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        
                        for (i in 1 until lines.size) {
                            val line = lines[i]
                            if (line.isBlank()) continue
                            val parts = line.split(",")
                            if (parts.size >= 5) {
                                val rawDate = parts[0]
                                val formattedDate = try {
                                    val d = inFormat.parse(rawDate)
                                    if (d != null) outFormat.format(d) else rawDate
                                } catch (e: Exception) {
                                    rawDate
                                }

                                val trans = Transaction(
                                    date = formattedDate,
                                    category = parts[3],
                                    description = parts[4],
                                    amount = parts[2].toDoubleOrNull() ?: 0.0,
                                    isExpense = parts[1].lowercase() == "expense",
                                    timestamp = try { inFormat.parse(rawDate)?.time ?: System.currentTimeMillis() } catch(e: Exception) { System.currentTimeMillis() }
                                )
                                db.transactionDao().insert(trans)
                                FirebaseSyncManager(requireContext()).saveTransaction(trans)
                            }
                        }
                    }
                }
                withContext(Dispatchers.Main) {
                    loadMonthData()
                    Toast.makeText(context, "Nhập dữ liệu thành công!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Lỗi khi nhập file. Kiểm tra lại định dạng CSV.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    /**
     * Tải dữ liệu giao dịch từ Database cục bộ
     */
    private fun loadMonthData() {
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(requireContext())
            db.transactionDao().getAllTransactionsFlow().collect { all ->
                allTransactions = all
                withContext(Dispatchers.Main) {
                    updateCalendar()
                    showDailyTransactions(selectedDate.time)
                }
            }
        }
    }

    /**
     * Cập nhật danh sách giao dịch bên dưới lịch cho ngày đang chọn
     */
    private fun showDailyTransactions(date: Date) {
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        val displayDate = SimpleDateFormat("'Ngày' dd/MM/yyyy", Locale.getDefault()).format(date)
        tvSelectedDateLabel.text = displayDate
        
        val daily = allTransactions.filter { it.date == dateStr }
        tvDailyCount.text = "${daily.size} giao dịch"
        rvDailyList.adapter = DailyAdapter(daily)
    }

    /**
     * Adapter hiển thị từng dòng giao dịch trong ngày
     */
    inner class DailyAdapter(private val list: List<Transaction>) : RecyclerView.Adapter<DailyAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_stats_entry, parent, false)
            return ViewHolder(v)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val t = list[position]
            holder.tvTitle?.text = if (t.description.isNullOrEmpty()) t.category else t.description
            val amt = t.amount
            holder.tvAmount?.text = "${if (t.isExpense) "-" else "+"}${AppUtils.formatCurrency(amt, requireContext())}"
            holder.tvAmount?.setTextColor(ContextCompat.getColor(requireContext(), if (t.isExpense) R.color.expense_red else R.color.income_green))
            val timeSdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            holder.tvDate?.text = timeSdf.format(Date(t.timestamp))
        }
        override fun getItemCount() = list.size
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvTitle = v.findViewById<TextView>(R.id.tvTitle)
            val tvAmount = v.findViewById<TextView>(R.id.tvAmount)
            val tvDate = v.findViewById<TextView>(R.id.tvDateTime)
        }
    }
}
