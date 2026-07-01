package com.money.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Lớp cơ sở dữ liệu Room chính của ứng dụng.
 * Quản lý các bảng (Entities): Giao dịch (Transaction), Quỹ (Fund), và Tin nhắn Chat (ChatMessage).
 * Sử dụng TypeConverters để chuyển đổi các kiểu dữ liệu phức tạp (như Map) sang dạng Room có thể lưu trữ.
 */
@Database(entities = [Transaction::class, Fund::class, ChatMessage::class], version = 15, exportSchema = false)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    // Khai báo các Data Access Objects (DAOs) để tương tác với từng bảng
    abstract fun transactionDao(): TransactionDao
    abstract fun fundDao(): FundDao
    abstract fun chatMessageDao(): ChatMessageDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Singleton pattern: Đảm bảo chỉ có một instance của Database duy nhất trong toàn bộ vòng đời ứng dụng.
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "money_database"
                )
                // .fallbackToDestructiveMigration(): 
                // Cảnh báo: Phương thức này sẽ xóa sạch dữ liệu cũ nếu bạn tăng số 'version' của Database 
                // mà không viết code chuyển đổi (Migration). Thích hợp trong quá trình phát triển (Dev).
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
