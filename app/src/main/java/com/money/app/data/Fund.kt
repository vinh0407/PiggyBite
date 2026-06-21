package com.money.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "funds")
data class Fund(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    var currentAmount: Double,
    var targetAmount: Double,
    val icon: String,
    val createdDate: Long,
    val endDate: Long,
    var isPinned: Boolean = false
)
