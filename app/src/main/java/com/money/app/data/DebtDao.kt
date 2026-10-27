package com.money.app.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface DebtDao {
    @Query("SELECT * FROM debts ORDER BY isSettled ASC, dueDate ASC")
    fun getAllDebtsFlow(): Flow<List<Debt>>

    @Query("SELECT * FROM debts")
    suspend fun getAllDebts(): List<Debt>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(debt: Debt): Long

    @Update
    suspend fun update(debt: Debt)

    @Delete
    suspend fun delete(debt: Debt)

    @Query("DELETE FROM debts")
    suspend fun clearAll()
}
