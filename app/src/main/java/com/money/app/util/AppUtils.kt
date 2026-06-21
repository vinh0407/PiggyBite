package com.money.app.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.*

object AppUtils {
    private val df = DecimalFormat("#,###", DecimalFormatSymbols(Locale.US))

    fun formatCurrency(amount: Double): String {
        return df.format(amount) + "đ"
    }

    fun parseAmount(amountStr: String): Double {
        return amountStr.replace(",", "").toDoubleOrNull() ?: 0.0
    }

    fun toVietnameseWords(amount: Double): String {
        if (amount == 0.0) return "Không đồng"
        
        val units = arrayOf("", "nghìn", "triệu", "tỷ", "nghìn tỷ", "triệu tỷ")
        
        var n = amount.toLong()
        if (n < 0) return "Âm " + toVietnameseWords(-amount.toDouble())
        
        var result = ""
        var unitIndex = 0
        
        while (n > 0) {
            val triplet = (n % 1000).toInt()
            if (triplet > 0) {
                val tripletText = tripletToWords(triplet, n >= 1000)
                result = tripletText + " " + units[unitIndex] + " " + result
            }
            n /= 1000
            unitIndex++
        }
        
        return result.trim().replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() } + " đồng"
    }

    private fun tripletToWords(n: Int, hasHigher: Boolean): String {
        val numbers = arrayOf("không", "một", "hai", "ba", "bốn", "năm", "sáu", "bảy", "tám", "chín")
        val hundreds = n / 100
        val tens = (n % 100) / 10
        val units = n % 10
        
        var res = ""
        if (hundreds > 0 || hasHigher) {
            res += numbers[hundreds] + " trăm "
        }
        
        if (tens > 1) {
            res += numbers[tens] + " mươi "
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