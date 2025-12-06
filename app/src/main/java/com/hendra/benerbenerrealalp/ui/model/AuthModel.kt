package com.hendra.benerbenerrealalp.ui.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)

data class LoginRequest(
    val email: String,
    val password: String
)

data class UserResponse(
    val id: String,
    val name: String,
    val email: String,
    val token: String? = null // Token ada saat login
)