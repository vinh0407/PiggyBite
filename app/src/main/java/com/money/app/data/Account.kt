package com.money.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity đại diện cho một Tài khoản tài chính (Tiền mặt, Ngân hàng, Ví điện tử, Thẻ tín dụng, v.v.).
 */
@Entity(
    tableName = "accounts",
    indices = [Index(value = ["syncId"], unique = true)]
)
data class Account(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val syncId: String = java.util.UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val type: String = "CASH", // CASH, BANK, EWALLET, SAVINGS, INVESTMENT, CREDIT_CARD, LOAN
    val balance: Double = 0.0,
    val currency: String = "VND",
    val institution: String = "",
    val color: String = "#1E69FF",
    val accountNumberMasked: String = "",
    val notes: String = "",
    val updatedAt: Long = System.currentTimeMillis()
)
