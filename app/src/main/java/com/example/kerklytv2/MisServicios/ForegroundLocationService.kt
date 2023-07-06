package com.example.kerklytv2.MisServicios

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.kerklytv2.R

class ForegroundLocationService : Service() {
    private val notificationId = 1
    private val channelId = "ForegroundLocationServiceChannel"

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // ...
        startForeground(notificationId, createNotification())
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    private fun createNotification(): Notification {
        // Crear un canal de notificación para Android 8.0 y versiones superiores
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Foreground Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Construir y retornar la notificación
        return NotificationCompat.Builder(this, channelId)
            .setContentTitle("Foreground Location Service")
            .setContentText("El servicio de ubicación está en ejecución")
            .setSmallIcon(R.drawable.archivos)
            .build()
    }
}