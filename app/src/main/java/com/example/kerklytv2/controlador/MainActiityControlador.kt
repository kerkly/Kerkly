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
import retrofit.client.OkClient
import retrofit.client.Response
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager

class MainActiityControlador {

    fun verificarUsuario(kerkly: Kerkly, context: AppCompatActivity) {
        val ROOT_URL = Url().URL
        val adapter = RestAdapter.Builder()
            .setEndpoint(ROOT_URL)
            .build()
        val api: LoginInterface = adapter.create(LoginInterface::class.java)

        api.LoginKerkly(kerkly.getTelefono(), kerkly.getContrasenia(), object : Callback<Response?> {
            override fun success(t: Response?, response: Response?) {
                var entrada: BufferedReader? = null
                try {
                    entrada = BufferedReader(InputStreamReader(t?.body?.`in`()))
                    val respuesta = entrada.readLine()

                    if (response?.status == 200 && respuesta == "1") {
                        val intent = Intent(context, InterfazKerkly::class.java)
                        intent.putExtra("numT", kerkly.getTelefono())
                        context.startActivity(intent)
                        context.finish()
                    } else {
                        Toast.makeText(context, "El Usuario o contraseña son incorrectos", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    entrada?.close()
                }
            }

            override fun failure(error: RetrofitError?) {
                // Manejar errores de manera más específica y segura
                Toast.makeText(context, "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
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
                    } finally {
                        reader?.close()
                    }

                    if (output == "0") {
                        val intent = Intent(contexto, MainActivity::class.java)
                        intent.putExtra("Telefono", output)
                        contexto.startActivity(intent)
                        contexto.finish()
                    } else {
                        val intent = Intent(contexto, InterfazKerkly::class.java)
                        intent.putExtra("numT", output)
                        contexto.startActivity(intent)
                        contexto.finish()
                    }
                }

                override fun failure(error: RetrofitError) {
                    Toast.makeText(
                        contexto,
                        "Tenemos Problemas con el Servidor.... por favor intente más tarde",
                        Toast.LENGTH_SHORT
                    ).show()
                    println("error ->Pantalla inicio -- ${error.message}")
                    println("Error: ${error.message}")
                    println("URL: ${error.url}")
                    println("Body: ${error.response?.body}")

                    contexto.finish()
                }
            }
        )
    }

}
