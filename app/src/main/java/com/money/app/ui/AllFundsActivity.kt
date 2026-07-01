package com.money.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.data.Fund
import com.money.app.util.AppUtils
import com.money.app.util.FirebaseSyncManager
import kotlinx.coroutines.launch

/**
 * Màn hình Tất cả các quỹ: Hiển thị danh sách đầy đủ các mục tiêu tiết kiệm và quỹ chung.
 * Cho phép người dùng theo dõi tiến độ và mời người khác tham gia vào quỹ chung qua Email.
 */
class AllFundsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_funds)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvAllFunds)
        rv.layoutManager = LinearLayoutManager(this)

        // Tải danh sách quỹ từ cơ sở dữ liệu Room
        lifecycleScope.launch {
            val db = AppDatabase.getDatabase(this@AllFundsActivity)
            val funds = db.fundDao().getAllFunds()
            rv.adapter = FundsAdapter(funds)
        }
    }

    /**
     * Adapter để hiển thị danh sách các thẻ Quỹ.
     */
    inner class FundsAdapter(private val list: List<Fund>) : RecyclerView.Adapter<FundsAdapter.ViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fund_premium, parent, false)
            return ViewHolder(view)
        }
        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val fund = list[position]
            holder.tvName.text = fund.name
            holder.tvProgress.text = "${AppUtils.formatCurrency(fund.currentAmount, this@AllFundsActivity)} / ${AppUtils.formatCurrency(fund.targetAmount, this@AllFundsActivity)}"
            
            // Tính toán phần trăm hoàn thành mục tiêu
            val percent = if (fund.targetAmount > 0) (fund.currentAmount / fund.targetAmount * 100).toInt() else 0
            holder.tvPercent.text = "$percent%"
            holder.progressBar.progress = percent.coerceIn(0, 100)
            holder.ivIcon.setImageResource(R.drawable.ic_piggy_bank)

            // Nút mời thành viên vào quỹ chung
            holder.btnShare.setOnClickListener {
                showShareDialog(fund)
            }
        }
        override fun getItemCount() = list.size

        /**
         * Hiển thị hộp thoại nhập Email để mời thành viên tham gia quỹ.
         * Lời mời sẽ được gửi lên Firebase và người nhận sẽ thấy quỹ này trong ứng dụng của họ.
         */
        private fun showShareDialog(fund: Fund) {
            val etEmail = EditText(this@AllFundsActivity)
            etEmail.hint = "Email thành viên gia đình"
            
            AlertDialog.Builder(this@AllFundsActivity)
                .setTitle("Chia sẻ quỹ chung")
                .setMessage("Nhập email người bạn muốn mời vào quỹ '${fund.name}':")
                .setView(etEmail)
                .setPositiveButton("Mời") { _, _ ->
                    val email = etEmail.text.toString().trim()
                    if (email.isNotEmpty()) {
                        lifecycleScope.launch {
                            val syncManager = FirebaseSyncManager(this@AllFundsActivity)
                            // Sử dụng syncId để định danh quỹ trên Firebase
                            val fid = fund.syncId
                            syncManager.shareFund(fid, email)
                            Toast.makeText(this@AllFundsActivity, "Đã gửi lời mời tới $email", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
                .setNegativeButton("Hủy", null)
                .show()
        }

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvName = v.findViewById<TextView>(R.id.tvGoalName)
            val tvProgress = v.findViewById<TextView>(R.id.tvGoalProgress)
            val tvPercent = v.findViewById<TextView>(R.id.tvGoalPercent)
            val progressBar = v.findViewById<ProgressBar>(R.id.pbGoal)
            val ivIcon = v.findViewById<ImageView>(R.id.ivGoalIcon)
            val btnShare = v.findViewById<ImageButton>(R.id.btnShareFund)
        }
    }
}
