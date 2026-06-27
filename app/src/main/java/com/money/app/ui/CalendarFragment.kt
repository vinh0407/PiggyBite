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

class CalendarFragment : Fragment() {

    private lateinit var rvDailyList: RecyclerView
    private lateinit var tvIncVal: TextView
    private lateinit var tvExpVal: TextView
    private lateinit var tvBalVal: TextView
    private lateinit var calendarView: android.widget.CalendarView
    private lateinit var tvSelectedDateLabel: TextView
    private lateinit var tvDailyCount: TextView

    private var allTransactions = listOf<Transaction>()

    private val createDocument = registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri ->
        uri?.let { exportToCsv(it) }
    }

    private val openDocument = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { importFromCsv(it) }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_calendar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        rvDailyList = view.findViewById(R.id.rvDailyList)
        calendarView = view.findViewById(R.id.calendarView)
        tvSelectedDateLabel = view.findViewById(R.id.tvSelectedDateLabel)
        tvDailyCount = view.findViewById(R.id.tvDailyCount)

        rvDailyList.layoutManager = LinearLayoutManager(requireContext())
        
        val summaryInc = view.findViewById<View>(R.id.summaryInc)
        val summaryExp = view.findViewById<View>(R.id.summaryExp)
        val summaryBal = view.findViewById<View>(R.id.summaryBal)
        
        summaryInc.findViewById<TextView>(R.id.tvLabel).text = "Thu"
        summaryExp.findViewById<TextView>(R.id.tvLabel).text = "Chi"
        summaryBal.findViewById<TextView>(R.id.tvLabel).text = "Còn lại"
        
        tvIncVal = summaryInc.findViewById(R.id.tvValue)
        tvExpVal = summaryExp.findViewById(R.id.tvValue)
        tvBalVal = summaryBal.findViewById(R.id.tvValue)

        view.findViewById<View>(R.id.btnExport)?.setOnClickListener {
            showExportImportDialog()
        }

        calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
            val cal = Calendar.getInstance()
            cal.set(year, month, dayOfMonth)
            showDailyTransactions(cal.time)
        }

        loadMonthData()
    }

    private fun showExportImportDialog() {
        val options = arrayOf("Xuất dữ liệu (CSV)", "Nhập dữ liệu (CSV)")
        android.app.AlertDialog.Builder(requireContext())
            .setTitle("Dữ liệu chi tiêu")
            .setItems(options) { _, which ->
                if (which == 0) {
                    val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val userName = prefs.getString("user_name", "User") ?: "User"
                    val exportDate = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(Date())
                    
                    // Find first transaction date
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
                        val amount = AppUtils.parseAmount(t.amount).toLong()
                        
                        // Convert date format
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
                    Toast.makeText(context, "Đã xuất file vào thư mục bạn chọn!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(context, "Lỗi khi xuất file", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

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
                                // Convert date back to app format
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
                                    amount = parts[2],
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

    private fun loadMonthData() {
        val cal = Calendar.getInstance()
        val currentMonthStr = SimpleDateFormat("/MM/yyyy", Locale.getDefault()).format(cal.time)

        lifecycleScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(requireContext())
            allTransactions = db.transactionDao().getAllTransactions()
            val filtered = allTransactions.filter { it.date.endsWith(currentMonthStr) }
            
            var totalInc = 0.0
            var totalExp = 0.0
            filtered.forEach {
                val amt = AppUtils.parseAmount(it.amount)
                if (it.isExpense) totalExp += amt else totalInc += amt
            }

            withContext(Dispatchers.Main) {
                tvIncVal.text = AppUtils.formatCurrency(totalInc)
                tvExpVal.text = AppUtils.formatCurrency(totalExp)
                tvBalVal.text = AppUtils.formatCurrency(totalInc - totalExp)
                showDailyTransactions(Date()) // Default today
            }
        }
    }

    private fun showDailyTransactions(date: Date) {
        val dateStr = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date)
        val displayDate = SimpleDateFormat("'Ngày' dd/MM/yyyy", Locale.getDefault()).format(date)
        tvSelectedDateLabel.text = displayDate
        
        val daily = allTransactions.filter { it.date == dateStr }
        tvDailyCount.text = "${daily.size} giao dịch"
        rvDailyList.adapter = DailyAdapter(daily)
    }

    inner class DailyAdapter(private val list: List<Transaction>) : RecyclerView.Adapter<DailyAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val v = LayoutInflater.from(parent.context).inflate(R.layout.item_stats_entry, parent, false)
            return ViewHolder(v)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val t = list[position]
            holder.tvTitle?.text = if (t.description.isNullOrEmpty()) t.category else t.description
            val amt = AppUtils.parseAmount(t.amount)
            holder.tvAmount?.text = "${if (t.isExpense) "-" else "+"}${AppUtils.formatCurrency(amt)}"
            holder.tvAmount?.setTextColor(ContextCompat.getColor(requireContext(), if (t.isExpense) R.color.expense_red else R.color.income_green))
            holder.tvDate?.text = t.date
        }
        override fun getItemCount() = list.size
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvTitle = v.findViewById<TextView>(R.id.tvTitle)
            val tvAmount = v.findViewById<TextView>(R.id.tvAmount)
            val tvDate = v.findViewById<TextView>(R.id.tvDateTime)
        }
    }
}
