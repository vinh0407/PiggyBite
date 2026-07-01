package com.money.app.ui

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
import com.money.app.databinding.ActivityStatisticsBinding
import com.money.app.util.AppUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private var currentCalendar = Calendar.getInstance()
    private var selectedTabId = R.id.rbWeek
    private var isExpenseMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener { finish() }

        setupSummaryCards()
        
        binding.rvStatsList.layoutManager = LinearLayoutManager(this)

        binding.rgPeriodTabs.setOnCheckedChangeListener { group, checkedId ->
            selectedTabId = checkedId
            
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

        binding.rgModeToggle.setOnCheckedChangeListener { _, checkedId ->
            isExpenseMode = (checkedId == R.id.rbModeExpense)
            updateModeToggleUI()
            loadData()
        }

        binding.btnPrev.setOnClickListener { navigateDate(-1) }
        binding.btnNext.setOnClickListener { navigateDate(1) }

        loadData()
    }

    private fun setupSummaryCards() {
        val expenseCard = binding.cardTotalExpense.root
        expenseCard.findViewById<TextView>(R.id.tvStatsCardLabel).text = "Tổng chi/Total Spend"
        expenseCard.findViewById<ImageView>(R.id.ivStatsCardIcon).apply {
            setImageResource(R.drawable.ic_arrow_down)
            rotation = -45f
            setColorFilter(ContextCompat.getColor(this@StatisticsActivity, R.color.expense_red))
        }

        val incomeCard = binding.cardTotalIncome.root
        incomeCard.findViewById<TextView>(R.id.tvStatsCardLabel).text = "Tổng thu/Total Income"
        incomeCard.findViewById<ImageView>(R.id.ivStatsCardIcon).apply {
            setImageResource(R.drawable.ic_arrow_down)
            rotation = 135f
            setColorFilter(ContextCompat.getColor(this@StatisticsActivity, R.color.income_green))
        }
        incomeCard.findViewById<TextView>(R.id.tvStatsCardValue).setTextColor(ContextCompat.getColor(this, R.color.income_green))

        val balanceCard = binding.cardBalance.root
        balanceCard.findViewById<TextView>(R.id.tvStatsCardLabel).text = "Còn lại/Balance"
        balanceCard.findViewById<ImageView>(R.id.ivStatsCardIcon).apply {
            setImageResource(R.drawable.ic_wallet)
            setColorFilter(ContextCompat.getColor(this@StatisticsActivity, R.color.primary_blue))
        }
        balanceCard.findViewById<TextView>(R.id.tvStatsCardValue).setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
    }

    private fun updateModeToggleUI() {
        if (isExpenseMode) {
            binding.rbModeExpense.setBackgroundResource(R.drawable.bg_pill_white)
            binding.rbModeExpense.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
            binding.rbModeExpense.setTypeface(null, android.graphics.Typeface.BOLD)
            
            binding.rbModeIncome.setBackgroundResource(0)
            binding.rbModeIncome.setTextColor(ContextCompat.getColor(this, R.color.text_hint))
            binding.rbModeIncome.setTypeface(null, android.graphics.Typeface.NORMAL)
        } else {
            binding.rbModeIncome.setBackgroundResource(R.drawable.bg_pill_white)
            binding.rbModeIncome.setTextColor(ContextCompat.getColor(this, R.color.primary_blue))
            binding.rbModeIncome.setTypeface(null, android.graphics.Typeface.BOLD)
            
            binding.rbModeExpense.setBackgroundResource(0)
            binding.rbModeExpense.setTextColor(ContextCompat.getColor(this, R.color.text_hint))
            binding.rbModeExpense.setTypeface(null, android.graphics.Typeface.NORMAL)
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
                val amt = it.amount
                if (it.isExpense) totalExp += amt else totalInc += amt
            }

            binding.cardTotalExpense.root.findViewById<TextView>(R.id.tvStatsCardValue).text = "-${AppUtils.formatCurrency(totalExp, this@StatisticsActivity)}"
            binding.cardTotalIncome.root.findViewById<TextView>(R.id.tvStatsCardValue).text = "+${AppUtils.formatCurrency(totalInc, this@StatisticsActivity)}"
            binding.cardBalance.root.findViewById<TextView>(R.id.tvStatsCardValue).text = AppUtils.formatCurrency(totalInc - totalExp, this@StatisticsActivity)

            val filteredList = transactions.filter { it.isExpense == isExpenseMode }
                .sortedByDescending { it.timestamp }
            
            when (selectedTabId) {
                R.id.rbMonth -> {
                    val weeklyData = withContext(Dispatchers.Default) {
                        aggregateByWeek(filteredList)
                    }
                    binding.rvStatsList.adapter = AggregateAdapter(weeklyData, "Tuần") { }
                }
                R.id.rbYear -> {
                    val monthlyData = withContext(Dispatchers.Default) {
                        aggregateByMonth(filteredList, startTime)
                    }
                    binding.rvStatsList.adapter = AggregateAdapter(monthlyData, "Tháng") { }
                }
                else -> {
                    binding.rvStatsList.adapter = StatsListAdapter(filteredList)
                }
            }
        }
    }

    private fun aggregateByWeek(transactions: List<Transaction>): List<AggregateItem> {
        val map = mutableMapOf<Int, Pair<Double, Long>>()
        val cal = Calendar.getInstance()
        transactions.forEach {
            cal.timeInMillis = it.timestamp
            val week = cal.get(Calendar.WEEK_OF_YEAR)
            val current = map[week] ?: Pair(0.0, it.timestamp)
            map[week] = Pair(current.first + it.amount, current.second)
        }
        
        return map.entries.map { entry ->
            val calTemp = Calendar.getInstance()
            calTemp.set(Calendar.WEEK_OF_YEAR, entry.key)
            val weekInMonth = calTemp.get(Calendar.WEEK_OF_MONTH)
            AggregateItem("Tuần $weekInMonth", entry.value.first, entry.value.second)
        }.sortedByDescending { it.timestamp }
    }

    private fun aggregateByMonth(transactions: List<Transaction>, start: Long): List<AggregateItem> {
        val map = mutableMapOf<Int, Double>()
        val cal = Calendar.getInstance()
        transactions.forEach {
            cal.timeInMillis = it.timestamp
            val month = cal.get(Calendar.MONTH)
            map[month] = (map[month] ?: 0.0) + it.amount
        }
        val result = mutableListOf<AggregateItem>()
        val monthNames = listOf("Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12")
        for (m in 0..11) {
            val amt = map[m] ?: 0.0
            if (amt > 0) {
                val monthCal = Calendar.getInstance()
                monthCal.timeInMillis = start
                monthCal.set(Calendar.MONTH, m)
                monthCal.set(Calendar.DAY_OF_MONTH, 1)
                result.add(AggregateItem(monthNames[m], amt, monthCal.timeInMillis))
            }
        }
        return result.sortedByDescending { it.timestamp }
    }

    private fun updateDateRangeText(start: Long, end: Long) {
        if (selectedTabId == R.id.rbAll) {
            binding.tvDateRange.text = "Tất cả thời gian/All time"
            return
        }
        val sdf = SimpleDateFormat("dd/MM", Locale.getDefault())
        val sdfFull = SimpleDateFormat("dd 'thg' M, yyyy", Locale("vi", "VN"))
        
        if (selectedTabId == R.id.rbWeek) {
            binding.tvDateRange.text = "${sdf.format(Date(start))} - ${sdfFull.format(Date(end))}"
        } else if (selectedTabId == R.id.rbMonth) {
            val monthSdf = SimpleDateFormat("MMMM yyyy", Locale("vi", "VN"))
            binding.tvDateRange.text = monthSdf.format(Date(start))
        } else {
            val yearSdf = SimpleDateFormat("yyyy", Locale.getDefault())
            binding.tvDateRange.text = yearSdf.format(Date(start))
        }
    }

    private fun getTimeRange(): Pair<Long, Long> {
        val start = currentCalendar.clone() as Calendar
        val end = currentCalendar.clone() as Calendar
        
        when (selectedTabId) {
            R.id.rbWeek -> {
                start.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
                start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0); start.set(Calendar.MILLISECOND, 0)
                end.timeInMillis = start.timeInMillis
                end.add(Calendar.DAY_OF_YEAR, 6)
                end.set(Calendar.HOUR_OF_DAY, 23); end.set(Calendar.MINUTE, 59); end.set(Calendar.SECOND, 59)
            }
            R.id.rbMonth -> {
                start.set(Calendar.DAY_OF_MONTH, 1)
                start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0); start.set(Calendar.MILLISECOND, 0)
                end.timeInMillis = start.timeInMillis
                end.add(Calendar.MONTH, 1); end.add(Calendar.MILLISECOND, -1)
            }
            R.id.rbYear -> {
                start.set(Calendar.DAY_OF_YEAR, 1)
                start.set(Calendar.HOUR_OF_DAY, 0); start.set(Calendar.MINUTE, 0); start.set(Calendar.SECOND, 0); start.set(Calendar.MILLISECOND, 0)
                end.timeInMillis = start.timeInMillis
                end.add(Calendar.YEAR, 1); end.add(Calendar.MILLISECOND, -1)
            }
            else -> {
                start.timeInMillis = 0
                end.timeInMillis = Long.MAX_VALUE
            }
        }
        return Pair(start.timeInMillis, end.timeInMillis)
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

    data class AggregateItem(val label: String, val amount: Double, val timestamp: Long)

    inner class AggregateAdapter(private val items: List<AggregateItem>, private val typeLabel: String, val onClick: (Long) -> Unit) : RecyclerView.Adapter<AggregateAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stats_entry, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvTitle.text = item.label
            holder.tvAmount.text = "${if (isExpenseMode) "-" else "+"}${AppUtils.formatCurrency(item.amount, this@StatisticsActivity)}"
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
            
            val amountVal = t.amount
            holder.tvAmount.text = "${if (t.isExpense) "-" else "+"}${AppUtils.formatCurrency(amountVal, this@StatisticsActivity)}"
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
