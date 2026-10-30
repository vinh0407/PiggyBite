package com.piggybite.app.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Formatters {

    fun formatMoney(amount: Long, currencySuffix: String = "đ"): String {
        val symbols = DecimalFormatSymbols(Locale.US).apply {
            groupingSeparator = ','
        }
        val df = DecimalFormat("#,###", symbols)
        return "${df.format(amount)} $currencySuffix"
    }

    /**
     * Định dạng số có dấu phẩy sau mỗi 3 chữ số (ví dụ: 100,000 hoặc 1,000,000)
     */
    fun formatWithComma(amount: Long): String {
        return String.format(Locale.US, "%,d", amount)
    }

    /**
     * Tự động thêm dấu phẩy sau mỗi 3 số khi người dùng nhập/sửa tiền
     * Xử lý thông minh khi xóa lùi (backspace) trúng dấu phẩy
     */
    fun formatAmountInput(oldText: String, newInput: String): Pair<String, Long> {
        val oldDigits = oldText.filter { it.isDigit() }
        var newDigits = newInput.filter { it.isDigit() }

        // Nếu người dùng vừa nhấn xóa (backspace) trúng dấu phẩy
        if (newInput.length == oldText.length - 1 && newDigits.length == oldDigits.length && newDigits.isNotEmpty()) {
            newDigits = newDigits.dropLast(1)
        }

        if (newDigits.isEmpty()) {
            return Pair("", 0L)
        }

        val maxDigits = 14
        val capped = if (newDigits.length > maxDigits) newDigits.take(maxDigits) else newDigits
        val value = capped.toLongOrNull() ?: 0L
        val formatted = String.format(Locale.US, "%,d", value)
        return Pair(formatted, value)
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
