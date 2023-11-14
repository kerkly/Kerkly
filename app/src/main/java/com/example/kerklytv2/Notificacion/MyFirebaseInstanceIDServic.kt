package com.example.kerklytv2.Notificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.kerklytv2.MainActivityChats
import com.example.kerklytv2.PantallaInicio
import com.example.kerklytv2.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.io.IOException
import java.util.*

class MyFirebaseInstanceIDServic : FirebaseMessagingService() {
    lateinit var token1: String
    lateinit var mensajeRemoto: RemoteMessage
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        token1 = token
        Log.e("token", "mi token es:$token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val from: String = message.getFrom()!!
        mensajeRemoto = message

        if (message.getData().size > 0) {
            val titulo: String = message.getData().get("titulo")!!
            val detalle: String = message.getData().get("detalle")!!
            val nombreCliente: String = message.getData().get("nombreCompletoCliente")!!
            val nombreKerkly: String = message.getData().get("nombreCompletoKerkly")!!
            val telefonoKerkly: String = message.getData().get("telefonoKerkly")!!
            val telefonoCliente: String = message.getData().get("telefonoCliente")!!
            val fotoCliente: String = message.getData().get("urlFotoCliente")!!
            val tokenCliente: String = message.getData().get("tokenCliente")!!
            val uidCliente: String = message.getData().get("uidCliente")!!
            val uidKerkly: String = message.getData().get("uidKerkly")!!
            CraerNotificacion(titulo, detalle, nombreCliente,nombreKerkly,telefonoKerkly,telefonoCliente
                ,fotoCliente,tokenCliente,uidCliente,uidKerkly)
        }
    }

    private fun CraerNotificacion(titulo: String, detalle: String, nombreCliente: String,
                                  nombreKerkly: String,telefonoKerkly:String,telefonoCliente:String, fotoCliente:String, tokenCliente:String,
                                  uidCliente: String, uidKerkly:String) {
        val id = "mensajeKerkly"
        val id2 = id.hashCode()
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nc = NotificationChannel(id, "nuevoKerkly", NotificationManager.IMPORTANCE_HIGH)
            nc.setShowBadge(true)
            assert(nm != null)
            nm!!.createNotificationChannel(nc)
        }
        try {
            //Bitmap imf_foto= Picasso.get(getApplicationContext()).load(foto).get();
            //   Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.iconoperito).into(img);
            //val imf_foto = Picasso.get().load(foto).get()
         /*   val icono = applicationContext.resources.getIdentifier(icono,
            "drawable",applicationContext.packageName)*/

            builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setSmallIcon(R.drawable.archivos)
                .setContentText(detalle)
                .setContentIntent(clicknoti(nombreCliente,nombreKerkly,
                telefonoKerkly,telefonoCliente,fotoCliente,tokenCliente,uidCliente,uidKerkly))
                .setContentInfo("nuevo")
         //   val random = Random()
            //val idNotity = random.nextInt(1000)
            assert(nm != null)
            nm!!.notify(id2, builder.build())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun clicknoti(nombreCliente: String,
                          nombreKerkly: String,telefonoKerkly:String,telefonoCliente:String, fotoCliente:String, tokenCliente:String,
                          uidCliente: String, uidKerkly:String): PendingIntent? {
        //val flags = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val nf = Intent(applicationContext, MainActivityChats::class.java)
        // Agrega otros datos que desees pasar a la actividad de chat

        nf.putExtra("nombreCompletoCliente", nombreCliente)
        nf.putExtra("nombreCompletoKerkly", nombreKerkly)
        nf.putExtra("telefonoKerkly", telefonoKerkly)
        nf.putExtra("telefonoCliente", telefonoCliente)
        nf.putExtra("urlFotoCliente", fotoCliente)
        nf.putExtra("tokenCliente", tokenCliente)
        nf.putExtra("uidCliente", uidCliente)
        nf.putExtra("uidKerkly", uidKerkly)
        nf.putExtra("Noti", "Noti")
        println("aqui ------> $uidCliente ke $uidKerkly telefono $telefonoCliente")
        nf.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0

        return PendingIntent.getActivity(applicationContext, 0, nf, flags)

    }

    override fun onDestroy() {
        super.onDestroy()
        onMessageReceived(mensajeRemoto)
    }

}