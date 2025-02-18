package com.example.kerklytv2.Notificacion

import android.content.Context
import android.widget.Toast
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.messaging.FirebaseMessaging
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

class llamartopico {

     fun llamartopico(context: Context, token: String, mensaje: String, titulo: String) {
        val myrequest = Volley.newRequestQueue(context)
        val json = JSONObject()
        try {
            // val token = "clRv1kYVRaGMDXONlKGJCU:APA91bGIcMSjD7SQ7Wf_XsKd46S6Jb9FK3bYIVMSj8iUBIPkgsvYZgoOKssXBREI9IZHKzDUvFHvZbTaDMY9w4abUftKV0Zu8h1T0GSd06dAoscn6snC54qWv3Xqgwrf08pyQ9C_2qPZ"
            json.put("to", "/$token/" + "EnviarNoti")
            val notificacion = JSONObject()
            notificacion.put("titulo", titulo)
            notificacion.put("detalle", mensaje)
            notificacion.put("dato"," mensaje de kerkly")

            //  notificacion.put("foto", url_foto)
            json.put("data", notificacion)
            val URL = "https://fcm.googleapis.com/fcm/send"
            val request: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, URL, json, null, null) {
                    override fun getHeaders(): Map<String, String> {
                        val header: MutableMap<String, String> = HashMap()
                        header["content-type"] = "application/json"
                        header["authorization"] = "key=AAAA5adbonE:APA91bE_Ymd-u5HEcSLb3Ps5878UXdXMf1GXT_Yrl9l5m3CPHlwyEXqchhqblmetYtejadNViumDgtxCBDEiO7nUu5K7yNSc52AsIIviInR93QqLhsWIT4fXLZj3L_R36W4y5lF633Pj"
                        return header
                    }
                }
            myrequest.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    fun llamarTopicAceptarSolicitudNormal(context: Context,folio:String, token: String, mensaje: String, titulo: String,
                                          tipoSolicitud:String,telefonoCliente: String,nombreCliente: String,uidCliente: String
                                          ,fechaSolicitud:String,problematica:String,pagoTotal:String,nombreOficio:String
                                          ,telefonoKerkly: String,nombreKerkly:String
                                          ,direccionKerkly:String,correoKerkly:String,uidKerkly: String) {
        val myrequest = Volley.newRequestQueue(context)
        val json = JSONObject()
        try {
            // val token = "clRv1kYVRaGMDXONlKGJCU:APA91bGIcMSjD7SQ7Wf_XsKd46S6Jb9FK3bYIVMSj8iUBIPkgsvYZgoOKssXBREI9IZHKzDUvFHvZbTaDMY9w4abUftKV0Zu8h1T0GSd06dAoscn6snC54qWv3Xqgwrf08pyQ9C_2qPZ"
            json.put("to", "/$token/" + "EnviarNoti")
            val notificacion = JSONObject()
            notificacion.put("tipoNoti", "llamarTopicAceptarSolicitudNormal")
            notificacion.put("titulo", titulo)
            notificacion.put("detalle", mensaje)
            notificacion.put("TipoDeSolicitud",tipoSolicitud)
            notificacion.put("Telefono",telefonoCliente)
            notificacion.put("nombreCompletoCliente",nombreCliente)
            notificacion.put("uidCliente",uidCliente)
            notificacion.put("folio",folio)
            notificacion.put("Fecha",fechaSolicitud)
            notificacion.put("Problema",problematica)
            notificacion.put("pagoTotal",pagoTotal)
            notificacion.put("Oficio",nombreOficio)
            notificacion.put("telefonoKerkly",telefonoKerkly)
            notificacion.put("nombreCompletoKerkly",nombreKerkly)
            notificacion.put("direccionKerkly",direccionKerkly)
            notificacion.put("correoKerkly",correoKerkly)
            notificacion.put("uidKerkly",uidKerkly)

            //  notificacion.put("foto", url_foto)
            json.put("data", notificacion)
            val URL = "https://fcm.googleapis.com/fcm/send"
            val request: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, URL, json, null, null) {
                    override fun getHeaders(): Map<String, String> {
                        val header: MutableMap<String, String> = HashMap()
                        header["content-type"] = "application/json"
                        header["authorization"] = "key=AAAA5adbonE:APA91bE_Ymd-u5HEcSLb3Ps5878UXdXMf1GXT_Yrl9l5m3CPHlwyEXqchhqblmetYtejadNViumDgtxCBDEiO7nUu5K7yNSc52AsIIviInR93QqLhsWIT4fXLZj3L_R36W4y5lF633Pj"
                        return header
                    }
                }
            myrequest.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
    fun chats(context: Context, token: String, Mensaje: String, Titulo: String,
              telefonoKerkly:String,telefonoCliente:String, nombreCliente:String, fotoKerkly:String,fotoCliente:String,tokenKerkly:String,
              uidCliente: String, uidKerkly:String) {
       // println("uidCliente $uidCliente uidKerkly $uidKerkly")
        var firebaseMessaging = FirebaseMessaging.getInstance().subscribeToTopic("EnviarNoti")
        firebaseMessaging.addOnCompleteListener {
            //Toast.makeText(this@MainActivityChats, "Registrado:", Toast.LENGTH_SHORT).show()
       // println("suscrito ")
        }

        val myrequest = Volley.newRequestQueue(context)
        val json = JSONObject()
        try {
            //  val token = "fNBcaF1mT2qEj9KexMEduK:APA91bGPhunaVKF8eBITrArKl_G_5qvl-ZLAOPzsBxhEZaNXH-MqmrISayZDxVt1FjzdU-qXPECesJ2IvhPM-f1lgo6786bANQT_apL2iMhV2DV5k1Uw9YYp1_m_5qcT8IfaW4QETJE_"
            json.put("to", "/$token/" + "EnviarNoti")
            val notificacion = JSONObject()
            notificacion.put("tipoNoti", "chats")
            notificacion.put("titulo", Titulo)
            notificacion.put("detalle", Mensaje)
            notificacion.put("nombreCompletoK", Titulo)
            notificacion.put("nombreCompletoCliente", nombreCliente)
            notificacion.put("telefonok", telefonoKerkly)
            notificacion.put("telefonoCliente", telefonoCliente)
            notificacion.put("urlFotoKerkly", fotoKerkly)
            notificacion.put("urlFotoCliente", fotoCliente)
            notificacion.put("tokenKerkly", tokenKerkly)
            notificacion.put("tokenCliente", token)
            notificacion.put("uidCliente", uidCliente)
            notificacion.put("uidKerkly", uidKerkly)

            // Imprimir cada valor
            println("titulo: $Titulo")
            println("detalle: $Mensaje")
            println("nombreCompletoK: $Titulo")
            println("nombreCompletoCliente: $nombreCliente")
            println("telefonok: $telefonoKerkly")
            println("telefonoCliente: $telefonoCliente")
            println("urlFotoKerkly: $fotoKerkly")
            println("urlFotoCliente: $fotoCliente")
            println("tokenKerkly: $tokenKerkly")
            println("tokenCliente: $token")
            println("uidCliente: $uidCliente")
            println("uidKerkly: $uidKerkly")

            //  notificacion.put("foto", url_foto)
            json.put("data", notificacion)
            val URL = "https://fcm.googleapis.com/fcm/send"
            val request: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, URL, json, null, null) {
                    override fun getHeaders(): Map<String, String> {
                        val header: MutableMap<String, String> = HashMap()
                        header["content-type"] = "application/json"
                        header["authorization"] = "key=AAAA5adbonE:APA91bE_Ymd-u5HEcSLb3Ps5878UXdXMf1GXT_Yrl9l5m3CPHlwyEXqchhqblmetYtejadNViumDgtxCBDEiO7nUu5K7yNSc52AsIIviInR93QqLhsWIT4fXLZj3L_R36W4y5lF633Pj"
                        return header
                    }
                }
            myrequest.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
            println("error al enviar noti ${e.message}")
        }
    }


    fun llamarTopicAceptarSolicitudUrgente(context: Context,folio:String, token: String, mensaje: String, titulo: String,
                                          tipoSolicitud:String,telefonoCliente: String,nombreCliente: String,uidCliente: String
                                          ,fechaSolicitud:String,problematica:String,pagoTotal:String,nombreOficio:String
                                          ,telefonoKerkly: String,nombreKerkly:String
                                          ,direccionKerkly:String,correoKerkly:String,uidKerkly: String) {
        val myrequest = Volley.newRequestQueue(context)
        val json = JSONObject()
        try {
            // val token = "clRv1kYVRaGMDXONlKGJCU:APA91bGIcMSjD7SQ7Wf_XsKd46S6Jb9FK3bYIVMSj8iUBIPkgsvYZgoOKssXBREI9IZHKzDUvFHvZbTaDMY9w4abUftKV0Zu8h1T0GSd06dAoscn6snC54qWv3Xqgwrf08pyQ9C_2qPZ"
            json.put("to", "/$token/" + "EnviarNoti")
            val notificacion = JSONObject()
            notificacion.put("tipoNoti", "llamarTopicAceptarSolicitudUrgente")
            notificacion.put("titulo", titulo)
            notificacion.put("detalle", mensaje)
            notificacion.put("TipoDeSolicitud",tipoSolicitud)
            notificacion.put("Telefono",telefonoCliente)
            notificacion.put("nombreCompletoCliente",nombreCliente)
            notificacion.put("uidCliente",uidCliente)
            notificacion.put("folio",folio)
            notificacion.put("Fecha",fechaSolicitud)
            notificacion.put("Problema",problematica)
            notificacion.put("pagoTotal",pagoTotal)
            notificacion.put("Oficio",nombreOficio)
            notificacion.put("telefonoKerkly",telefonoKerkly)
            notificacion.put("nombreCompletoKerkly",nombreKerkly)
            notificacion.put("direccionKerkly",direccionKerkly)
            notificacion.put("correoKerkly",correoKerkly)
            notificacion.put("uidKerkly",uidKerkly)
           // Toast.makeText(context,"noti enviado",Toast.LENGTH_SHORT).show()
            //  notificacion.put("foto", url_foto)
            json.put("data", notificacion)
            val URL = "https://fcm.googleapis.com/fcm/send"
            val request: JsonObjectRequest =
                object : JsonObjectRequest(Method.POST, URL, json, null, null) {
                    override fun getHeaders(): Map<String, String> {
                        val header: MutableMap<String, String> = HashMap()
                        header["content-type"] = "application/json"
                        header["authorization"] = "key=AAAA5adbonE:APA91bE_Ymd-u5HEcSLb3Ps5878UXdXMf1GXT_Yrl9l5m3CPHlwyEXqchhqblmetYtejadNViumDgtxCBDEiO7nUu5K7yNSc52AsIIviInR93QqLhsWIT4fXLZj3L_R36W4y5lF633Pj"
                        return header
                    }
                }
            myrequest.add(request)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}