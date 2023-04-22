package com.example.kerklytv2.Notificacion

import android.content.Context
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
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