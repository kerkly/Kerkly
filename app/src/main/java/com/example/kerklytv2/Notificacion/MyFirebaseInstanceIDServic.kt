package com.example.kerklytv2.Notificacion

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.kerklytv2.MainActivityChats
import com.example.kerklytv2.MapsActivity
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
            val TipoNoti:String = message.getData().get("tipoNoti")!!
            //println("tipo noti $TipoNoti")
            if (TipoNoti == "llamarTopicEnviarSolicitudUrgente"){
                val latitud = message.getData().get("latitud").toString()
                val longitud = message.getData().get("longitud").toString()
                val folio = message.getData().get("Folio").toString()
                val nombreCliente = message.getData().get("nombreCompletoCliente").toString()
                val direccion = message.getData().get("direccion").toString()
                val problema = message.getData().get("problema").toString()
                val telefonoCliente = message.getData().get("telefonoCliente").toString()
                val TipoServicio  = message.getData().get("tipoServicio").toString()
                val Curp = message.getData().get("Curp").toString()
                val telefonoKerkly = message.getData().get("telefonok").toString()
                val correoCliente = message.getData().get("correoCliente").toString()
                val correoKerkrly = message.getData().get("correoKerly").toString()
                val nombrekerkly = message.getData().get("nombreCompletoKerkly").toString()
               // val direccionKerly = message.getData().get("direccionkerkly").toString()
                val  uidCliente = message.getData().get("uidCliente").toString()
                crearNotiSolicitud(titulo, detalle,latitud,longitud,folio,nombreCliente,direccion,problema,telefonoCliente,TipoServicio
                    ,Curp,telefonoKerkly,correoCliente,correoKerkrly,nombrekerkly,uidCliente)
            }
            if (TipoNoti == "chats"){
                val nombreCliente: String = message.getData().get("nombreCompletoCliente")!!
                val nombreKerkly: String = message.getData().get("nombreCompletoKerkly")!!
                val telefonoKerkly: String = message.getData().get("telefonoKerkly")!!
                val telefonoCliente: String = message.getData().get("telefonoCliente")!!
                val fotoCliente: String = message.getData().get("urlFotoCliente")!!
                val tokenCliente: String = message.getData().get("tokenCliente")!!
                val uidCliente: String = message.getData().get("uidCliente")!!
                val uidKerkly: String = message.getData().get("uidKerkly")!!
                CrearNotificacion(titulo, detalle, nombreCliente,nombreKerkly,telefonoKerkly,telefonoCliente
                    ,fotoCliente,tokenCliente,uidCliente,uidKerkly)
            }

            if (TipoNoti == "llamarTopicEnviarSolicitudNormal"){
                val latitud = message.getData().get("latitud").toString()
                val longitud = message.getData().get("longitud").toString()
                val folio = message.getData().get("Folio").toString()
                val nombreCliente = message.getData().get("nombreCompletoCliente").toString()
                val direccion = message.getData().get("direccion").toString()
                val problema = message.getData().get("problema").toString()
                val telefonoCliente = message.getData().get("telefonoCliente").toString()
                val TipoServicio  = message.getData().get("tipoServicio").toString()
                val Curp = message.getData().get("Curp").toString()
                val telefonoKerkly = message.getData().get("telefonok").toString()
                val correoCliente = message.getData().get("correoCliente").toString()
                val  correoKerkrly = message.getData().get("correoKerly").toString()
                val nombrekerkly = message.getData().get("nombreCompletoKerkly").toString()
                // val direccionKerly = message.getData().get("direccionkerkly").toString()

                println("notificaion ------> $latitud, $longitud")
                val  uidCliente = message.getData().get("uidCliente").toString()
                crearNotiSolicitud(titulo, detalle,latitud,longitud,folio,nombreCliente,direccion,problema,telefonoCliente,TipoServicio
                    ,Curp,telefonoKerkly,correoCliente,correoKerkrly,nombrekerkly,uidCliente)
            }

            if (TipoNoti =="PresupuestoAceptado"){

            }
        }
    }

    private fun crearNotiSolicitud(titulo:String, detalle:String, latitud:String, longitud:String,
                                   folio:String, nombreCliente:String, direccion:String, problema:String
                                   , telefonoCliente:String, TipoServicio:String, telefonoKerkly: String
                                   , Curp:String, correoCliente:String, correoKerkrly:String
                                   , nombrekerkly:String, uidCliente:String) {
        val id = "solicitud$folio"
        val id2 = id.hashCode()
        val nm = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val builder = NotificationCompat.Builder(this, id)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nc = NotificationChannel(id, "nuevaSolicitud$folio", NotificationManager.IMPORTANCE_HIGH)
            nc.setShowBadge(true)
            assert(nm != null)
            nm!!.createNotificationChannel(nc)
        }
        try {
            builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setSmallIcon(R.drawable.archivos)
                .setContentText(detalle)
                .setContentIntent(clicknotiSolicitudUrgenteYNormal(latitud,longitud,folio,nombreCliente,direccion,problema,telefonoCliente,TipoServicio
                    ,Curp,telefonoKerkly,correoCliente,correoKerkrly,nombrekerkly,uidCliente))
                .setContentInfo("nuevo$folio")
               val random = Random()
            val idNotity = random.nextInt(1000)
            assert(nm != null)
            nm!!.notify(idNotity, builder.build())
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }



    private fun CrearNotificacion(titulo: String, detalle: String, nombreCliente: String,
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

        println("foto Cliente $fotoCliente")


        // nf.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        nf.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val uniqueId = uidCliente.hashCode() // Puedes cambiar esto según tus necesidades
        return PendingIntent.getActivity(applicationContext, uniqueId, nf, flags)

    }

    private fun clicknotiSolicitudUrgenteYNormal(latitud:String,longitud:String,
                                          folio:String,nombreCliente:String,direccion:String,problema:String
                                          ,telefonoCliente:String,TipoServicio:String,telefonoKerkly: String
                                          ,Curp:String,correoCliente:String,correoKerkrly:String
                                          ,nombrekerkly:String,uidCliente:String): PendingIntent? {
        val nf = Intent(applicationContext, MapsActivity::class.java)
        // Agrega otros datos que desees pasar a la actividad de chat
        nf.putExtra("latitud",latitud)
        nf.putExtra("longitud",longitud)
        nf.putExtra("Folio",folio)
        nf.putExtra("nombreCompletoCliente",nombreCliente)
        nf.putExtra("direccion",direccion)
        nf.putExtra("problema",problema)
        nf.putExtra("telefonoCliente",telefonoCliente)
        nf.putExtra("tipoServicio",TipoServicio)
        nf.putExtra("Curp",Curp)
        nf.putExtra("telefonok",telefonoKerkly)
        nf.putExtra("correoCliente",correoCliente)
        nf.putExtra("correoKerly",correoKerkrly)
        nf.putExtra("nombreCompletoKerkly",nombrekerkly)
        // val direccionKerly = message.getData().get("direccionkerkly").toString()
        nf.putExtra("uidCliente",uidCliente)
        nf.putExtra("Noti", "Noti")

        println("noti folio1 --------> $folio")
        println("notificaion ------> $latitud, $longitud")

       // nf.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        nf.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK

        val flags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE else 0
        val uniqueId = folio.hashCode() // Puedes cambiar esto según tus necesidades
        return PendingIntent.getActivity(applicationContext, uniqueId, nf, flags)

      //  return PendingIntent.getActivity(applicationContext, 0, nf, flags)
    }

    override fun onDestroy() {
        super.onDestroy()
        onMessageReceived(mensajeRemoto)
    }

}