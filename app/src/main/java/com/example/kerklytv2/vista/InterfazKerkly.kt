package com.example.kerklytv2.vista

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import com.example.kerklytv2.R
import com.example.kerklytv2.interfaces.CerrarSesionInterface
import com.example.kerklytv2.interfaces.ObtenerKerklyInterface
import com.example.kerklytv2.interfaces.SesionAbiertaInterface
import com.example.kerklytv2.modelo.Kerkly
import com.example.kerklytv2.modelo.serial.HistorialNormal
import com.example.kerklytv2.ui.home.HomeFragment
import com.example.kerklytv2.url.Url
import com.example.kerklytv2.vista.fragments.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader

class InterfazKerkly : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var kerkly: Kerkly
    private lateinit var b: Bundle
    private lateinit var id: String
    private lateinit var telefono: String
    private lateinit var nombre_completo: String
    private lateinit var correo: String
    private lateinit var curp: String
    private lateinit var txt_nombre: TextView
    private lateinit var txt_correo: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_kerkly)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        kerkly = Kerkly()
        b = intent.extras!!

        telefono = b.getString("numT").toString()

        sesion(telefono!!)
        getKerkly()



        val drawerLayout: DrawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_content_interfaz_kerkly)

        val view = navView.getHeaderView(0)
        txt_correo = view.findViewById(R.id.correo_header)
        txt_nombre = view.findViewById(R.id.nombre_header)



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.mensajesFragment,
                R.id.nav_slideshow,
                R.id.presupuestoFragment,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> setFragmentHome()
             //   R.id.mensajesFragment -> setFragmentMensajes() // mensajes
                R.id.nav_slideshow -> setFragmenTrabajos() // agenda
                R.id.presupuestoFragment -> setFragmentPresupuesto()
                R.id.historialFragment -> setFragmentHistorial() // historial
                R.id.cerrar_sesion_nav -> cerrarSesion() // cerrar sesion
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }
    }

    private fun cerrarSesion() {
        Log.d("curp",curp)
        val ROOT_URL = Url().URL

        val adapter = RestAdapter.Builder()
            .setEndpoint(ROOT_URL)
            .build()

        val api = adapter.create(CerrarSesionInterface::class.java)
        api.cerrar(curp,
            object : retrofit.Callback<Response> {
                override fun success(t: Response?, response: Response?) {
                    var reader: BufferedReader? = null
                    var output = ""
                    try {
                        reader = BufferedReader(InputStreamReader(t?.body?.`in`()))

                        output = reader.readLine()


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    Log.d("output", output)

                    if (output == "1") {
                        Toast.makeText(applicationContext, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        val i = Intent(applicationContext, MainActivity::class.java)
                        startActivity(i)
                    }
                }

                override fun failure(error: RetrofitError?) {
                    TODO("Not yet implemented")
                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.interfaz_kerkly, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_interfaz_kerkly)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    private fun setFragmentHome() {
        val f = HomeFragment()
        val fm = supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly, f).commit()
        }
    }

    private fun setFragmenTrabajos() {
        val args = Bundle()
        val num = b.getString("numT")
        args.putString("numNR", num)
        val f =
            TrabajosPendientesFragment()
        f.arguments = args
        var fm = supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
        }
    }


    private fun setFragmentPresupuesto() {
        val args = Bundle()
        val num = b.getString("numT")
        args.putString("numNR", num)
        val f = PresupuestosPreviewFragment()
        f.arguments = args
        var fm = supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
        }
    }

    private fun setFragmentNotificaciones() {
        // val args = Bundle()
        //   val num = b.getString("numT")
        //     args.putString("numNR", num)
        val f = PresupuestoFragment()
        //   f.arguments = args
        var fm = supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
    }

    private fun setFragmentMensajes() {
        //  val args = Bundle()
        //  val num = b.getString("numT")
        //  args.putString("numNR", num)
        val f = MensajesFragment()
        //  f.arguments = args
        var fm = supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
        }
    }

    private fun setFragmentAgenda() {
        //  val args = Bundle()
        //  val num = b.getString("numT")
        //  args.putString("numNR", num)
        val f = PresupuestoFragment()
        //  f.arguments = args
        var fm = supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
    }

    private fun setFragmentHistorial() {
        val args = Bundle()
        val num = b.getString("numT")
        args.putString("numNR", num)
        val f = HistorialFragment()
        f.arguments = args
        var fm = supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
    }

    private fun sesion(telefono: String) {
        val ROOT_URL = Url().URL
        val adapter = RestAdapter.Builder()
            .setEndpoint(ROOT_URL)
            .build()
        val api = adapter.create(SesionAbiertaInterface::class.java)
        api.sesionAbierta(
            telefono,
            id,
            object : retrofit.Callback<retrofit.client.Response?> {
                override fun success(t: retrofit.client.Response?, response2: retrofit.client.Response?) {
                    var reader: BufferedReader? = null
                    var output = ""
                    try {
                        reader = BufferedReader(InputStreamReader(t?.body?.`in`()))

                        output = reader.readLine()


                    } catch (e: IOException) {
                        e.printStackTrace()
                    }

                    Log.e("nosee", output)

                }

                override fun failure(error: RetrofitError) {
                    println("error $error")
                }

            }
        )
    }

    private fun getKerkly() {
        val ROOT_URL = Url().URL
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("$ROOT_URL/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val presupuestoGET = retrofit.create(ObtenerKerklyInterface::class.java)
        val call = presupuestoGET.getKerkly(telefono)
        call?.enqueue(object: Callback<List<com.example.kerklytv2.modelo.serial.Kerkly?>?> {
            override fun onResponse(
                call: Call<List<com.example.kerklytv2.modelo.serial.Kerkly?>?>,
                response: retrofit2.Response<List<com.example.kerklytv2.modelo.serial.Kerkly?>?>
            ) {
                val postList: ArrayList<com.example.kerklytv2.modelo.serial.Kerkly> = response.body() as
                        ArrayList<com.example.kerklytv2.modelo.serial.Kerkly>

                correo = postList[0].correo
                val nombre = postList[0].nombre
                val ap = postList[0].ap
                val am = postList[0].am
                curp = postList[0].curp

                nombre_completo = "$nombre $ap $am"

                txt_nombre.text = nombre_completo
                txt_correo.text = correo

                Log.d("correo", correo)
            }

            override fun onFailure(
                call: Call<List<com.example.kerklytv2.modelo.serial.Kerkly?>?>,
                t: Throwable
            ) {
                Toast.makeText(
                    applicationContext,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show();
            }

        })
    }
}