package com.example.kerklytv2.controlador

import android.content.Intent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.kerklytv2.interfaces.LoginInterface
import com.example.kerklytv2.interfaces.VerificarSesionInterface
import com.example.kerklytv2.modelo.Kerkly
import com.example.kerklytv2.url.Url
import com.example.kerklytv2.vista.InterfazKerkly
import com.example.kerklytv2.vista.MainActivity
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class MainActiityControlador {

    fun verificarUsuario(kerkly: Kerkly, context: AppCompatActivity) {
        val ROOT_URL = Url().URL
        //Toast.makeText(context, kerkly.getTelefono(), Toast.LENGTH_SHORT).show()
        val adapter = RestAdapter.Builder()
            .setEndpoint(ROOT_URL)
            .build()
        val api: LoginInterface = adapter.create(LoginInterface::class.java)
        api.LoginKerkly(kerkly.getTelefono(),
            kerkly.getContrasenia(),
            object : Callback<Response?> {
                override fun success(t: Response?, response: Response?) {
                    var entrada: BufferedReader? =  null
                    var Respuesta = ""
                    try {
                        entrada = BufferedReader(InputStreamReader(t?.body?.`in`()))
                        Respuesta = entrada.readLine()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }

                    val resp1 = "1"
                    if (resp1.equals(Respuesta)){
                        //Toast.makeText(context, "Todo exelente, ya pasaria a la sig actividad", Toast.LENGTH_SHORT).show()
                        val  intent = Intent(context, InterfazKerkly::class.java)
                        intent.putExtra("numT", kerkly.getTelefono())
                        context.startActivity(intent)
                        context.finish()

                    }else{
                        Toast.makeText(context, "El Usuario o contraseña Son Incorrectos", Toast.LENGTH_SHORT).show()
                       //  Toast.makeText(context, "$Respuesta", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun failure(error: RetrofitError?) {
                    Toast.makeText(context, "error $error", Toast.LENGTH_SHORT).show()
                }

            }
        )
    }

    fun verificarSesion(id: String, contexto: AppCompatActivity) {
        val ROOT_URL = Url().URL
        val adapter = RestAdapter.Builder()
            .setEndpoint(ROOT_URL)
            .build()
        val api = adapter.create(VerificarSesionInterface::class.java)
        api.sesionAbierta(
            id,
            object : Callback<Response?> {
                override fun success(t: Response?, response2: Response?) {
                    var reader: BufferedReader? = null
                    var output = ""
                    try {
                        reader = BufferedReader(InputStreamReader(t?.body?.`in`()))
                        output = reader.readLine()
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    if(output == "0") {

                        val  intent = Intent(contexto, MainActivity::class.java)
                        intent.putExtra("Telefono", output)
                        contexto.startActivity(intent)
                        contexto.finish()
                    } else {
                        val  intent = Intent(contexto, InterfazKerkly::class.java)
                        intent.putExtra("numT", output)
                        contexto.startActivity(intent)
                        contexto.finish()
                    }

                }

                override fun failure(error: RetrofitError) {
                    Toast.makeText(contexto, "Tenemos Problemas con el Servidor.... por favor intente mas tarde",Toast.LENGTH_SHORT).show()
                    println("error ->Pantalla inicio -- ${error.message}")
                    contexto.finish()
                }

            }
        )
    }
}
