package com.hendra.benerbenerrealalp.data.repository

import com.hendra.benerbenerrealalp.data.dto.*
import com.hendra.benerbenerrealalp.data.service.ApiService
import com.hendra.benerbenerrealalp.ui.model.TodoRequest

class TodoRepository(private val api: ApiService) {

    suspend fun getTodos() = safeCall { api.getTodos() }
    suspend fun createTodo(req: TodoRequest) = safeCall { api.createTodo(req) }
    suspend fun toggleTodo(id: String) = safeCall { api.toggleTodo(id) }
    suspend fun deleteTodo(id: String) = safeCall { api.deleteTodo(id) }

    // Copy fungsi safeCall dari repository sebelumnya (atau buat BaseRepository)
    private suspend fun <T> safeCall(call: suspend () -> retrofit2.Response<WebResponse<T>>): Result<T> {
        return try {
            val response = call()
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.data)
            } else {
                Result.failure(Exception("Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}