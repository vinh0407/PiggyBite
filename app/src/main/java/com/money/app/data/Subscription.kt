package com.money.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Entity theo dõi các gói đăng ký định kỳ (Netflix, Spotify, Google One, Gym, SaaS, v.v.).
 */
@Entity(
    tableName = "subscriptions",
    indices = [Index(value = ["syncId"], unique = true)]
)
data class Subscription(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val syncId: String = java.util.UUID.randomUUID().toString(),
    val userId: String = "",
    val name: String = "",
    val icon: String = "bag",
    val cost: Double = 0.0,
    val billingCycle: String = "MONTHLY", // MONTHLY, ANNUAL
    val nextBillingDate: Long = 0L,
    val category: String = "Subscriptions",
    val updatedAt: Long = System.currentTimeMillis()
)
