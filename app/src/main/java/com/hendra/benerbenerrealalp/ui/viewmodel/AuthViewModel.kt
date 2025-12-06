package com.hendra.benerbenerrealalp.ui.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.hendra.benerbenerrealalp.MomentumApplication
import com.hendra.benerbenerrealalp.data.container.TokenManager
import com.hendra.benerbenerrealalp.ui.model.LoginRequest
import com.hendra.benerbenerrealalp.ui.model.RegisterRequest
import com.hendra.benerbenerrealalp.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository) : ViewModel() {

    // State UI (Loading, Success, Error, Idle)
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState = _uiState.asStateFlow()

    // --- FUNGSI LOGIN ---
    fun login(email: String, pass: String, context: Context) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repository.login(LoginRequest(email, pass)).fold(
                onSuccess = {
                    // Simpan token ke Memory HP (SharedPreferences)
                    TokenManager.saveToken(context, it.token ?: "")
                    _uiState.value = AuthUiState.Success
                },
                onFailure = {
                    _uiState.value = AuthUiState.Error(it.message ?: "Login Gagal")
                }
            )
        }
    }

    // --- FUNGSI REGISTER ---
    fun register(name: String, email: String, pass: String) {
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            repository.register(RegisterRequest(name, email, pass)).fold(
                onSuccess = {
                    _uiState.value = AuthUiState.Success
                },
                onFailure = {
                    _uiState.value = AuthUiState.Error(it.message ?: "Register Gagal")
                }
            )
        }
    }

    // Reset state agar tidak stuck di Success/Error saat pindah layar
    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }

    // --- FACTORY (DEPENDENCY INJECTION) ---
    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // Mengambil repository dari MomentumApplication (Global Container)
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MomentumApplication)
                AuthViewModel(app.container.authRepository)
            }
        }
    }
}

sealed interface AuthUiState {
    object Idle : AuthUiState       // Diam (Belum ngapa-ngapain)
    object Loading : AuthUiState    // Sedang memuat (Tampilkan loading spinner)
    object Success : AuthUiState    // Berhasil Login/Register
    data class Error(val msg: String) : AuthUiState // Gagal (Tampilkan pesan error)
}