package com.piggybite.app.model

data class Transaction(
    val id: String,
    val amount: Long,
    val isIncome: Boolean,
    val bankAccountId: String,
    val bankName: String,
    val note: String,
    val timestamp: Long = System.currentTimeMillis(),
    val category: String = if (isIncome) "Thu nhập" else "Chi tiêu"
)
