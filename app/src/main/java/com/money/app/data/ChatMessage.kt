package com.money.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entity đại diện cho một tin nhắn trong cửa sổ Chat với AI.
 * Lưu trữ lịch sử trò chuyện cục bộ trên máy.
 */
@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String, // Nội dung tin nhắn
    val isUser: Boolean, // true: Tin nhắn của người dùng, false: Phản hồi từ AI
    val timestamp: Long // Thời gian gửi tin
)
