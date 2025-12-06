package com.hendra.benerbenerrealalp.data.container

import android.content.Context
import com.hendra.benerbenerrealalp.data.repository.AuthRepository
import com.hendra.benerbenerrealalp.data.repository.FinanceRepository
import com.hendra.benerbenerrealalp.data.repository.SleepRepository
import com.hendra.benerbenerrealalp.data.repository.TodoRepository
import com.hendra.benerbenerrealalp.data.service.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Ubah menjadi class biasa (bukan object) agar bisa menerima Context
class AppContainer(private val context: Context) {

    // IP Emulator Android (localhost komputer)
    private val BASE_URL = "http://10.0.2.2:3000/"

    private val client = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()

            // AMBIL TOKEN DARI SHARED PREFERENCES (Memori HP)
            TokenManager.getToken(context)?.let { token ->
                request.addHeader("Authorization", token)
            }

            chain.proceed(request.build())
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService by lazy { retrofit.create(ApiService::class.java) }

    val authRepository by lazy { AuthRepository(apiService) }
    val financeRepository by lazy { FinanceRepository(apiService) }
    val todoRepository by lazy { TodoRepository(apiService) }
    val sleepRepository by lazy { SleepRepository(apiService) }
}