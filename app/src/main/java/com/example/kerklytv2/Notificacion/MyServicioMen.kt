package com.example.kerklytv2.Notificacion

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.kerklytv2.PantallaInicio
import com.example.kerklytv2.vista.InterfazKerkly


class MyServicioMen: Service() {
    private val TAG: String = MyServicioMen::class.java.getSimpleName()

    fun MyServicioMen() {}

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }


    override fun onCreate() {
        Log.d(TAG, "Servicio creado...")
    }

    override fun onStartCommand(intent: Intent?, flags1: Int, startId: Int): Int {
        Log.d(TAG, "Servicio iniciado...")

        val localIntent = Intent(Constants.ACTION_RUN_SERVICE)
        // Emitir el intent a la actividad
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(localIntent)
        val intent = Intent(applicationContext, PantallaInicio::class.java).apply {
            //usamos el siguiente codigo para que no se creen varias aplicaciones
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Quitar de primer plano
          // stopService(localIntent)
        }
        val flags = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent : PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, flags)

        try {
            var noti = NotificationCompat.Builder(applicationContext, InterfazKerkly.chanel_id)
                .setSmallIcon(android.R.drawable.ic_delete)
                .setContentTitle(InterfazKerkly.titulo)
                .setContentText(InterfazKerkly.mensaje)
                //hacer una notificacion expandible
                .setStyle(
                    NotificationCompat.BigTextStyle()
                    .bigText(InterfazKerkly.mensaje))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
            Thread.sleep(20000)


            val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.notify(InterfazKerkly.ID_NOTIFICACION, noti)
            println("servicio en segundo planooo")

            // Quitar de primer plano
            //stopForeground(true)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return START_NOT_STICKY
    }

    override fun onDestroy() {

        val localIntent = Intent(Constants.ACTION_MEMORY_EXIT)

        // Emitir el intent a la actividad
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(localIntent)
        Log.d(TAG, "Servicio destruido...")
    }

}