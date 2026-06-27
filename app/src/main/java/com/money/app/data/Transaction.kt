package com.money.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "transactions",
    indices = [Index(value = ["syncId"], unique = true)]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val syncId: String = java.util.UUID.randomUUID().toString(),
    val userId: String = "",
    val amount: String = "0",
    val category: String = "",
    val date: String = "",
    val description: String = "",
    val imagePath: String? = null,
    val isExpense: Boolean = true,
    val rating: Int = 0,
    val timestamp: Long = System.currentTimeMillis()
)
