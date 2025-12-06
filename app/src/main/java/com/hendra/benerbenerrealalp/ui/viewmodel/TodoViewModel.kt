package com.hendra.benerbenerrealalp.ui.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.hendra.benerbenerrealalp.MomentumApplication
import com.hendra.benerbenerrealalp.ui.model.TodoRequest
import com.hendra.benerbenerrealalp.ui.model.TodoResponse
import com.hendra.benerbenerrealalp.data.repository.TodoRepository
import com.hendra.benerbenerrealalp.service.TodoAlarmReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar


class TodoViewModel(private val repository: TodoRepository) : ViewModel() {

    private val _todos = MutableStateFlow<List<TodoResponse>>(emptyList())
    val todos = _todos.asStateFlow()

    // LOAD DATA SAAT VIEW MODEL DIBUAT
    init {
        loadTodos()
    }

    fun loadTodos() {
        viewModelScope.launch {
            repository.getTodos().onSuccess { _todos.value = it }
        }
    }

    fun addTodo(context: Context, title: String, timeString: String) {
        viewModelScope.launch {
            val req = TodoRequest(title, timeString, isReminder = true)
            // Simpan ke DB
            repository.createTodo(req).onSuccess {
                loadTodos() // Reload data dari DB
                scheduleTodoAlarm(context, title, timeString) // Set Alarm
            }
        }
    }

    fun toggleTodo(id: String) {
        viewModelScope.launch {
            repository.toggleTodo(id).onSuccess { loadTodos() }
        }
    }

    fun deleteTodo(id: String) {
        viewModelScope.launch {
            repository.deleteTodo(id).onSuccess { loadTodos() }
        }
    }

    // Logic Alarm Todo (Sama seperti sebelumnya)
    private fun scheduleTodoAlarm(context: Context, title: String, timeString: String) {
        try {
            val parts = timeString.split(":")
            val hour = parts[0].toInt()
            val minute = parts[1].toInt()

            val calendar = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }
            if (calendar.timeInMillis <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1)
            }

            val intent = Intent(context, TodoAlarmReceiver::class.java).apply {
                putExtra("TODO_TITLE", title)
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                title.hashCode(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
                // Handle permission
                return
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    calendar.timeInMillis,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
            Toast.makeText(context, "Pengingat diset!", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                // CARA BARU: Ambil dari MomentumApplication
                val app =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MomentumApplication)
                TodoViewModel(app.container.todoRepository)
            }
        }
    }
}