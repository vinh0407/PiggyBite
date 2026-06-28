package com.money.app.util

import android.content.Context
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

/**
 * Custom Exception for Money project to demonstrate Exception Handling
 */
class MoneyException(message: String, cause: Throwable? = null) : Exception(message, cause)

object AppUtils {
    private val df = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))
    private val dateFormats = listOf(
        SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()),
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    )

    /**
     * Number Handling: Formats Double to Currency String
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
            "0đ" // Fallback if formatting fails
        }
    }

    /**
     * String & Number Handling: Parses String to Double safely
     */
    fun parseAmount(amountStr: String?): Double {
        if (amountStr.isNullOrBlank()) return 0.0
        return try {
            // String manipulation: remove separators and currency symbols
            val cleanStr = amountStr.replace("[^0-9.-]".toRegex(), "")
            cleanStr.toDoubleOrNull() ?: 0.0
        } catch (e: NumberFormatException) {
            0.0
        }
    }

    /**
     * Date Handling: Parses various date strings to Date object
     */
    fun parseDate(dateStr: String): Date {
        for (format in dateFormats) {
            try {
                return format.parse(dateStr) ?: continue
            } catch (e: Exception) {
                // Try next format
            }
        }
        return Date() // Default to now if all fail
    }

    /**
     * String & Logic Handling: Converts amount to Vietnamese words
     */
    fun toVietnameseWords(amount: Double): String {
        // String handling: null/empty/zero checks
        if (amount == 0.0) return "Không đồng"
        
        val units = arrayOf("", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ")
        
        // Number handling: Conversion to Long for precision in word mapping
        var n = amount.toLong()
        if (n < 0) return "Âm " + toVietnameseWords(-amount.toDouble())
        
        var result = ""
        var unitIndex = 0
        
        while (n > 0) {
            val triplet = (n % 1000).toInt()
            if (triplet > 0) {
                val tripletText = tripletToWords(triplet, n >= 1000)
                // String template usage
                result = "$tripletText ${units[unitIndex]} $result"
            }
            n /= 1000
            unitIndex++
        }
        
        // Advanced String manipulation: trim, capitalize, and replace
        return result.trim().replaceFirstChar { 
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() 
        } + " đồng"
    }

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
