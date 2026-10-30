package com.piggybite.app.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {

    fun formatMoney(amount: Long, currencySuffix: String = "đ"): String {
        val symbols = DecimalFormatSymbols(Locale("vi", "VN")).apply {
            groupingSeparator = '.'
        }
        val df = DecimalFormat("#,###", symbols)
        return "${df.format(amount)} $currencySuffix"
    }

    fun formatDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("vi", "VN"))
        return sdf.format(Date(timestamp))
    }

    fun formatShortDate(timestamp: Long): String {
        val sdf = SimpleDateFormat("dd/MM", Locale("vi", "VN"))
        return sdf.format(Date(timestamp))
    }

    fun getVietnameseTodayString(): String {
        val cal = SimpleDateFormat("d 'tháng' M, yyyy", Locale("vi", "VN"))
        return "Hôm nay, " + cal.format(Date())
    }
}
