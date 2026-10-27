package com.money.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Hàng đợi đồng bộ Offline-First: Lưu các hành động cần ghi nhận lên Firebase khi khôi phục mạng.
 */
@Entity(tableName = "sync_queue")
data class SyncQueueItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val action: String = "CREATE", // CREATE, UPDATE, DELETE
    val entityType: String = "TRANSACTION", // TRANSACTION, FUND, ACCOUNT, BUDGET, DEBT
    val syncId: String = "",
    val payloadJson: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val isSynced: Boolean = false
)
