package com.money.app.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.data.Transaction
import com.money.app.util.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    private lateinit var tvDateRange: TextView
    private lateinit var rvStatsList: RecyclerView
    private lateinit var rgPeriodTabs: RadioGroup
    private lateinit var rgModeToggle: RadioGroup
    private lateinit var rbModeExpense: RadioButton
    private lateinit var rbModeIncome: RadioButton

    private lateinit var tvTotalExpVal: TextView
    private lateinit var tvTotalIncVal: TextView
    private lateinit var tvBalanceVal: TextView

    private var currentCalendar = Calendar.getInstance()
    private var selectedTabId = R.id.rbWeek
    private var isExpenseMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistics)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        tvDateRange = findViewById(R.id.tvDateRange)
        rvStatsList = findViewById(R.id.rvStatsList)
        rgPeriodTabs = findViewById(R.id.rgPeriodTabs)
        rgModeToggle = findViewById(R.id.rgModeToggle)
        rbModeExpense = findViewById(R.id.rbModeExpense)
        rbModeIncome = findViewById(R.id.rbModeIncome)

        tvTotalExpVal = findViewById<View>(R.id.cardTotalExpense).findViewById(R.id.tvStatsCardValue)
        tvTotalIncVal = findViewById<View>(R.id.cardTotalIncome).findViewById(R.id.tvStatsCardValue)
        tvBalanceVal = findViewById<View>(R.id.cardBalance).findViewById(R.id.tvStatsCardValue)

        setupSummaryCards()
        
        rvStatsList.layoutManager = LinearLayoutManager(this)

        rgPeriodTabs.setOnCheckedChangeListener { group, checkedId ->
            selectedTabId = checkedId
            
            // Tab switch animation logic (Pill background)
            for (i in 0 until group.childCount) {
                val rb = group.getChildAt(i) as RadioButton
                if (rb.id == checkedId) {
                    rb.setBackgroundResource(R.drawable.bg_pill_white)
                    rb.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
                } else {
                    rb.setBackgroundResource(0)
                    rb.setTextColor(android.graphics.Color.WHITE)
                }
            }
            loadData()
        }

        rgModeToggle.setOnCheckedChangeListener { _, checkedId ->
            isExpenseMode = (checkedId == R.id.rbModeExpense)
            updateModeToggleUI()
            loadData()
        }

        findViewById<ImageView>(R.id.btnPrev).setOnClickListener { navigateDate(-1) }
        findViewById<ImageView>(R.id.btnNext).setOnClickListener { navigateDate(1) }

        loadData()
    }

    private fun setupSummaryCards() {
        val cardExp = findViewById<View>(R.id.cardTotalExpense)
        cardExp.findViewById<TextView>(R.id.tvStatsCardLabel).text = "Tổng chi/Total Spend"
        cardExp.findViewById<ImageView>(R.id.ivStatsCardIcon).apply {
            setImageResource(R.drawable.ic_arrow_down)
            rotation = -45f
            setColorFilter(ContextCompat.getColor(this@StatisticsActivity, R.color.expense_red))
        }

        val cardInc = findViewById<View>(R.id.cardTotalIncome)
        cardInc.findViewById<TextView>(R.id.tvStatsCardLabel).text = "Tổng thu/Total Income"
        cardInc.findViewById<ImageView>(R.id.ivStatsCardIcon).apply {
            setImageResource(R.drawable.ic_arrow_down)
            rotation = 135f
            setColorFilter(ContextCompat.getColor(this@StatisticsActivity, R.color.income_green))
        }
        cardInc.findViewById<TextView>(R.id.tvStatsCardValue).setTextColor(ContextCompat.getColor(this, R.color.income_green))

        val cardBal = findViewById<View>(R.id.cardBalance)
        cardBal.findViewById<TextView>(R.id.tvStatsCardLabel).text = "Còn lại/Balance"
        cardBal.findViewById<ImageView>(R.id.ivStatsCardIcon).apply {
            setImageResource(R.drawable.ic_wallet)
            setColorFilter(ContextCompat.getColor(this@StatisticsActivity, R.color.primary_blue))
        }
        cardBal.findViewById<TextView>(R.id.tvStatsCardValue).setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
    }

    private fun updateModeToggleUI() {
        if (isExpenseMode) {
            rbModeExpense.setBackgroundResource(R.drawable.bg_pill_white)
            rbModeExpense.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
            rbModeExpense.setTypeface(null, android.graphics.Typeface.BOLD)
            
            rbModeIncome.setBackgroundResource(0)
            rbModeIncome.setTextColor(ContextCompat.getColor(this, R.color.text_hint))
            rbModeIncome.setTypeface(null, android.graphics.Typeface.NORMAL)
        } else {
            rbModeIncome.setBackgroundResource(R.drawable.bg_pill_white)
            rbModeIncome.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
            rbModeIncome.setTypeface(null, android.graphics.Typeface.BOLD)
            
            rbModeExpense.setBackgroundResource(0)
            rbModeExpense.setTextColor(ContextCompat.getColor(this, R.color.text_hint))
            rbModeExpense.setTypeface(null, android.graphics.Typeface.NORMAL)
        }
    }

    private fun navigateDate(direction: Int) {
        when (selectedTabId) {
            R.id.rbWeek -> currentCalendar.add(Calendar.WEEK_OF_YEAR, direction)
            R.id.rbMonth -> currentCalendar.add(Calendar.MONTH, direction)
            R.id.rbYear -> currentCalendar.add(Calendar.YEAR, direction)
        }
        loadData()
    }

    private fun loadData() {
        val (startTime, endTime) = getTimeRange()
        updateDateRangeText(startTime, endTime)

        lifecycleScope.launch {
            val transactions = withContext(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(this@StatisticsActivity)
                db.transactionDao().getTransactionsInTimeRange(startTime, endTime)
            }
            
            var totalExp = 0.0
            var totalInc = 0.0
            transactions.forEach {
                val amt = AppUtils.parseAmount(it.amount)
                if (it.isExpense) totalExp += amt else totalInc += amt
            }

            tvTotalExpVal.text = "-${AppUtils.formatCurrency(totalExp)}"
            tvTotalIncVal.text = "+${AppUtils.formatCurrency(totalInc)}"
            tvBalanceVal.text = AppUtils.formatCurrency(totalInc - totalExp)

            val filteredList = transactions.filter { it.isExpense == isExpenseMode }
                .sortedByDescending { it.timestamp }
            
            // Logic for different tabs
            when (selectedTabId) {
                R.id.rbMonth -> {
                    val weeklyData = withContext(Dispatchers.Default) {
                        aggregateByWeek(filteredList, startTime, endTime)
                    }
                    rvStatsList.adapter = AggregateAdapter(weeklyData, "Tuần") { }
                }
                R.id.rbYear -> {
                    val monthlyData = withContext(Dispatchers.Default) {
                        aggregateByMonth(filteredList, startTime, endTime)
                    }
                    rvStatsList.adapter = AggregateAdapter(monthlyData, "Tháng") { }
                }
                else -> {
                    rvStatsList.adapter = StatsListAdapter(filteredList)
                }
            }
        }
    }

    private fun aggregateByWeek(transactions: List<Transaction>, start: Long, end: Long): List<AggregateItem> {
        val map = mutableMapOf<Int, Double>()
        val cal = Calendar.getInstance()
        transactions.forEach {
            cal.timeInMillis = it.timestamp
            val week = cal.get(Calendar.WEEK_OF_MONTH)
            map[week] = (map[week] ?: 0.0) + AppUtils.parseAmount(it.amount)
        }
        val result = mutableListOf<AggregateItem>()
        for (w in 1..5) {
            val amt = map[w] ?: 0.0
            if (amt > 0) {
                result.add(AggregateItem("Tuần $w", amt, start))
            }
        }
        return result.sortedByDescending { it.label }
    }

    private fun aggregateByMonth(transactions: List<Transaction>, start: Long, end: Long): List<AggregateItem> {
        val map = mutableMapOf<Int, Double>()
        val cal = Calendar.getInstance()
        transactions.forEach {
            cal.timeInMillis = it.timestamp
            val month = cal.get(Calendar.MONTH)
            map[month] = (map[month] ?: 0.0) + AppUtils.parseAmount(it.amount)
        }
        val result = mutableListOf<AggregateItem>()
        val monthNames = listOf("Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12")
        for (m in 0..11) {
            val amt = map[m] ?: 0.0
            if (amt > 0) {
                val monthCal = Calendar.getInstance().apply {
                    timeInMillis = start
                    set(Calendar.MONTH, m)
                }
                result.add(AggregateItem(monthNames[m], amt, monthCal.timeInMillis))
            }
        }
        return result.sortedByDescending { it.timestamp }
    }

    private fun showFullImage(path: String) {
        val dialog = android.app.Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        val iv = ImageView(this)
        iv.setImageURI(android.net.Uri.fromFile(java.io.File(path)))
        iv.scaleType = ImageView.ScaleType.FIT_CENTER
        dialog.setContentView(iv)
        iv.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    private fun getTimeRange(): Pair<Long, Long> {
        val start = currentCalendar.clone() as Calendar
        val end = currentCalendar.clone() as Calendar
        
        when (selectedTabId) {
            R.id.rbWeek -> {
                start.set(Calendar.DAY_OF_WEEK, start.firstDayOfWeek)
                start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0)
                end.timeInMillis = start.timeInMillis
                end.add(Calendar.DAY_OF_YEAR, 6)
                end.set(Calendar.HOUR_OF_DAY, 23); end.set(Calendar.MINUTE, 59); end.set(Calendar.SECOND, 59)
            }
            R.id.rbMonth -> {
                start.set(Calendar.DAY_OF_MONTH, 1)
                start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0)
                end.timeInMillis = start.timeInMillis
                end.add(Calendar.MONTH, 1); end.add(Calendar.SECOND, -1)
            }
            R.id.rbYear -> {
                start.set(Calendar.DAY_OF_YEAR, 1)
                start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0)
                end.timeInMillis = start.timeInMillis
                end.add(Calendar.YEAR, 1); end.add(Calendar.SECOND, -1)
            }
            R.id.rbAll -> {
                start.timeInMillis = 0
                end.timeInMillis = Long.MAX_VALUE
            }
        }
        return Pair(start.timeInMillis, end.timeInMillis)
    }

    private fun updateDateRangeText(start: Long, end: Long) {
        if (selectedTabId == R.id.rbAll) {
            tvDateRange.text = "Tất cả thời gian/All time"
            return
        }
        val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        val sdfFull = SimpleDateFormat("dd 'thg' M, yyyy", Locale("vi", "VN"))
        
        if (selectedTabId == R.id.rbWeek) {
            tvDateRange.text = "${sdf.format(Date(start))} - ${sdfFull.format(Date(end))}"
        } else if (selectedTabId == R.id.rbMonth) {
            val monthSdf = SimpleDateFormat("MMMM yyyy", Locale("vi", "VN"))
            tvDateRange.text = monthSdf.format(Date(start))
        } else {
            val yearSdf = SimpleDateFormat("yyyy", Locale.getDefault())
            tvDateRange.text = yearSdf.format(Date(start))
        }
    }

    data class AggregateItem(val label: String, val amount: Double, val timestamp: Long)

    inner class AggregateAdapter(private val items: List<AggregateItem>, private val typeLabel: String, val onClick: (Long) -> Unit) : RecyclerView.Adapter<AggregateAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stats_entry, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvTitle.text = item.label
            val symbol = "đ"
            holder.tvAmount.text = "${if (isExpenseMode) "-" else "+"}${AppUtils.formatCurrency(item.amount)}"
            holder.tvAmount.setTextColor(ContextCompat.getColor(this@StatisticsActivity, if (isExpenseMode) R.color.expense_red else R.color.income_green))
            holder.tvDateTime.text = "Tổng $typeLabel"
            holder.itemView.setOnClickListener { onClick(item.timestamp) }
        }
        override fun getItemCount(): Int = items.size
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvTitle = v.findViewById<TextView>(R.id.tvTitle)
            val tvAmount = v.findViewById<TextView>(R.id.tvAmount)
            val tvDateTime = v.findViewById<TextView>(R.id.tvDateTime)
        }
    }

    inner class StatsListAdapter(private val list: List<Transaction>) : RecyclerView.Adapter<StatsListAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stats_entry, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val t = list[position]
            holder.tvTitle.text = if (t.description.isNotEmpty()) t.description else t.category
            
            val amountVal = AppUtils.parseAmount(t.amount)
            holder.tvAmount.text = "${if (t.isExpense) "-" else "+"}${AppUtils.formatCurrency(amountVal)}"
            holder.tvAmount.setTextColor(ContextCompat.getColor(this@StatisticsActivity, if (t.isExpense) R.color.expense_red else R.color.income_green))
            
            val timeSdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            holder.tvDateTime.text = timeSdf.format(Date(t.timestamp))

            if (t.imagePath != null) {
                val file = java.io.File(t.imagePath)
                if (file.exists()) {
                    holder.cvPhoto.visibility = View.VISIBLE
                    holder.ivPhoto.setImageURI(android.net.Uri.fromFile(file))
                    holder.ivPhoto.setOnClickListener { showFullImage(t.imagePath) }
                } else {
                    holder.cvPhoto.visibility = View.GONE
                }
            } else {
                holder.cvPhoto.visibility = View.GONE
            }
        }
        override fun getItemCount(): Int = list.size
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvTitle = v.findViewById<TextView>(R.id.tvTitle)
            val tvAmount = v.findViewById<TextView>(R.id.tvAmount)
            val tvDateTime = v.findViewById<TextView>(R.id.tvDateTime)
            val cvPhoto = v.findViewById<View>(R.id.cvPhoto)
            val ivPhoto = v.findViewById<ImageView>(R.id.ivPhoto)
        }
    }
}