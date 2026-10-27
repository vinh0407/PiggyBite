package com.money.app.util

import android.content.Context
import com.google.firebase.database.FirebaseDatabase
import com.money.app.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Điều phối Hàng đợi Đồng bộ Offline-First (SyncQueueDispatcher):
 * Tự động kiểm tra các thao tác tài chính chưa được gửi lên Firebase khi thiết bị ngoại tuyến (Offline),
 * đẩy dữ liệu lên mây khi khôi phục mạng và đánh dấu đã đồng bộ (isSynced = true).
 */
class SyncQueueDispatcher(private val context: Context) {
    private val appDb = AppDatabase.getDatabase(context)
    private val db = FirebaseDatabase.getInstance().reference

    suspend fun dispatchPendingSyncQueue(): Int = withContext(Dispatchers.IO) {
        var syncedCount = 0
        try {
            val unsyncedItems = appDb.syncQueueDao().getUnsyncedItems()
            unsyncedItems.forEach { item ->
                if (item.entityType == "TRANSACTION") {
                    db.child("pending_queue").child(item.syncId).setValue(item.payloadJson).await()
                    appDb.syncQueueDao().markSynced(item.id)
                    syncedCount++
                }
            }
            appDb.syncQueueDao().clearSynced()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        syncedCount
    }
}
