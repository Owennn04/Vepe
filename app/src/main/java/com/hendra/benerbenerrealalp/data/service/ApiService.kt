package com.hendra.benerbenerrealalp.data.service

import com.hendra.benerbenerrealalp.data.dto.*
import com.hendra.benerbenerrealalp.ui.model.AlarmRequest
import com.hendra.benerbenerrealalp.ui.model.AlarmResponse
import com.hendra.benerbenerrealalp.ui.model.LoginRequest
import com.hendra.benerbenerrealalp.ui.model.RegisterRequest
import com.hendra.benerbenerrealalp.ui.model.TodoRequest
import com.hendra.benerbenerrealalp.ui.model.TodoResponse
import com.hendra.benerbenerrealalp.ui.model.ToggleAlarmRequest
import com.hendra.benerbenerrealalp.ui.model.TransactionRequest
import com.hendra.benerbenerrealalp.ui.model.TransactionResponse
import com.hendra.benerbenerrealalp.ui.model.UserResponse
import retrofit2.Response
import retrofit2.http.*

interface
ApiService {
    // --- AUTH & FINANCE (Tetap sama) ---
    @POST("api/users/register")
    suspend fun register(@Body request: RegisterRequest): Response<WebResponse<UserResponse>>
    @POST("api/users/login")
    suspend fun login(@Body request: LoginRequest): Response<WebResponse<UserResponse>>
    @POST("api/finance")
    suspend fun createTransaction(@Body request: TransactionRequest): Response<WebResponse<TransactionResponse>>
    @GET("api/finance")
    suspend fun getTransactions(): Response<WebResponse<List<TransactionResponse>>>

    // --- TO-DO LIST (CRUD) ---
    @GET("api/todos")
    suspend fun getTodos(): Response<WebResponse<List<TodoResponse>>>

    @POST("api/todos")
    suspend fun createTodo(@Body request: TodoRequest): Response<WebResponse<TodoResponse>>

    @PATCH("api/todos/{id}/toggle")
    suspend fun toggleTodo(@Path("id") id: String): Response<WebResponse<TodoResponse>>

    @DELETE("api/todos/{id}")
    suspend fun deleteTodo(@Path("id") id: String): Response<WebResponse<Any>>

    // --- ALARM (PENGGANTI SLEEP) ---
    @GET("api/alarms")
    suspend fun getAlarms(): Response<WebResponse<List<AlarmResponse>>>

    @POST("api/alarms")
    suspend fun createAlarm(@Body request: AlarmRequest): Response<WebResponse<AlarmResponse>>

    @PATCH("api/alarms/{id}/toggle")
    suspend fun toggleAlarm(@Path("id") id: String, @Body request: ToggleAlarmRequest): Response<WebResponse<AlarmResponse>>

    @DELETE("api/alarms/{id}")
    suspend fun deleteAlarm(@Path("id") id: String): Response<WebResponse<Any>>
}
