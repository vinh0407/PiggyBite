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
import android.widget.PopupMenu
import android.widget.Toast
import com.money.app.util.FirebaseSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

/**
 * Màn hình Lịch sử Giao dịch: Hiển thị toàn bộ các khoản thu/chi đã thực hiện.
 * Cho phép người dùng:
 * - Xem danh sách chi tiết tất cả giao dịch theo thứ tự thời gian mới nhất.
 * - Xem ảnh hóa đơn đính kèm.
 * - Nhấn giữ để Xóa một giao dịch khỏi hệ thống.
 */
class AllTransactionsActivity : AppCompatActivity() {
    private lateinit var rv: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_transactions)

        findViewById<View>(R.id.btnBack).setOnClickListener { finish() }

        rv = findViewById<RecyclerView>(R.id.rvAllTransactions)
        rv.layoutManager = LinearLayoutManager(this)

        // Tải toàn bộ giao dịch từ Room Database
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
            // Ưu tiên hiển thị mô tả, nếu không có thì hiện tên hạng mục
            holder.tvTitle.text = if (t.description.isNotEmpty()) t.description else t.category
            
            val amountVal = t.amount
            holder.tvAmount.text = "${if (t.isExpense) "-" else "+"}${AppUtils.formatCurrency(amountVal, this@AllTransactionsActivity)}"
            holder.tvAmount.setTextColor(ContextCompat.getColor(this@AllTransactionsActivity, if (t.isExpense) R.color.expense_red else R.color.income_green))
            
            // Định dạng ngày giờ đầy đủ
            val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            holder.tvDateTime.text = sdf.format(Date(t.timestamp))
            holder.tvDateTime.visibility = View.VISIBLE

            // Hiển thị thumbnail ảnh hóa đơn nếu có
            if (t.imagePath != null) {
                holder.cvPhoto.visibility = View.VISIBLE
                val file = java.io.File(t.imagePath)
                if (file.exists()) {
                    holder.ivPhoto.setImageURI(android.net.Uri.fromFile(file))
                }
            } else {
                holder.cvPhoto.visibility = View.GONE
            }

            // Sự kiện nhấn giữ để hiện menu xóa
            holder.itemView.setOnLongClickListener {
                showPopupMenu(it, t)
                true
            }
        }

        /**
         * Hiển thị Menu tùy chọn khi nhấn giữ một dòng giao dịch
         */
        private fun showPopupMenu(view: View, transaction: Transaction) {
            val popup = PopupMenu(this@AllTransactionsActivity, view)
            popup.menu.add("Xóa giao dịch")
            
            popup.setOnMenuItemClickListener { item ->
                if (item.title == "Xóa giao dịch") {
                    deleteTransaction(transaction)
                }
                true
            }
            popup.show()
        }

        /**
         * Xử lý xóa giao dịch khỏi database cục bộ
         */
        private fun deleteTransaction(transaction: Transaction) {
            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(this@AllTransactionsActivity)
                withContext(Dispatchers.IO) {
                    db.transactionDao().delete(transaction)
                }
                Toast.makeText(this@AllTransactionsActivity, "Đã xóa giao dịch", Toast.LENGTH_SHORT).show()
                // Làm mới lại màn hình sau khi xóa
                recreate()
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
