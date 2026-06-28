package com.money.app.ui

import android.content.Context
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.data.Transaction
import com.money.app.util.FirebaseSyncManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    private var isPhoneMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val rgMethod = findViewById<RadioGroup>(R.id.rgRegisterMethod)
        val emailContainer = findViewById<View>(R.id.emailRegisterContainer)
        val phoneContainer = findViewById<View>(R.id.phoneRegisterContainer)
        
        val etName = findViewById<EditText>(R.id.etName)
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPhonePassword = findViewById<EditText>(R.id.etPhonePassword)
        val etInitialBalance = findViewById<EditText>(R.id.etInitialBalance)
        val btnRegister = findViewById<Button>(R.id.btnRegister)
        val btnBack = findViewById<ImageButton>(R.id.btnBack)

        btnBack.setOnClickListener { finish() }

        rgMethod.setOnCheckedChangeListener { _, checkedId ->
            isPhoneMode = checkedId == R.id.rbPhone
            emailContainer.visibility = if (isPhoneMode) View.GONE else View.VISIBLE
            phoneContainer.visibility = if (isPhoneMode) View.VISIBLE else View.GONE
        }

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val initialBalance = etInitialBalance.text.toString().toDoubleOrNull() ?: 0.0

            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập họ tên", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (isPhoneMode) {
                val phone = etPhone.text.toString().trim()
                val pass = etPhonePassword.text.toString().trim()
                if (phone.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                if (pass.length < 6) {
                    Toast.makeText(this, "Mật khẩu phải từ 6 ký tự", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val shadowEmail = "phone_${phone}@piggybite.com"
                registerWithEmail(name, shadowEmail, pass, initialBalance, phone)
            } else {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                registerWithEmail(name, email, password, initialBalance, "")
            }
        }
    }

    private fun registerWithEmail(name: String, email: String, pass: String, balance: Double, phone: String) {
        lifecycleScope.launch {
            try {
                val result = auth.createUserWithEmailAndPassword(email, pass).await()
                saveProfileAndFinish(result.user, name, phone, balance)
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Lỗi đăng ký: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun saveProfileAndFinish(user: FirebaseUser?, name: String, phone: String, balance: Double) {
        val userId = user?.uid ?: return
        try {
            val profile = hashMapOf(
                "name" to name,
                "email" to (user.email ?: ""),
                "phone" to phone,
                "createdAt" to System.currentTimeMillis()
            )
            db.child("users").child(userId).child("profile").setValue(profile).await()

            val syncManager = FirebaseSyncManager(this)
            
            val userEmail = user.email
            if (!userEmail.isNullOrEmpty()) {
                syncManager.checkPendingInvitations(userEmail, userId)
            }

            if (balance > 0) {
                val initialTrans = Transaction(
                    amount = balance,
                    category = "Số dư ban đầu",
                    description = "Tài sản khi khởi tạo tài khoản",
                    date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    isExpense = false,
                    timestamp = System.currentTimeMillis()
                )
                AppDatabase.getDatabase(this).transactionDao().insert(initialTrans)
                syncManager.saveTransaction(initialTrans)
            }

            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi lưu dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}