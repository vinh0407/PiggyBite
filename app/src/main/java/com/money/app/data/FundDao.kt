package com.money.app.data

import androidx.room.*

@Dao
interface FundDao {
    @Query("SELECT * FROM funds ORDER BY isPinned DESC, createdDate DESC")
    suspend fun getAllFunds(): List<Fund>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fund: Fund)

    @Update
    suspend fun update(fund: Fund)

    @Delete
    suspend fun delete(fund: Fund)
    
    @Query("DELETE FROM funds")
    suspend fun clearAll()
}
