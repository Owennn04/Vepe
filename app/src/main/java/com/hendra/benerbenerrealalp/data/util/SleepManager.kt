package com.hendra.benerbenerrealalp.data.util

import android.content.Context
import android.content.SharedPreferences

object SleepManager {
    private const val PREF_NAME = "MomentumSleep"
    private const val KEY_START_TIME = "sleep_start_time"
    private const val KEY_IS_SLEEPING = "is_sleeping"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Simpan waktu mulai tidur (dalam milisecond)
    fun saveSleepStart(context: Context, timeInMillis: Long) {
        getPrefs(context).edit()
            .putLong(KEY_START_TIME, timeInMillis)
            .putBoolean(KEY_IS_SLEEPING, true)
            .apply()
    }

    // Ambil waktu mulai tidur
    fun getSleepStart(context: Context): Long {
        return getPrefs(context).getLong(KEY_START_TIME, 0L)
    }

    // Cek apakah sedang mode tidur
    fun isSleeping(context: Context): Boolean {
        return getPrefs(context).getBoolean(KEY_IS_SLEEPING, false)
    }

    // Hapus data saat bangun
    fun clearSleepSession(context: Context) {
        getPrefs(context).edit()
            .remove(KEY_START_TIME)
            .putBoolean(KEY_IS_SLEEPING, false)
            .apply()
    }
}