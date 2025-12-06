package com.hendra.benerbenerrealalp.ui.model

data class TransactionRequest(
    val type: String, // "INCOME" atau "EXPENSE"
    val amount: Double,
    val category: String,
    val date: String // ISO String date
)

data class TransactionResponse(
    val id: String,
    val type: String,
    val amount: Double,
    val category: String,
    val date: String,
    val userId: String
)