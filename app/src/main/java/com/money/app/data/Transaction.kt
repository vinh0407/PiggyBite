package com.money.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Lớp Entity đại diện cho một giao dịch tài chính (Thu nhập hoặc Chi tiêu).
 * Được cấu hình để lưu trữ trong Room Database và tương thích với việc đồng bộ Firebase.
 * 
 * @property amount Giá trị số tiền thô (mặc định là VND).
 * @property syncId ID duy nhất dùng để đồng bộ dữ liệu giữa thiết bị và Firebase.
 */
@Entity(
    tableName = "transactions",
    indices = [Index(value = ["syncId"], unique = true)] // Đảm bảo syncId không bị trùng lặp
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val syncId: String = java.util.UUID.randomUUID().toString(),
    val userId: String = "",
    val amount: Double = 0.0,
    val category: String = "",
    val date: String = "", // Định dạng dd/MM/yyyy
    val description: String = "",
    val imagePath: String? = null, // Đường dẫn ảnh hóa đơn đính kèm (nếu có)
    val isExpense: Boolean = true, // true: Chi tiêu, false: Thu nhập
    val rating: Int = 0,
    val timestamp: Long = System.currentTimeMillis() // Thời gian tạo để sắp xếp
)
