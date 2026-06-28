package com.money.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.money.app.R
import com.money.app.util.FirebaseSyncManager
import com.money.app.util.ThemeHelper
import com.money.app.util.CurrencyHelper
import android.widget.TextView
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        findViewById<View>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        findViewById<View>(R.id.btnTheme).setOnClickListener {
            showThemeSelectionDialog()
        }

        val tvCurrency = findViewById<TextView>(R.id.tvCurrencyValue)
        tvCurrency.text = CurrencyHelper.getSelectedCurrency(this)
        
        findViewById<View>(R.id.btnCurrency).setOnClickListener {
            showCurrencySelectionDialog(tvCurrency)
        }

        findViewById<View>(R.id.btnLogout).setOnClickListener {
            lifecycleScope.launch {
                val syncManager = FirebaseSyncManager(this@SettingsActivity)
                
                // 1. Upload all local data to Firestore before logout
                Toast.makeText(this@SettingsActivity, "Đang đồng bộ dữ liệu...", Toast.LENGTH_SHORT).show()
                syncManager.uploadAllLocalData()
                
                // 2. Clear local data
                syncManager.clearLocalData()

                // 3. Sign out from Firebase
                FirebaseAuth.getInstance().signOut()

                // 4. Clear local prefs
                val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                prefs.edit().clear().apply()

                // 5. Go back to Login Screen
                val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun showThemeSelectionDialog() {
        val themes = arrayOf("Sáng", "Tối", "Hệ thống")
        val checkedItem = ThemeHelper.getSavedTheme(this)

        AlertDialog.Builder(this)
            .setTitle("Chọn giao diện")
            .setSingleChoiceItems(themes, checkedItem) { dialog, which ->
                ThemeHelper.saveTheme(this, which)
                dialog.dismiss()
                recreate() // Apply theme changes immediately
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    private fun showCurrencySelectionDialog(tvValue: TextView) {
        val currencies = arrayOf("VND", "USD")
        val current = CurrencyHelper.getSelectedCurrency(this)
        val checkedItem = if (current == "USD") 1 else 0

        AlertDialog.Builder(this)
            .setTitle("Chọn loại tiền tệ")
            .setSingleChoiceItems(currencies, checkedItem) { dialog, which ->
                val selected = currencies[which]
                CurrencyHelper.saveCurrency(this, selected)
                tvValue.text = selected
                dialog.dismiss()
                Toast.makeText(this, "Đã đổi tiền tệ thành $selected", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}