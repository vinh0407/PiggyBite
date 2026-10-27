package com.money.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity đại diện cho một Hóa đơn nhắc nhở định kỳ (Điện, Nước, Internet, Tiền nhà, v.v.).
 */
@Entity(
    tableName = "bills",
    indices = [Index(value = ["syncId"], unique = true)]
)
data class Bill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val syncId: String = java.util.UUID.randomUUID().toString(),
    val userId: String = "",
    val title: String = "",
    val amount: Double = 0.0,
    val dueDate: Long = 0L,
    val recurringCycle: String = "MONTHLY", // MONTHLY, QUARTERLY, YEARLY
    val accountId: String = "",
    val autoTransaction: Boolean = false,
    val isPaid: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
