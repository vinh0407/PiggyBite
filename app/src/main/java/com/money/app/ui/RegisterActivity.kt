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

/**
 * Màn hình Đăng ký: Cho phép người dùng mới tạo tài khoản.
 * Các tính năng đặc biệt:
 * - Đăng ký bằng Email hoặc Số điện thoại.
 * - Cho phép thiết lập "Số dư ban đầu" ngay khi khởi tạo tài khoản.
 * - Tự động tạo một giao dịch thu nhập đầu tiên nếu người dùng nhập số dư ban đầu.
 * - Lưu thông tin profile lên Firebase Realtime Database.
 */
class RegisterActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    private var isPhoneMode = false // Trạng thái chọn đăng ký bằng SĐT hay Email

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        // Ánh xạ các View
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

        // Chuyển đổi giao diện giữa Email và Số điện thoại
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
                // Xử lý đăng ký bằng Số điện thoại (sử dụng shadow email)
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
                // Xử lý đăng ký bằng Email
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

    /**
     * Tạo tài khoản trên Firebase Authentication
     */
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

    /**
     * Lưu thông tin cá nhân và hoàn tất đăng ký:
     * 1. Lưu Profile lên Firebase Database.
     * 2. Kiểm tra nếu có lời mời tham gia quỹ chung đang chờ (pending invitations).
     * 3. Tạo bản ghi giao dịch số dư đầu kỳ (nếu có).
     */
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
            
            // Tự động kiểm tra và chấp nhận các quỹ đã được mời trước khi đăng ký
            val userEmail = user.email
            if (!userEmail.isNullOrEmpty()) {
                syncManager.checkPendingInvitations(userEmail, userId)
            }

            // Ghi nhận số dư ban đầu vào ví chính
            if (balance > 0) {
                val initialTrans = Transaction(
                    amount = balance,
                    category = "Số dư ban đầu",
                    description = "Tài sản khi khởi tạo tài khoản",
                    date = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    isExpense = false, // Đây là một khoản thu nhập khởi tạo
                    timestamp = System.currentTimeMillis()
                )
                AppDatabase.getDatabase(this).transactionDao().insert(initialTrans)
                syncManager.saveTransaction(initialTrans)
            }

            Toast.makeText(this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show()
            finish() // Quay về màn hình Login để người dùng đăng nhập lại (hoặc vào thẳng)
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi lưu dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}
