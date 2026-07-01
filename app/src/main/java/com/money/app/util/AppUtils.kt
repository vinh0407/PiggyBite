package com.money.app.util

import android.content.Context
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

/**
 * Lớp Exception tùy chỉnh cho dự án Money
 */
class MoneyException(message: String, cause: Throwable? = null) : Exception(message, cause)

/**
 * Singleton Utility: Chứa các hàm hỗ trợ chung về định dạng dữ liệu trong ứng dụng.
 */
object AppUtils {
    private val df = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
    private val dateFormats = listOf(
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    )

    /**
     * Định dạng số tiền (Double) thành chuỗi hiển thị kèm đơn vị tiền tệ.
     * Tự động chuyển đổi tỷ giá nếu người dùng chọn USD.
     */
    fun formatCurrency(amount: Double, context: Context? = null): String {
        return try {
            val currency = if (context != null) CurrencyHelper.getSelectedCurrency(context) else "VND"
            val convertedAmount = CurrencyHelper.convertFromBase(amount, currency)
            
            if (currency == "USD") {
                "$" + DecimalFormat("#,##0.00", DecimalFormatSymbols(Locale.US)).format(convertedAmount)
            } else {
                df.format(convertedAmount) + "đ"
            }
        } catch (e: Exception) {
            "0đ" // Trả về mặc định nếu lỗi định dạng
        }
    }

    /**
     * Chuyển đổi chuỗi nhập liệu (có thể chứa ký tự lạ) thành số Double an toàn.
     */
    fun parseAmount(amountStr: String?): Double {
        if (amountStr.isNullOrBlank()) return 0.0
        return try {
            // Xóa tất cả các ký tự không phải số, dấu chấm hoặc dấu gạch ngang
            val cleanStr = amountStr.replace("[^0-9.-]".toRegex(), "")
            cleanStr.toDoubleOrNull() ?: 0.0
        } catch (e: NumberFormatException) {
            0.0
        }
    }

    /**
     * Chuyển đổi chuỗi ngày tháng sang đối tượng Date, hỗ trợ nhiều định dạng khác nhau.
     */
    fun parseDate(dateStr: String): Date {
        for (format in dateFormats) {
            try {
                return format.parse(dateStr) ?: continue
            } catch (e: Exception) {}
        }
        return Date() // Trả về ngày hiện tại nếu không parse được
    }

    /**
     * Chuyển đổi số tiền thành văn bản đọc bằng tiếng Việt.
     * Ví dụ: 1000000 -> "Một triệu đồng"
     */
    fun toVietnameseWords(amount: Double): String {
        if (amount == 0.0) return "Không đồng"
        
        val units = arrayOf("", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ")
        
        var n = amount.toLong()
        if (n < 0) return "Âm " + toVietnameseWords(-amount.toDouble())
        
        var result = ""
        var unitIndex = 0
        
        // Chia số tiền thành từng nhóm 3 chữ số để xử lý
        while (n > 0) {
            val triplet = (n % 1000).toInt()
            if (triplet > 0) {
                val tripletText = tripletToWords(triplet, n >= 1000)
                result = "$tripletText ${units[unitIndex]} $result"
            }
            n /= 1000
            unitIndex++
        }
        
        return result.trim().replaceFirstChar { it.uppercase() } + " đồng"
    }

    /**
     * Xử lý đọc một nhóm 3 chữ số (Vd: 123 -> Một trăm hai mươi ba)
     */
    private fun tripletToWords(n: Int, hasHigher: Boolean): String {
        val numbers = arrayOf("không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín")
        val hundreds = n / 100
        val tens = (n % 100) / 10
        val units = n % 10
        
        var res = ""
        if (hundreds > 0 || hasHigher) {
            res += "${numbers[hundreds]} trăm "
        }
        
        if (tens > 1) {
            res += "${numbers[tens]} mươi "
            if (units == 1) res += "mốt"
            else if (units == 5) res += "lăm"
            else if (units > 0) res += numbers[units]
        } else if (tens == 1) {
            res += "mười "
            if (units == 5) res += "lăm"
            else if (units > 0) res += numbers[units]
        } else if (units > 0) {
            if (hundreds > 0 || hasHigher) res += "lẻ "
            res += numbers[units]
        }
        
        return res.trim()
    }
}
