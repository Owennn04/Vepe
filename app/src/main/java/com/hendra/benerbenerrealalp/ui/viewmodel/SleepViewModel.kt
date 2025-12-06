package com.hendra.benerbenerrealalp.ui.viewmodel

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log // PENTING: Tambahkan Import Log
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.lifecycle.viewModelScope
import com.hendra.benerbenerrealalp.MomentumApplication
import com.hendra.benerbenerrealalp.ui.model.AlarmResponse
import com.hendra.benerbenerrealalp.data.repository.SleepRepository
import com.hendra.benerbenerrealalp.service.AlarmReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

class SleepViewModel(private val repository: SleepRepository) : ViewModel() {

    private val _alarms = MutableStateFlow<List<AlarmResponse>>(emptyList())
    val alarms = _alarms.asStateFlow()

    // --- 1. LOAD DATA DENGAN LOGGING ---
    fun loadData() {
        viewModelScope.launch {
            repository.getAlarms().fold(
                onSuccess = { list ->
                    Log.d("SleepViewModel", "Berhasil Load: ${list.size} alarm")
                    _alarms.value = list
                },
                onFailure = { error ->
                    Log.e("SleepViewModel", "GAGAL Load Data: ${error.message}")
                    // Jika error parsing, biasanya karena format JSON beda
                }
            )
        }
    }

    // --- 2. TAMBAH ALARM ---
    fun addAlarm(context: Context, hour: Int, minute: Int, label: String, days: List<Boolean>) {
        val timeString = String.format("%02d:%02d", hour, minute)

        viewModelScope.launch {
            repository.createAlarm(timeString, label, days).fold(
                onSuccess = { newAlarm ->
                    Log.d("SleepViewModel", "Sukses Simpan Alarm: ${newAlarm.id}")
                    loadData() // Refresh UI otomatis
                    scheduleSystemAlarm(context, hour, minute, newAlarm.id.hashCode())
                    Toast.makeText(context, "Alarm tersimpan!", Toast.LENGTH_SHORT).show()
                },
                onFailure = { error ->
                    Log.e("SleepViewModel", "GAGAL Simpan Alarm: ${error.message}")
                    Toast.makeText(context, "Gagal simpan: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // --- 3. TOGGLE (ON/OFF) ---
    fun toggleAlarm(context: Context, id: String, isActive: Boolean) {
        viewModelScope.launch {
            repository.toggleAlarm(id, isActive).fold(
                onSuccess = { updatedAlarm ->
                    loadData() // Refresh UI

                    val parts = updatedAlarm.time.split(":")
                    val h = parts[0].toInt()
                    val m = parts[1].toInt()

                    if (isActive) {
                        scheduleSystemAlarm(context, h, m, updatedAlarm.id.hashCode())
                        Toast.makeText(context, "Alarm Nyala", Toast.LENGTH_SHORT).show()
                    } else {
                        cancelSystemAlarm(context, updatedAlarm.id.hashCode())
                        Toast.makeText(context, "Alarm Mati", Toast.LENGTH_SHORT).show()
                    }
                },
                onFailure = {
                    Log.e("SleepViewModel", "Gagal Toggle: ${it.message}")
                }
            )
        }
    }

    // --- 4. HAPUS ---
    fun deleteAlarm(context: Context, id: String) {
        viewModelScope.launch {
            repository.deleteAlarm(id).fold(
                onSuccess = {
                    cancelSystemAlarm(context, id.hashCode())
                    loadData()
                },
                onFailure = {
                    Log.e("SleepViewModel", "Gagal Hapus: ${it.message}")
                }
            )
        }
    }

    // --- LOGIKA ALARM MANAGER ---
    private fun scheduleSystemAlarm(context: Context, hour: Int, minute: Int, requestCode: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                return
            }
        }

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        if (calendar.timeInMillis <= System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1)
        }

        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode, // ID Unik
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            }
            Log.d("SleepViewModel", "Alarm Sistem Dijadwalkan: $hour:$minute")
        } catch (e: Exception) {
            Log.e("SleepViewModel", "Gagal Jadwal Alarm: ${e.message}")
        }
    }

    private fun cancelSystemAlarm(context: Context, requestCode: Int) {
        try {
            val intent = Intent(context, AlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            val am = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            am.cancel(pendingIntent)
            Log.d("SleepViewModel", "Alarm Sistem Dibatalkan")
        } catch (e: Exception) {
            Log.e("SleepViewModel", "Gagal Batal Alarm: ${e.message}")
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as MomentumApplication)
                SleepViewModel(app.container.sleepRepository)
            }
        }
    }
}