package com.hendra.benerbenerrealalp.data.dto

// Format standar response API: { "data": ..., "message": ... }
data class WebResponse<T>(
    val data: T,
    val message: String? = null,
    val errors: Any? = null // Untuk menangkap error validasi Zod
)