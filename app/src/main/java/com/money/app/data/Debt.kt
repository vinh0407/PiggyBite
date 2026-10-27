package com.money.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity đại diện cho Khoản nợ (TÔI NỢ hoặc NGƯỜI KHÁC NỢ TÔI).
 */
@Entity(
    tableName = "debts",
    indices = [Index(value = ["syncId"], unique = true)]
)
data class Debt(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val syncId: String = java.util.UUID.randomUUID().toString(),
    val userId: String = "",
    val personName: String = "",
    val amount: Double = 0.0,
    val isOwedToMe: Boolean = true, // true: OWED TO ME (Người khác nợ tôi), false: I OWE (Tôi nợ)
    val interestRate: Double = 0.0,
    val startDate: Long = System.currentTimeMillis(),
    val dueDate: Long = 0L,
    val notes: String = "",
    val isSettled: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
