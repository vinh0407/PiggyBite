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
import com.money.app.R
import com.money.app.data.AppDatabase
import com.money.app.util.AppUtils
import com.money.app.util.FirebaseSyncManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ProfileActivity : AppCompatActivity() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference
    
    private lateinit var etName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPhone: EditText
    private lateinit var etPassword: EditText
    private lateinit var tvBalance: TextView
    private lateinit var btnUpdate: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        etName = findViewById(R.id.etName)
        etEmail = findViewById(R.id.etEmail)
        etPhone = findViewById(R.id.etPhone)
        etPassword = findViewById(R.id.etPassword)
        tvBalance = findViewById(R.id.tvBalance)
        btnUpdate = findViewById(R.id.btnUpdateProfile)

        loadUserData()

        btnUpdate.setOnClickListener {
            updateProfile()
        }
    }

    private fun loadUserData() {
        val userId = auth.currentUser?.uid ?: return
        
        lifecycleScope.launch {
            try {
                // Load profile from RTDB
                val snapshot = db.child("users").child(userId).child("profile").get().await()
                
                val name = snapshot.child("name").value as? String ?: ""
                val email = snapshot.child("email").value as? String ?: auth.currentUser?.email ?: ""
                val phone = snapshot.child("phone").value as? String ?: ""
                
                etName.setText(name)
                etEmail.setText(email)
                etPhone.setText(phone)

                // Calculate balance from local DB (most up to date if synced)
                val localDb = AppDatabase.getDatabase(this@ProfileActivity)
                val transactions = withContext(Dispatchers.IO) {
                    localDb.transactionDao().getAllTransactions()
                }
                
                var total = 0.0
                transactions.forEach {
                    val amt = AppUtils.parseAmount(it.amount)
                    if (it.isExpense) total -= amt else total += amt
                }
                
                tvBalance.text = AppUtils.formatCurrency(total)

            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Lỗi khi tải dữ liệu: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateProfile() {
        val userId = auth.currentUser?.uid ?: return
        val newName = etName.text.toString().trim()
        val newEmail = etEmail.text.toString().trim()
        val newPhone = etPhone.text.toString().trim()
        val newPassword = etPassword.text.toString().trim()

        if (newName.isEmpty() || newEmail.isEmpty()) {
            Toast.makeText(this, "Vui lòng nhập tên và email", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val user = auth.currentUser!!
                
                // 1. Update Email if changed
                if (newEmail != user.email) {
                    user.updateEmail(newEmail).await()
                }
                
                // 2. Update Password if not empty
                if (newPassword.isNotEmpty()) {
                    if (newPassword.length < 6) {
                        Toast.makeText(this@ProfileActivity, "Mật khẩu phải >= 6 ký tự", Toast.LENGTH_SHORT).show()
                        return@launch
                    }
                    user.updatePassword(newPassword).await()
                }

                // 3. Update RTDB
                val updates = hashMapOf<String, Any>(
                    "name" to newName,
                    "email" to newEmail,
                    "phone" to newPhone
                )
                db.child("users").child(userId).child("profile").updateChildren(updates).await()
                
                // Check for invitations if email changed
                FirebaseSyncManager(this@ProfileActivity).checkPendingInvitations(newEmail, userId)

                Toast.makeText(this@ProfileActivity, "Cập nhật thành công!", Toast.LENGTH_SHORT).show()
                
                // Update local prefs
                val prefs = getSharedPreferences("user_prefs", MODE_PRIVATE)
                prefs.edit().putString("user_name", newName).apply()
                
            } catch (e: Exception) {
                val msg = if (e.message?.contains("recent login") == true) {
                    "Vui lòng đăng nhập lại để thực hiện thay đổi email/mật khẩu."
                } else {
                    "Lỗi cập nhật: ${e.message}"
                }
                Toast.makeText(this@ProfileActivity, msg, Toast.LENGTH_LONG).show()
            }
        }
    }
}