package com.money.app.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Lớp Entity đại diện cho một Quỹ (Fund) - có thể là quỹ tiết kiệm cá nhân hoặc quỹ chung.
 * Chứa thông tin về số tiền hiện có, mục tiêu và các thành viên tham gia đóng góp.
 */
@Entity(
    tableName = "funds",
    indices = [Index(value = ["syncId"], unique = true)]
)
data class Fund(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val syncId: String = java.util.UUID.randomUUID().toString(), // ID dùng để đồng bộ Firebase
    val ownerId: String = "", // ID người tạo quỹ
    val name: String, // Tên quỹ (Vd: "Tiền cưới", "Quỹ ăn nhậu")
    var currentAmount: Double, // Số tiền hiện đang có trong quỹ
    var targetAmount: Double, // Số tiền mục tiêu cần đạt được
    val icon: String, // Icon đại diện (Emoji)
    val createdDate: Long,
    val endDate: Long,
    var isPinned: Boolean = false, // Có được ghim lên đầu trang chủ không
    var isShared: Boolean = false, // Có phải là quỹ chung chia sẻ với người khác không
    var members: List<String> = listOf(), // Danh sách ID các thành viên tham gia
    var memberContributions: Map<String, Double> = mapOf() // Bản đồ ghi nhận số tiền từng người đã đóng góp
)
