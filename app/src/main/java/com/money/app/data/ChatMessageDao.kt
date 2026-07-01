package com.money.app.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

/**
 * Interface cung cấp các phương thức truy vấn cho lịch sử Chat.
 */
@Dao
interface ChatMessageDao {
    // Lấy toàn bộ lịch sử tin nhắn sắp xếp theo thời gian
    @Query("SELECT * FROM chat_messages ORDER BY timestamp ASC")
    suspend fun getAllMessages(): List<ChatMessage>

    // Lưu một tin nhắn mới vào lịch sử
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(message: ChatMessage)

    // Xóa sạch toàn bộ lịch sử trò chuyện
    @Query("DELETE FROM chat_messages")
    suspend fun clearHistory()
}
