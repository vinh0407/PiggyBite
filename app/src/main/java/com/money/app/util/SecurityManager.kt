package com.money.app.util

import android.content.Context
import android.content.SharedPreferences
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import java.util.concurrent.Executor

/**
 * Quản lý Bảo mật (Security Manager):
 * Phụ trách Khóa ứng dụng, Vân tay/Khuôn mặt (BiometricPrompt), Mã PIN bảo mật và Thời gian chờ phiên làm việc.
 */
class SecurityManager(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("vinora_security_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_APP_LOCK_ENABLED = "app_lock_enabled"
        private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
        private const val KEY_PIN_CODE = "pin_code"
        private const val KEY_LAST_ACTIVE = "last_active_timestamp"
        private const val SESSION_TIMEOUT_MS = 5 * 60 * 1000L // 5 phút không hoạt động
    }

    fun isAppLockEnabled(): Boolean = prefs.getBoolean(KEY_APP_LOCK_ENABLED, false)

    fun setAppLockEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_APP_LOCK_ENABLED, enabled).apply()
    }

    fun isBiometricEnabled(): Boolean = prefs.getBoolean(KEY_BIOMETRIC_ENABLED, true)

    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun savePinCode(pin: String) {
        prefs.edit().putString(KEY_PIN_CODE, pin).apply()
    }

    fun verifyPinCode(pin: String): Boolean {
        val savedPin = prefs.getString(KEY_PIN_CODE, "")
        return savedPin.isNullOrEmpty() || savedPin == pin
    }

    fun updateLastActiveTimestamp() {
        prefs.edit().putLong(KEY_LAST_ACTIVE, System.currentTimeMillis()).apply()
    }

    fun isSessionExpired(): Boolean {
        if (!isAppLockEnabled()) return false
        val lastActive = prefs.getLong(KEY_LAST_ACTIVE, 0L)
        if (lastActive == 0L) return true
        return (System.currentTimeMillis() - lastActive) > SESSION_TIMEOUT_MS
    }

    fun canAuthenticateBiometrics(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return biometricManager.canAuthenticate(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.BIOMETRIC_WEAK
        ) == BiometricManager.BIOMETRIC_SUCCESS
    }

    fun authenticateBiometrics(
        activity: FragmentActivity,
        title: String = "Xác thực sinh trắc học",
        subtitle: String = "Sử dụng vân tay hoặc khuôn mặt để mở khóa VINORA",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor: Executor = ContextCompat.getMainExecutor(context)
        val biometricPrompt = BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                updateLastActiveTimestamp()
                onSuccess()
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                onError(errString.toString())
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("Xác thực thất bại. Vui lòng thử lại.")
            }
        })

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Hủy")
            .build()

        biometricPrompt.authenticate(promptInfo)
    }
}
