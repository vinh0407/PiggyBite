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
                
                val fund = Fund(
                    name = name,
                    targetAmount = target,
                    currentAmount = current,
                    icon = "🏦",
                    createdDate = System.currentTimeMillis(),
                    endDate = System.currentTimeMillis() + (30L * 24 * 60 * 60 * 1000),
                    ownerId = currentUserId,
                    members = listOf(currentUserId),
                    memberContributions = if (current > 0) mapOf(currentUserId to current) else mapOf()
                )
                
                val syncManager = FirebaseSyncManager(this@AddFundActivity)
                syncManager.createFund(fund)
                
                db.fundDao().insert(fund)

                // Deduct initial amount from main wallet
                if (current > 0) {
                    val trans = Transaction(
                        amount = current,
                        category = "Góp quỹ",
                        description = "Góp vốn ban đầu cho quỹ $name",
                        date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                        isExpense = true,
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