package com.money.app.data

import androidx.room.*

/**
 * Interface cung cấp các phương thức truy vấn cho bảng Quỹ (funds).
 */
@Dao
interface FundDao {
    // Lấy toàn bộ danh sách quỹ, ưu tiên các quỹ được ghim lên trước
    @Query("SELECT * FROM funds ORDER BY isPinned DESC, createdDate DESC")
    suspend fun getAllFunds(): List<Fund>

    // Thêm một quỹ mới hoặc ghi đè nếu đã tồn tại
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(fund: Fund)

    // Cập nhật thông tin quỹ (số dư, thành viên...)
    @Update
    suspend fun update(fund: Fund)

    // Xóa một quỹ
    @Delete
    suspend fun delete(fund: Fund)
    
    // Xóa toàn bộ danh sách quỹ (khi đăng xuất)
    @Query("DELETE FROM funds")
    suspend fun clearAll()
}
