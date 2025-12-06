package com.hendra.benerbenerrealalp.data.repository

import com.hendra.benerbenerrealalp.data.dto.*
import com.hendra.benerbenerrealalp.data.service.ApiService
import com.hendra.benerbenerrealalp.ui.model.LoginRequest
import com.hendra.benerbenerrealalp.ui.model.RegisterRequest
import com.hendra.benerbenerrealalp.ui.model.UserResponse
import retrofit2.Response

class AuthRepository(private val api: ApiService) {

    suspend fun register(req: RegisterRequest): Result<UserResponse> {
        return safeCall { api.register(req) }
    }

    suspend fun login(req: LoginRequest): Result<UserResponse> {
        return safeCall { api.login(req) }
    }

    // Helper function untuk handle error
    private suspend fun <T> safeCall(call: suspend () -> Response<WebResponse<T>>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception(response.errorBody()?.string() ?: "Error"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}