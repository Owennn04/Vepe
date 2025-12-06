package com.hendra.benerbenerrealalp

import android.app.Application
import com.hendra.benerbenerrealalp.data.container.AppContainer

class MomentumApplication : Application() {
    // Container ini akan bisa diakses dari mana saja di aplikasi
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        // Inisialisasi container dengan Context aplikasi
        container = AppContainer(this)
    }
}