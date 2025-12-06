package com.hendra.benerbenerrealalp.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.hendra.benerbenerrealalp.R
import com.hendra.benerbenerrealalp.ui.view.QuestActivity

class AlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        // 1. Nyalakan Service Suara (Biar Berisik)
        val serviceIntent = Intent(context, AlarmSoundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }

        // 2. Tampilkan Layar Quest (Full Screen)
        showAlarmNotification(context)
    }

    private fun showAlarmNotification(context: Context) {
        val channelId = "ALARM_URGENT"
        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Alarm Bangun", NotificationManager.IMPORTANCE_HIGH)
            channel.setSound(null, null) // Suara dihandle service
            manager.createNotificationChannel(channel)
        }

        // Intent untuk membuka QuestActivity (Layar Matematika)
        val fullScreenIntent = Intent(context, QuestActivity::class.java).apply {
            // --- PERBAIKAN DI SINI ---
            // Gunakan 'flags =' bukan 'Intent.setFlags ='
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            0,
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Pastikan icon ini ada
            .setContentTitle("WAKTUNYA BANGUN!")
            .setContentText("Selesaikan soal matematika.")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setFullScreenIntent(fullScreenPendingIntent, true) // KUNCI AGAR MUNCUL DI LOCKSCREEN
            .setAutoCancel(false)
            .setOngoing(true)
            .build()

        manager.notify(999, notification)
    }
}