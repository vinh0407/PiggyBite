package com.money.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface InvestmentDao {
    @Query("SELECT * FROM investments ORDER BY assetType ASC")
    fun getAllInvestmentsFlow(): Flow<List<Investment>>

    @Query("SELECT * FROM investments")
    suspend fun getAllInvestments(): List<Investment>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(investment: Investment): Long

    @Update
    suspend fun update(investment: Investment)

    @Delete
    suspend fun delete(investment: Investment)

    @Query("DELETE FROM investments")
    suspend fun clearAll()
}
