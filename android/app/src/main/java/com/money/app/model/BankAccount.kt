package com.money.app.model

data class BankAccount(
    val id: String,
    val bankId: String,
    val bankName: String,
    val accountNumber: String,
    var balance: Long,
    val lastUpdated: Long = System.currentTimeMillis(),
    var qrImagePath: String? = null // Đường dẫn ảnh mã QR đã lưu trong bộ nhớ máy
)
