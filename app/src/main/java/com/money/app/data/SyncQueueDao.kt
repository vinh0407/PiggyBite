package com.money.app.data

import androidx.room.*

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue WHERE isSynced = 0 ORDER BY timestamp ASC")
    suspend fun getUnsyncedItems(): List<SyncQueueItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: SyncQueueItem): Long

    @Query("UPDATE sync_queue SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: Long)

    @Query("DELETE FROM sync_queue WHERE isSynced = 1")
    suspend fun clearSynced()
}
