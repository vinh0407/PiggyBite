package com.money.app.data

import androidx.room.TypeConverter

class Converters {
    @TypeConverter
    fun fromString(value: String): List<String> {
        return if (value.isEmpty()) listOf() else value.split(",")
    }

    @TypeConverter
    fun fromList(list: List<String>): String {
        return list.joinToString(",")
    }

    @TypeConverter
    fun fromMap(map: Map<String, Double>): String {
        val sb = StringBuilder()
        map.forEach { (k, v) ->
            if (sb.isNotEmpty()) sb.append("|")
            sb.append("$k:$v")
        }
        return sb.toString()
    }

    @TypeConverter
    fun toMap(value: String): Map<String, Double> {
        if (value.isEmpty()) return mapOf()
        return value.split("|").associate {
            val parts = it.split(":")
            parts[0] to (parts[1].toDoubleOrNull() ?: 0.0)
        }
    }
}
