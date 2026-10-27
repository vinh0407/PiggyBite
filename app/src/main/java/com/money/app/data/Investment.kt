package com.money.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity đại diện cho tài sản đầu tư (Cổ phiếu, Crypto, Vàng, Quỹ đầu tư, ETF).
 */
@Entity(
    tableName = "investments",
    indices = [Index(value = ["syncId"], unique = true)]
)
data class Investment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val syncId: String = java.util.UUID.randomUUID().toString(),
    val userId: String = "",
    val assetName: String = "",
    val assetType: String = "STOCKS", // STOCKS, CRYPTO, GOLD, ETF, FUNDS
    val quantity: Double = 0.0,
    val averageCost: Double = 0.0,
    val currentValue: Double = 0.0,
    val currency: String = "VND",
    val updatedAt: Long = System.currentTimeMillis()
)
