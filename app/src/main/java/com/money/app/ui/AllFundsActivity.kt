package com.money.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.data.Fund
import com.money.app.util.AppUtils
import kotlinx.coroutines.launch

class AllFundsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_funds)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvAllFunds)
        rv.layoutManager = LinearLayoutManager(this)

        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@AllFundsActivity)
            val funds = db.fundDao().getAllFunds()
            rv.adapter = FundsAdapter(funds)
        }
    }

    inner class FundsAdapter(private val list: List<Fund>) : RecyclerView.Adapter<FundsAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fund_premium, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val fund = list[position]
            holder.tvName.text = fund.name
            holder.tvProgress.text = "${AppUtils.formatCurrency(fund.currentAmount)} / ${AppUtils.formatCurrency(fund.targetAmount)}"
            val percent = if (fund.targetAmount > 0) (fund.currentAmount / fund.targetAmount * 100).toInt() else 0
            holder.tvPercent.text = "$percent%"
            holder.progressBar.progress = percent.coerceIn(0, 100)
            // Use generic icon for now
            holder.ivIcon.setImageResource(R.drawable.ic_piggy_bank)
        }
        override fun getItemCount() = list.size
        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvName = v.findViewById<TextView>(R.id.tvGoalName)
            val tvProgress = v.findViewById<TextView>(R.id.tvGoalProgress)
            val tvPercent = v.findViewById<TextView>(R.id.tvGoalPercent)
            val progressBar = v.findViewById<ProgressBar>(R.id.pbGoal)
            val ivIcon = v.findViewById<ImageView>(R.id.ivGoalIcon)
        }
    }
}