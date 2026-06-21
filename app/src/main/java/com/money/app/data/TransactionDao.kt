package com.money.app.data

import androidx.room.*

@Dao
interface TransactionDao {
    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestTransaction(): Transaction?

    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<Transaction>

    @Delete
    suspend fun delete(transaction: Transaction)

    @Query("SELECT * FROM transactions WHERE isExpense = 1 AND timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getExpensesInTimeRange(startTime: Long, endTime: Long): List<Transaction>

    @Query("SELECT * FROM transactions WHERE timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getTransactionsInTimeRange(startTime: Long, endTime: Long): List<Transaction>

    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}
