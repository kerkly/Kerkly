package com.example.kerklytv2.Notificacion

import android.app.IntentService
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.kerklytv2.PantallaInicio
import com.example.kerklytv2.vista.InterfazKerkly


// TODO: Rename actions, choose action names that describe tasks that this
// IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS


/**
 * An [IntentService] subclass for handling asynchronous task requests in
 * a service on a separate handler thread.

 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.

 */
class MyIntentServiceMensajes : IntentService("MyIntentServiceMensajes") {

    override fun onHandleIntent(intent: Intent?) {
        if (intent != null){
            val action = intent.action
            if (Constants.ACTION_RUN_ISERVICE.equals(action)){
                handleActionRun()
            }
        }
    }

    private fun handleActionRun() {
        val intent = Intent(applicationContext, PantallaInicio::class.java).apply {
            //usamos el siguiente codigo para que no se creen varias aplicaciones
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Quitar de primer plano
        }
        val flags = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val pendingIntent : PendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, flags)

        try {
            var noti = NotificationCompat.Builder(applicationContext, InterfazKerkly.chanel_id)
                .setSmallIcon(android.R.drawable.ic_delete)
                .setContentTitle(InterfazKerkly.titulo)
                .setContentText(InterfazKerkly.mensaje)
                //hacer una notificacion expandible
                .setStyle(NotificationCompat.BigTextStyle()
                    .bigText(InterfazKerkly.mensaje))
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build()
            //Thread.sleep(10000)
              startForeground(InterfazKerkly.ID_NOTIFICACION, noti)
            //startForeground(InterfazKerkly.ID_NOTIFICACION, noti);
           // val manager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            //manager.notify(InterfazKerkly.ID_NOTIFICACION, noti)
            println("servicio en segundo planooo")

            // Quitar de primer plano
           //stopForeground(true)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
    }

   /* override fun onDestroy() {
        Toast.makeText(this, "Servicio destruido...", Toast.LENGTH_SHORT).show()

        // Emisión para avisar que se terminó el servicio
        val localIntent = Intent(Constants.ACTION_PROGRESS_EXIT)
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent)
    }*/

}