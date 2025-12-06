package com.hendra.benerbenerrealalp.data.container

import android.content.Context
import android.content.SharedPreferences

object TokenManager {
    private const val PREF_NAME = "MomentumAuth"
    private const val KEY_TOKEN = "jwt_token"

    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    // Simpan Token (Dipanggil saat Login/Register sukses)
    fun saveToken(context: Context, token: String) {
        getPrefs(context).edit().putString(KEY_TOKEN, token).apply()
    }

    // Ambil Token (Dipanggil oleh AppContainer untuk Header)
    fun getToken(context: Context): String? {
        return getPrefs(context).getString(KEY_TOKEN, null)
    }

    // Hapus Token (Dipanggil saat Logout)
    fun clearToken(context: Context) {
        getPrefs(context).edit().remove(KEY_TOKEN).apply()
    }
}