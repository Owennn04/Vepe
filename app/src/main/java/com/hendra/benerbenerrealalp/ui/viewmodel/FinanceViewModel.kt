package com.hendra.benerbenerrealalp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.hendra.benerbenerrealalp.MomentumApplication
import com.hendra.benerbenerrealalp.ui.model.TransactionRequest
import com.hendra.benerbenerrealalp.ui.model.TransactionResponse
import com.hendra.benerbenerrealalp.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class FinanceViewModel(private val repository: FinanceRepository) : ViewModel() {

    private val _transactions = MutableStateFlow<List<TransactionResponse>>(emptyList())
    val transactions = _transactions.asStateFlow()

    fun loadData() {
        viewModelScope.launch {
            repository.getTransactions().onSuccess {
                _transactions.value = it
                }
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MomentumApplication)
                FinanceViewModel(app.container.financeRepository)
            }
        }
    }

    fun createTransaction(type: String, amount: Double, category: String) {
        viewModelScope.launch {
            val req = TransactionRequest(type, amount, category, Instant.now().toString())
            repository.createTransaction(req).onSuccess {
                loadData() // Reload list setelah simpan
            }
        }
    }

}