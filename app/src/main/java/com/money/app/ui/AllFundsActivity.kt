package com.money.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.data.Fund
import com.money.app.data.Transaction
import com.money.app.util.AppUtils
import com.money.app.util.CurrencyHelper
import com.money.app.util.FirebaseSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Màn hình Tất cả các quỹ: Hiển thị danh sách đầy đủ các mục tiêu tiết kiệm và quỹ chung.
 * Cho phép người dùng theo dõi tiến độ và mời người khác tham gia vào quỹ chung qua Email.
 */
class AllFundsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_funds)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        loadFunds()
    }

    private fun loadFunds() {
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

            // Nộp tiền vào quỹ
            holder.btnDeposit.setOnClickListener {
                showAmountDialog(fund, isDeposit = true)
            }

            // Rút tiền từ quỹ
            holder.btnWithdraw.setOnClickListener {
                showAmountDialog(fund, isDeposit = false)
            }

            // Nút yêu thích (ghim quỹ)
            holder.btnPin.setImageResource(R.drawable.ic_heart)
            holder.btnPin.setColorFilter(if (fund.isPinned) ContextCompat.getColor(this@AllFundsActivity, R.color.expense_red) else ContextCompat.getColor(this@AllFundsActivity, R.color.text_hint))
            holder.btnPin.setOnClickListener {
                fund.isPinned = !fund.isPinned
                lifecycleScope.launch(Dispatchers.IO) {
                    AppDatabase.getDatabase(this@AllFundsActivity).fundDao().update(fund)
                    withContext(Dispatchers.Main) { loadFunds() }
                }
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
            etEmail.setPadding(40, 40, 40, 40)
            
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

        private fun showAmountDialog(fund: Fund, isDeposit: Boolean) {
            val layout = LinearLayout(this@AllFundsActivity)
            layout.orientation = LinearLayout.HORIZONTAL
            layout.setPadding(50, 40, 50, 10)

            val et = EditText(this@AllFundsActivity)
            et.layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            et.inputType = android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
            et.hint = "Nhập số tiền..."
            
            val tvCurrency = TextView(this@AllFundsActivity)
            val savedCurrency = CurrencyHelper.getSelectedCurrency(this@AllFundsActivity)
            var currentCurrency = if (savedCurrency == CurrencyHelper.CURRENCY_BOTH) CurrencyHelper.CURRENCY_VND else savedCurrency
            tvCurrency.text = currentCurrency
            tvCurrency.setPadding(20, 0, 20, 0)
            tvCurrency.textSize = 16f
            tvCurrency.setTextColor(ContextCompat.getColor(this@AllFundsActivity, R.color.primary_blue))
            tvCurrency.isClickable = true
            tvCurrency.setOnClickListener {
                currentCurrency = if (currentCurrency == CurrencyHelper.CURRENCY_VND) CurrencyHelper.CURRENCY_USD else CurrencyHelper.CURRENCY_VND
                tvCurrency.text = currentCurrency
            }

            layout.addView(et)
            layout.addView(tvCurrency)
            
            AlertDialog.Builder(this@AllFundsActivity)
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

        private fun processFundTransaction(fund: Fund, amount: Double, isDeposit: Boolean) {
            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDatabase.getDatabase(this@AllFundsActivity)
                val syncManager = FirebaseSyncManager(this@AllFundsActivity)
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
                
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
                syncManager.createFund(fund)

                val trans = Transaction(
                    amount = amount,
                    category = if (isDeposit) "Góp quỹ" else "Rút tiền quỹ",
                    description = "${if (isDeposit) "Góp vào" else "Rút từ"} quỹ ${fund.name}",
                    date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    isExpense = isDeposit,
                    timestamp = System.currentTimeMillis()
                )
                db.transactionDao().insert(trans)
                syncManager.saveTransaction(trans)

                withContext(Dispatchers.Main) {
                    loadFunds()
                    Toast.makeText(this@AllFundsActivity, "Thao tác thành công", Toast.LENGTH_SHORT).show()
                }
            }
        }

        inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val tvName = v.findViewById<TextView>(R.id.tvGoalName)
            val tvProgress = v.findViewById<TextView>(R.id.tvGoalProgress)
            val tvPercent = v.findViewById<TextView>(R.id.tvGoalPercent)
            val progressBar = v.findViewById<ProgressBar>(R.id.pbGoal)
            val ivIcon = v.findViewById<ImageView>(R.id.ivGoalIcon)
            val btnShare = v.findViewById<ImageButton>(R.id.btnShareFund)
            val btnDeposit = v.findViewById<Button>(R.id.btnDeposit)
            val btnWithdraw = v.findViewById<Button>(R.id.btnWithdraw)
            val btnPin = v.findViewById<ImageButton>(R.id.btnDeleteFund)
        }
    }
}
