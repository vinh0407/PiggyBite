package com.money.app.util

import android.content.Context

/**
 * Hỗ trợ Tiền tệ (Currency Helper): Quản lý việc lựa chọn và quy đổi tỷ giá tiền tệ trong ứng dụng.
 * Mặc định hệ thống lưu trữ số tiền dưới dạng VND (Base Currency).
 */
object CurrencyHelper {
    private const val PREFS_NAME = "currency_prefs"
    private const val KEY_CURRENCY = "selected_currency"

    const val CURRENCY_VND = "VND"
    const val CURRENCY_USD = "USD"
    
    // Tỷ giá hối đoái giả định (1 USD = 25,000 VND)
    const val EXCHANGE_RATE_USD_TO_VND = 25000.0

    /**
     * Lưu lựa chọn tiền tệ của người dùng vào SharedPreferences.
     */
    fun saveCurrency(context: Context, currency: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CURRENCY, currency).apply()
    }

    /**
     * Lấy đơn vị tiền tệ đang được sử dụng (Mặc định là VND).
     */
    fun getSelectedCurrency(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CURRENCY, CURRENCY_VND) ?: CURRENCY_VND
    }
    
    /**
     * Chuyển đổi số tiền từ VND sang loại tiền tệ được chọn (VND hoặc USD).
     */
    fun convertFromBase(amountVnd: Double, targetCurrency: String): Double {
        return if (targetCurrency == CURRENCY_USD) {
            amountVnd / EXCHANGE_RATE_USD_TO_VND
        } else {
            amountVnd
        }
    }
}
