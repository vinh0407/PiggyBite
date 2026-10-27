package com.money.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Lớp cơ sở dữ liệu Room chính của ứng dụng VINORA Financial Operating System.
 * Quản lý toàn bộ 10 Entities lõi tài chính.
 */
@Database(
    entities = [
        Transaction::class,
        Fund::class,
        ChatMessage::class,
        Account::class,
        Budget::class,
        Bill::class,
        Subscription::class,
        Debt::class,
        Investment::class,
        SyncQueueItem::class
    ],
    version = 16,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun transactionDao(): TransactionDao
    abstract fun fundDao(): FundDao
    abstract fun chatMessageDao(): ChatMessageDao
    abstract fun accountDao(): AccountDao
    abstract fun budgetDao(): BudgetDao
    abstract fun debtDao(): DebtDao
    abstract fun investmentDao(): InvestmentDao
    abstract fun syncQueueDao(): SyncQueueDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "money_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
