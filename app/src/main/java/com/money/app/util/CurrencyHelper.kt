package com.money.app.util

import android.content.Context

object CurrencyHelper {
    private const val PREFS_NAME = "currency_prefs"
    private const val KEY_CURRENCY = "selected_currency"

    const val CURRENCY_VND = "VND"
    const val CURRENCY_USD = "USD"
    
    // Tỷ giá hối đoái (Ví dụ: 1 USD = 25,000 VND)
    const val EXCHANGE_RATE_USD_TO_VND = 25000.0

    fun saveCurrency(context: Context, currency: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CURRENCY, currency).apply()
    }

    fun getSelectedCurrency(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_CURRENCY, CURRENCY_VND) ?: CURRENCY_VND
    }
    
    /**
     * Quy đổi giá trị từ VND (đơn vị gốc) sang loại tiền tệ đích
     */
    fun convertFromBase(amountVnd: Double, targetCurrency: String): Double {
        return if (targetCurrency == CURRENCY_USD) {
            amountVnd / EXCHANGE_RATE_USD_TO_VND
        } else {
            amountVnd
        }
    }
}