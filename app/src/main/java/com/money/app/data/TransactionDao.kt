package com.money.app.data

import androidx.room.*

/**
 * Interface cung cấp các phương thức truy vấn dữ liệu cho bảng giao dịch (transactions).
 */
@Dao
interface TransactionDao {
    
    // Chèn hoặc cập nhật một giao dịch. Nếu trùng syncId sẽ ghi đè (REPLACE)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: Transaction): Long

    // Lấy giao dịch mới nhất vừa thực hiện
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC LIMIT 1")
    suspend fun getLatestTransaction(): Transaction?

    // Lấy toàn bộ danh sách giao dịch (chạy một lần)
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    suspend fun getAllTransactions(): List<Transaction>

    // Lấy dòng dữ liệu giao dịch dưới dạng Flow (tự động cập nhật UI khi database thay đổi)
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getAllTransactionsFlow(): kotlinx.coroutines.flow.Flow<List<Transaction>>

    // Xóa một giao dịch khỏi máy
    @Delete
    suspend fun delete(transaction: Transaction)

    // Lấy danh sách chi tiêu trong một khoảng thời gian nhất định (cho báo cáo)
    @Query("SELECT * FROM transactions WHERE isExpense = 1 AND timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getExpensesInTimeRange(startTime: Long, endTime: Long): List<Transaction>

    // Lấy tất cả thu/chi trong một khoảng thời gian
    @Query("SELECT * FROM transactions WHERE timestamp >= :startTime AND timestamp <= :endTime")
    suspend fun getTransactionsInTimeRange(startTime: Long, endTime: Long): List<Transaction>

    // Tìm kiếm giao dịch theo từ khóa trong hạng mục hoặc mô tả
    @Query("SELECT * FROM transactions WHERE category LIKE :query OR description LIKE :query")
    suspend fun searchTransactions(query: String): List<Transaction>

    // Xóa sạch toàn bộ dữ liệu giao dịch (thường dùng khi đăng xuất)
    @Query("DELETE FROM transactions")
    suspend fun clearAll()
}
