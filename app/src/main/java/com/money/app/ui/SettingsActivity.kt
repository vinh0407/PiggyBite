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

/**
 * Màn hình Cài đặt (Settings): Nơi người dùng tùy chỉnh ứng dụng và quản lý phiên đăng nhập.
 * Các tính năng:
 * - Thay đổi Giao diện (Sáng/Tối/Hệ thống).
 * - Thay đổi Đơn vị tiền tệ (VND/USD).
 * - Xem và chỉnh sửa Profile.
 * - Đăng xuất an toàn: Đồng bộ dữ liệu lên mây lần cuối trước khi xóa dữ liệu cục bộ.
 */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<ImageButton>(R.id.btnBack).setOnClickListener { finish() }

        // Mở màn hình Profile
        findViewById<View>(R.id.btnProfile).setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
        }

        // Thay đổi chủ đề (Theme)
        findViewById<View>(R.id.btnTheme).setOnClickListener {
            showThemeSelectionDialog()
        }

        // Hiển thị và thay đổi tiền tệ
        val tvCurrency = findViewById<TextView>(R.id.tvCurrencyValue)
        tvCurrency.text = CurrencyHelper.getSelectedCurrency(this)
        
        findViewById<View>(R.id.btnCurrency).setOnClickListener {
            showCurrencySelectionDialog(tvCurrency)
        }

        // Xử lý Đăng xuất
        findViewById<View>(R.id.btnLogout).setOnClickListener {
            performLogout()
        }
    }

    /**
     * Quy trình Đăng xuất an toàn:
     * 1. Đẩy toàn bộ dữ liệu giao dịch mới từ máy lên Firebase.
     * 2. Xóa sạch Database Room cục bộ.
     * 3. Xóa SharedPreferences.
     * 4. Gọi signOut() của Firebase.
     */
    private fun performLogout() {
        lifecycleScope.launch {
            val syncManager = FirebaseSyncManager(this@SettingsActivity)
            
            Toast.makeText(this@SettingsActivity, "Đang đồng bộ dữ liệu lần cuối...", Toast.LENGTH_SHORT).show()
            syncManager.uploadAllLocalData() // Đảm bảo dữ liệu không bị mất
            
            syncManager.clearLocalData() // Xóa dữ liệu cục bộ để bảo mật

            FirebaseAuth.getInstance().signOut()

            val prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()

            // Quay lại màn hình Đăng nhập và xóa sạch stack Activity
            val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }
    }

    /**
     * Hộp thoại chọn chủ đề Ứng dụng
     */
    private fun showThemeSelectionDialog() {
        val themes = arrayOf("Sáng", "Tối", "Hệ thống")
        val checkedItem = ThemeHelper.getSavedTheme(this)

        AlertDialog.Builder(this)
            .setTitle("Chọn giao diện")
            .setSingleChoiceItems(themes, checkedItem) { dialog, which ->
                ThemeHelper.saveTheme(this, which)
                dialog.dismiss()
                recreate() // Làm mới lại Activity để áp dụng theme ngay lập tức
            }
            .setNegativeButton("Hủy", null)
            .show()
    }

    /**
     * Hộp thoại chọn đơn vị tiền tệ chính
     */
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
                // Cần làm mới lại dữ liệu ở các màn hình khác (onResume sẽ xử lý)
            }
            .setNegativeButton("Hủy", null)
            .show()
    }
}
