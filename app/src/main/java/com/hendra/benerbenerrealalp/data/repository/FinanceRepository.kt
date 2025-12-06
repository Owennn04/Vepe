package com.hendra.benerbenerrealalp.data.repository

import com.hendra.benerbenerrealalp.data.dto.*
import com.hendra.benerbenerrealalp.data.service.ApiService
import com.hendra.benerbenerrealalp.ui.model.TransactionRequest

class FinanceRepository(private val api: ApiService) {
    // Logic sama seperti AuthRepository, disederhanakan:
    suspend fun createTransaction(req: TransactionRequest) = safeCall { api.createTransaction(req) }
    suspend fun getTransactions() = safeCall { api.getTransactions() }

    private suspend fun <T> safeCall(call: suspend () -> retrofit2.Response<WebResponse<T>>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}