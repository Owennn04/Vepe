package com.hendra.benerbenerrealalp.data.repository

import com.hendra.benerbenerrealalp.data.dto.*
import com.hendra.benerbenerrealalp.data.service.ApiService
import com.hendra.benerbenerrealalp.ui.model.AlarmRequest
import com.hendra.benerbenerrealalp.ui.model.ToggleAlarmRequest

class SleepRepository(private val api: ApiService) {

    suspend fun getAlarms() = safeCall { api.getAlarms() }

    suspend fun createAlarm(time: String, label: String, days: List<Boolean>) = safeCall {
        api.createAlarm(AlarmRequest(time, label, days))
    }

    suspend fun toggleAlarm(id: String, isActive: Boolean) = safeCall {
        api.toggleAlarm(id, ToggleAlarmRequest(isActive))
    }

    suspend fun deleteAlarm(id: String) = safeCall { api.deleteAlarm(id) }

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