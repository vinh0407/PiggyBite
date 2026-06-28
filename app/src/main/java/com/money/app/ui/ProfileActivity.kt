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

        findViewById<TextView>(R.id.tvChangePassword).setOnClickListener {
            showChangePasswordDialog()
        }

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
                    val amt = it.amount
                    if (it.isExpense) total -= amt else total += amt
                }
                
                tvBalance.text = AppUtils.formatCurrency(total, this@ProfileActivity)

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

                // 2. Update RTDB
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
                    "Vui lòng đăng nhập lại để thực hiện thay đổi email."
                } else {
                    "Lỗi cập nhật: ${e.message}"
                }
                Toast.makeText(this@ProfileActivity, msg, Toast.LENGTH_LONG).show()
            }
        }
    }

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
                    Toast.makeText(this, "Mật khẩu phải >= 6 ký tự", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                performPasswordChange(oldPass, newPass)
                dialog.dismiss()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun performPasswordChange(oldPass: String, newPass: String) {
        val user = auth.currentUser ?: return
        val email = user.email ?: return
        
        lifecycleScope.launch {
            try {
                // Re-authenticate user
                val credential = EmailAuthProvider.getCredential(email, oldPass)
                user.reauthenticate(credential).await()
                
                // Update password
                user.updatePassword(newPass).await()
                Toast.makeText(this@ProfileActivity, "Đổi mật khẩu thành công!", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ProfileActivity, "Lỗi: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}