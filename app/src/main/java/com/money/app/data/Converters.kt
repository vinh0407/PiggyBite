package com.money.app.data

import androidx.room.TypeConverter

/**
 * Lớp chuyển đổi dữ liệu để Room Database có thể lưu trữ các kiểu dữ liệu phức tạp
 * như List (Danh sách) hoặc Map (Bản đồ) dưới dạng chuỗi String.
 */
class Converters {
    
    // Chuyển chuỗi String (phân cách bằng dấu phẩy) ngược lại thành List<String>
    @TypeConverter
    fun fromString(value: String): List<String> {
        return if (value.isEmpty()) listOf() else value.split(",")
    }

    // Chuyển List<String> thành một chuỗi String duy nhất để lưu vào database
    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }

    // Chuyển Map (ID người dùng -> Số tiền đóng góp) thành chuỗi String (k1:v1|k2:v2)
    @TypeConverter
    fun fromMap(map: Map<String, Double>): String {
        val sb = StringBuilder()
        map.forEach { (k, v) ->
            if (sb.isNotEmpty()) sb.append("|")
            sb.append("$k:$v")
        }
        return sb.toString()
    }

    // Chuyển chuỗi String ngược lại thành Map<String, Double> để sử dụng trong code
    @TypeConverter
    fun toMap(value: String): Map<String, Double> {
        if (value.isEmpty()) return mapOf()
        return value.split("|").associate {
            val parts = it.split(":")
            if (parts.size >= 2) {
                parts[0] to (parts[1].toDoubleOrNull() ?: 0.0)
            } else {
                "" to 0.0
            }
        }
    }
}
