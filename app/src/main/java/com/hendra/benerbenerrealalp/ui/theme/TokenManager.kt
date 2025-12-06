package com.hendra.mynewalp.data.container

object okenManager {
    private var token: String? = null

    fun saveToken(newToken: String) { token = newToken }
    fun getToken(): String? = token
    fun clearToken() { token = null }
}