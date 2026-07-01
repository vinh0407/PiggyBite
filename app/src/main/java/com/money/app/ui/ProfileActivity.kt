package com.money.app.ui

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.auth.EmailAuthProvider
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.util.AppUtils
import com.money.app.util.FirebaseSyncManager
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Màn hình Thông tin cá nhân (Profile): Quản lý dữ liệu tài khoản người dùng.
 * Các tính năng chính:
 * - Hiển thị và cập nhật Tên, Email, Số điện thoại.
 * - Hiển thị tổng số dư hiện tại của ví.
 * - Đổi mật khẩu tài khoản (Yêu cầu xác thực lại bằng mật khẩu cũ).
 * - Tự động đồng bộ các thay đổi lên Firebase Realtime Database.
 */
class ProfileActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference
    
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var tvBalance: TextView
    private lateinit var btnUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        tvBalance = findViewById(R.id.tvBalance)
        btnUpdate = findViewById(R.id.btnUpdateProfile)

        // Nút mở hộp thoại đổi mật khẩu
        findViewById<TextView>(R.id.tvChangePassword).setOnClickListener {
            showChangePasswordDialog()
        }

        loadUserData() // Tải dữ liệu từ Firebase và Database cục bộ

        btnUpdate.setOnClickListener {
            updateProfile() // Cập nhật thông tin
        }
    }

    /**
     * Tải thông tin người dùng:
     * 1. Lấy Tên/Email/SĐT từ Firebase Realtime Database.
     * 2. Tính toán tổng số dư từ lịch sử giao dịch trong Database Room cục bộ.
     */
    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return
        
        lifecycleScope.launch {
            try {
                // Tải Profile từ Firebase RTDB
                val snapshot = db.child("users").child(userId).child("profile").get().await()
                
                val name = snapshot.child("name").value as? String ?: ""
                val email = snapshot.child("email").value as? String ?: auth.currentUser?.email ?: ""
                val phone = snapshot.child("phone").value as? String ?: ""
                
                etName.setText(name)
                etEmail.setText(email)
                etPhone.setText(phone)

                // Tính toán số dư dựa trên danh sách giao dịch hiện có
                val localDb = AppDatabase.getDatabase(this@ProfileActivity)
                val transactions = withContext(Dispatchers.IO) {
                    localDb.transactionDao().getAllTransactions()
                }
                
                var total = 0.0
                transactions.forEach {
                    val amt = it.amount
                    if (it.isExpense) total -= amt else total += amt
                }
                
                tvBalance.text = AppUtils.formatCurrency(total, this@ProfileActivity)

            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Lỗi khi tải dữ liệu người dùng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Cập nhật thông tin Profile:
     * - Cập nhật email trong hệ thống xác thực Firebase (Authentication).
     * - Cập nhật các trường thông tin trong Realtime Database.
     * - Cập nhật lại Tên trong SharedPreferences để hiển thị ở Trang chủ.
     */
    private fun updateProfile() {
        val userId = auth.currentUser?.uid ?: return
        val newName = etName.text.toString().trim()
        val newEmail = etEmail.text.toString().trim()
        val newPhone = etPhone.text.toString().trim()

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và email", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val user = auth.currentUser!!
                
                // 1. Cập nhật Email trong Firebase Auth (nếu có thay đổi)
                if (newEmail != user.email) {
                    user.updateEmail(newEmail).await()
                }

                // 2. Cập nhật dữ liệu trong RTDB
                val updates = hashMapOf<String, Any>(
                    "name" to newName,
                    "email" to newEmail,
                    "phone" to newPhone
                )
                db.child("users").child(userId).child("profile").updateChildren(updates).await()
                
                // Kiểm tra lại lời mời quỹ nếu email thay đổi
                FirebaseSyncManager(this@ProfileActivity).checkPendingInvitations(newEmail, userId)

                Toast.makeText(this@ProfileActivity, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                
                // Cập nhật bộ nhớ đệm cục bộ
                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                prefs.edit().putString("user_name", newName).apply()
                
            } catch (e: Exception) {
                val msg = if (e.message?.contains("recent login") == true) {
                    "Vui lòng đăng nhập lại để thay đổi email."
                } else {
                    "Lỗi: ${e.message}"
                }
                Toast.makeText(this@ProfileActivity, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

    /**
     * Hiển thị hộp thoại thay đổi mật khẩu
     */
    private fun showChangePasswordDialog() {
        val view = LayoutInflater.from(this).inflate(R.layout.dialog_change_password, null)
        val etOld = view.findViewById<EditText>(R.id.etOldPassword)
        val etNew = view.findViewById<EditText>(R.id.etNewPassword)
        val etConfirm = view.findViewById<EditText>(R.id.etConfirmPassword)

        AlertDialog.Builder(this)
            .setTitle("Đổi mật khẩu")
            .setView(view)
            .setPositiveButton("Thay đổi") { dialog, _ ->
                val oldPass = etOld.text.toString()
                val newPass = etNew.text.toString()
                val confirmPass = etConfirm.text.toString()

                if (newPass != confirmPass) {
                    Toast.makeText(this, "Mật khẩu mới không khớp", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }
                if (newPass.length < 6) {
                    Toast.makeText(this, "Mật khẩu phải từ 6 ký tự", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                performPasswordChange(oldPass, newPass)
                dialog.dismiss()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    /**
     * Quy trình đổi mật khẩu an toàn: 
     * Xác thực lại mật khẩu cũ -> Cập nhật mật khẩu mới.
     */
    private fun performPasswordChange(oldPass: String, newPass: String) {
        val user = auth.currentUser ?: return
        val email = user.email ?: return
        
        lifecycleScope.launch {
            try {
                // Xác thực lại (Re-authenticate)
                val credential = EmailAuthProvider.getCredential(email, oldPass)
                user.reauthenticate(credential).await()
                
                // Đổi mật khẩu
                user.updatePassword(newPass).await()
                Toast.makeText(this@ProfileActivity, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Mật khẩu cũ không chính xác", Toast.LENGTH_LONG).show()
            }
        }
    }
}
