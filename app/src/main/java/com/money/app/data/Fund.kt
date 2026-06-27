package com.money.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "funds",
    indices = [Index(value = ["syncId"], unique = true)]
)
data class Fund(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val syncId: String = java.util.UUID.randomUUID().toString(),
    val ownerId: String = "", 
    val name: String,
    var currentAmount: Double,
    var targetAmount: Double,
    val icon: String,
    val createdDate: Long,
    val endDate: Long,
    var isPinned: Boolean = false,
    var isShared: Boolean = false,
    var members: List<String> = listOf(),
    var memberContributions: Map<String, Double> = mapOf()
)
