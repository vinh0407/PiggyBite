package com.money.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.data.Transaction
import com.money.app.util.AppUtils
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class AllTransactionsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_transactions)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvAllTransactions)
        rv.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@AllTransactionsActivity)
            val list = db.transactionDao().getAllTransactions().sortedByDescending { it.timestamp }
            rv.adapter = TransactionsAdapter(list)
        }
    }

    inner class TransactionsAdapter(private val list: List<Transaction>) : RecyclerView.Adapter<TransactionsAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stats_entry, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val t = list[position]
            holder.tvTitle.text = if (t.description.isNotEmpty()) t.description else t.category
            
            val amountVal = AppUtils.parseAmount(t.amount)
            holder.tvAmount.text = "${if (t.isExpense) "-" else "+"}${AppUtils.formatCurrency(amountVal)}"
            holder.tvAmount.setTextColor(ContextCompat.getColor(this@AllTransactionsActivity, if (t.isExpense) R.color.expense_red else R.color.income_green))
            
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            holder.tvDateTime.text = sdf.format(Date(t.timestamp))
            holder.tvDateTime.visibility = View.VISIBLE

            if (t.imagePath != null) {
                holder.cvPhoto.visibility = View.VISIBLE
                holder.ivPhoto.setImageURI(android.net.Uri.fromFile(java.io.File(t.imagePath)))
            } else {
                holder.cvPhoto.visibility = View.GONE
            }
        }
        override fun getItemCount() = list.size
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvTitle = v.findViewById<TextView>(R.id.tvTitle)
            val tvAmount = v.findViewById<TextView>(R.id.tvAmount)
            val tvDateTime = v.findViewById<TextView>(R.id.tvDateTime)
            val cvPhoto = v.findViewById<View>(R.id.cvPhoto)
            val ivPhoto = v.findViewById<ImageView>(R.id.ivPhoto)
        }
    }
}