package com.money.app.ui

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.data.Transaction
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener {
            finish()
        }

        btnRegister.setOnClickListener {
            val name = findViewById<EditText>(R.id.etName).text.toString()
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            val initialBalance = findViewById<EditText>(R.id.etInitialBalance).text.toString().toDoubleOrNull() ?: 0.0

            if (email.isEmpty() || password.isEmpty() || name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString(email, password)
                putString("user_name", name)
                putString("user_email", email)
                apply()
            }

            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(this@RegisterActivity)
                // Save initial balance as an Income transaction
                if (initialBalance > 0) {
                    val initialTrans = Transaction(
                        amount = initialBalance.toLong().toString(),
                        category = "Số dư ban đầu",
                        description = "Tài sản khi khởi tạo tài khoản",
                        date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                        isExpense = false,
                        timestamp = System.currentTimeMillis()
                    )
                    db.transactionDao().insert(initialTrans)
                }
                
                Toast.makeText(this@RegisterActivity, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}