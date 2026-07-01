package com.money.app.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.data.Fund
import com.money.app.data.Transaction
import com.money.app.util.FirebaseSyncManager
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Màn hình Thêm Quỹ (Add Fund): Cho phép người dùng tạo mục tiêu tiết kiệm hoặc quỹ chung mới.
 * Khi tạo quỹ với một số tiền ban đầu, ứng dụng sẽ tự động tạo một giao dịch chi tiêu trong ví chính.
 */
class AddFundActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_fund)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        val etName = findViewById<EditText>(R.id.etFundName)
        val etTarget = findViewById<EditText>(R.id.etTargetAmount)
        val etCurrent = findViewById<EditText>(R.id.etCurrentAmount)
        val btnSave = findViewById<Button>(R.id.btnSaveFund)

        btnSave.setOnClickListener {
            val name = etName.text.toString()
            val target = etTarget.text.toString().toDoubleOrNull() ?: 0.0
            val current = etCurrent.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên quỹ", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(this@AddFundActivity)
                val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                
                // Khởi tạo đối tượng Quỹ mới
                val fund = Fund(
                    name = name,
                    targetAmount = target,
                    currentAmount = current,
                    icon = "🏦", // Icon mặc định
                    createdDate = System.currentTimeMillis(),
                    endDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000), // Mặc định thời hạn 30 ngày
                    ownerId = currentUserId,
                    members = listOf(currentUserId), // Người tạo là thành viên đầu tiên
                    memberContributions = if (current > 0) mapOf(currentUserId to current) else mapOf()
                )
                
                // Đồng bộ lên Firebase Realtime Database
                val syncManager = FirebaseSyncManager(this@AddFundActivity)
                syncManager.createFund(fund)
                
                // Lưu vào database Room cục bộ
                db.fundDao().insert(fund)

                // Nếu có số tiền ban đầu, trừ số tiền đó khỏi ví chính bằng một giao dịch
                if (current > 0) {
                    val trans = Transaction(
                        amount = current,
                        category = "Góp quỹ",
                        description = "Góp vốn ban đầu cho quỹ $name",
                        date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                        isExpense = true, // Góp vào quỹ được coi là chi tiêu từ ví chính
                        timestamp = System.currentTimeMillis()
                    )
                    db.transactionDao().insert(trans)
                    syncManager.saveTransaction(trans)
                }

                Toast.makeText(this@AddFundActivity, "Đã tạo quỹ thành công", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}
