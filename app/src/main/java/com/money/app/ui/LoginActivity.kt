package com.money.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.*
import com.google.firebase.database.FirebaseDatabase
import com.money.app.R
import com.money.app.util.FirebaseSyncManager
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginActivity : AppCompatActivity() {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseDatabase.getInstance().reference

    private var isPhoneMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        if (auth.currentUser != null) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        
        setContentView(R.layout.activity_login)

        val rgMethod = findViewById<RadioGroup>(R.id.rgLoginMethod)
        val emailContainer = findViewById<View>(R.id.emailContainer)
        val phoneContainer = findViewById<View>(R.id.phoneContainer)
        
        val etEmail = findViewById<EditText>(R.id.etEmail)
        val etPassword = findViewById<EditText>(R.id.etPassword)
        val etPhone = findViewById<EditText>(R.id.etPhone)
        val etPhonePassword = findViewById<EditText>(R.id.etPhonePassword)
        val btnLogin = findViewById<Button>(R.id.btnLogin)
        val tvRegister = findViewById<TextView>(R.id.tvRegister)
        val tvForgot = findViewById<TextView>(R.id.tvForgotPassword)

        rgMethod.setOnCheckedChangeListener { _, checkedId ->
            isPhoneMode = checkedId == R.id.rbPhone
            emailContainer.visibility = if (isPhoneMode) View.GONE else View.VISIBLE
            phoneContainer.visibility = if (isPhoneMode) View.VISIBLE else View.GONE
        }

        btnLogin.setOnClickListener {
            if (isPhoneMode) {
                val phone = etPhone.text.toString().trim()
                val pass = etPhonePassword.text.toString().trim()
                if (phone.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                // Phone to Email transformation
                val shadowEmail = "phone_${phone}@piggybite.com"
                signInWithEmail(shadowEmail, pass)
            } else {
                val email = etEmail.text.toString().trim()
                val password = etPassword.text.toString().trim()

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                signInWithEmail(email, password)
            }
        }

        tvForgot.setOnClickListener {
            showForgotPasswordDialog()
        }

        tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showForgotPasswordDialog() {
        val et = EditText(this)
        et.hint = "Nhập Email của bạn"
        et.setPadding(60, 40, 60, 40)
        
        AlertDialog.Builder(this)
            .setTitle("Khôi phục mật khẩu")
            .setMessage("Chúng tôi sẽ gửi liên kết đổi mật khẩu vào email này.")
            .setView(et)
            .setPositiveButton("Gửi") { _, _ ->
                val email = et.text.toString().trim()
                if (email.isNotEmpty()) {
                    auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Đã gửi email khôi phục", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this, "Lỗi: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun signInWithEmail(email: String, pass: String) {
        lifecycleScope.launch {
            try {
                val result = auth.signInWithEmailAndPassword(email, pass).await()
                handleSuccessfulLogin(result.user)
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Tài khoản hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun handleSuccessfulLogin(user: FirebaseUser?) {
        val userId = user?.uid ?: return
        try {
            val snapshot = db.child("users").child(userId).child("profile").get().await()
            val name = snapshot.child("name").value as? String ?: "User"
            val emailFromDb = snapshot.child("email").value as? String ?: user.email ?: ""

            val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            prefs.edit().apply {
                putString("user_name", name)
                putString("user_email", emailFromDb)
                putString("user_id", userId)
                apply()
            }

            val syncManager = FirebaseSyncManager(this@LoginActivity)
            syncManager.clearLocalData()
            syncManager.checkPendingInvitations(emailFromDb, userId)
            syncManager.syncTransactions()
            syncManager.syncFunds()

            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, "Lỗi đồng bộ: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}