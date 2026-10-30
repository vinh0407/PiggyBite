package com.piggybite.app.model

enum class NotificationCategory {
    INCOME,        // Nhận tiền (Tiền vào)
    EXPENSE,       // Chuyển tiền / Thanh toán (Tiền ra)
    PROMO_OR_ADS   // Quảng cáo, tin rác, khuyến mãi, ưu đãi
}

data class ReceivedNotification(
    val id: String = java.util.UUID.randomUUID().toString(),
    val bankName: String,
    val bankId: String,
    val category: NotificationCategory,
    val amount: Long = 0L,
    val rawText: String,
    val summary: String,
    val timestamp: Long = System.currentTimeMillis()
)
