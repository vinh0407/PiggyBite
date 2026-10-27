package com.money.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity đại diện cho một Ngân sách chi tiêu (Theo tháng/tuần và hạng mục).
 */
@Entity(
    tableName = "budgets",
    indices = [Index(value = ["syncId"], unique = true)]
)
data class Budget(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val syncId: String = java.util.UUID.randomUUID().toString(),
    val userId: String = "",
    val category: String = "",
    val amountLimit: Double = 0.0,
    val alertThreshold: Double = 0.8, // 80% cảnh báo mặc định
    val period: String = "MONTHLY", // MONTHLY, WEEKLY
    val monthYear: String = "", // MM/yyyy
    val updatedAt: Long = System.currentTimeMillis()
)
