package com.example.kerklytv2.vista

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.SetProgressDialog
import com.example.kerklytv2.interfaces.CerrarSesionInterface
import com.example.kerklytv2.interfaces.ObtenerKerklyInterface
import com.example.kerklytv2.interfaces.ObtenerKerklyaOficiosInterface
import com.example.kerklytv2.interfaces.SesionAbiertaInterface
import com.example.kerklytv2.modelo.Kerkly
import com.example.kerklytv2.modelo.serial.OficioKerkly
import com.example.kerklytv2.modelo.usuarios
import com.example.kerklytv2.ui.home.HomeFragment
import com.example.kerklytv2.url.Url
import com.example.kerklytv2.vista.fragments.*
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONException
import org.json.JSONObject
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
import java.text.DateFormat
import java.util.*

class InterfazKerkly : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var kerkly: Kerkly
    private lateinit var b: Bundle
    private lateinit var id: String
    private lateinit var telefonoKerkly: String
    private lateinit var nombre_completo: String
    private lateinit var nombreKerkly: String
    private lateinit var correo: String
    private lateinit var curp: String
    private lateinit var txt_nombre: TextView
    private lateinit var txt_correo: TextView
    private lateinit var txt_oficios: TextView
    lateinit var ofi: MutableList<String>
    private lateinit var drawerLayout: DrawerLayout
    lateinit var postList: ArrayList<OficioKerkly>

    //Autenticacion con cuenta de google
    var providers: MutableList<AuthUI.IdpConfig?>? = null
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private val MY_REQUEST_CODE = 200

    //firebase Realtime data base
    lateinit var photoUrl: String
    lateinit var name: String
    lateinit var correoKerkly: String
    private lateinit var token: String
    val setProgressDialog = SetProgressDialog()



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_kerkly)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        setProgressDialog.setProgressDialog(this)
        //Autenticacion
        providers = Arrays.asList(
            // EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())
        mAuth = FirebaseAuth.getInstance()
        id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        kerkly = Kerkly()
        b = intent.extras!!
        telefonoKerkly = b.getString("numT").toString()

        sesion(telefonoKerkly)
        getKerkly()

        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_content_interfaz_kerkly)

        val view = navView.getHeaderView(0)
        txt_correo = view.findViewById(R.id.correo_header)
        txt_nombre = view.findViewById(R.id.nombre_header)
        txt_oficios = view.findViewById(R.id.oficios_header)



        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.ContactosFragment,
                R.id.nav_slideshow,
                R.id.presupuestoFragment,
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.nav_home -> setFragmentHome()
                R.id.ContactosFragment -> setFragmentcontactos() // mensajes
                R.id.nav_slideshow -> setFragmenTrabajos() // agenda
                R.id.presupuestoFragment -> setFragmentPresupuesto()
                R.id.historialFragment -> setFragmentHistorial() // historial
                R.id.cerrar_sesion_nav -> cerrarSesion() // cerrar sesion
            }

            drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

/*
        //ejemplo en firebase
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")
        myRef.setValue("Hola Luis luis")
        System.out.println("entro en lo de firebase")*/
        askNotificationPermission()
        createChanel()
        //noti

       /* val intent = Intent(this, MyIntentServiceMensajes::class.java)
        intent.action = Constants.ACTION_RUN_ISERVICE
        startService(intent)*/


    }
    companion object{
        const val chanel_id = "chanelID"
         var mensaje: String = "mensaje ejemplo"
        var titulo: String = "soy luis"
        const val ID_NOTIFICACION = 1
    }

    fun createChanel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel(
                chanel_id,
                "my channel",
                NotificationManager.IMPORTANCE_DEFAULT

            ).apply {
                description = "MisNotificaciones"

            }

            val notificationManager : NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)

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
                        metodoSalir()
                        Toast.makeText(applicationContext, "Sesión cerrada", Toast.LENGTH_SHORT).show()
                        val i = Intent(applicationContext, MainActivity::class.java)
                        startActivity(i)
                    }
                }

                override fun failure(error: RetrofitError?) {
                    Toast.makeText(
                        applicationContext,
                        "Codigo de respuesta de error: $error",
                        Toast.LENGTH_SHORT
                    ).show()
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
        print("aqui 260")
        val args = Bundle()
        val num = b.getString("numT")
        args.putString("numNR", num)
        args.putString("Curp", curp)
        val f =
            TrabajosPendientesFragment()
        f.arguments = args
        var fm = supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
        }
    }
    private fun setFragmentcontactos() {
        val b2 = Bundle()
        val f = ContactosFragment()
        f.arguments = b2
        b2!!.putString("telefonoKerkly", telefonoKerkly)
        b2!!.putString("urlFotoKerkly", photoUrl)
        b2!!.putString("nombreCompletoKerkly", nombre_completo)
        b2!!.putString("correoKerkly", correoKerkly)

        var fm = supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
        }
    }

    private fun setFragmentPresupuesto() {
        val args = Bundle()
        val num = b.getString("numT")
        args!!.putString("telefonoKerkly", telefonoKerkly)
        args!!.putString("urlFotoKerkly", photoUrl)
       // args!!.putString("nombreKerkly", name)
        args!!.putString("correoKerkly", correoKerkly)
        args.putString("numNR", num)
        args.putString("Curp", curp)
       // args.putString("nombrekerkly", nombreKerkly)
        args.putSerializable("arrayOfcios", postList)
        args.putString("nombreCompletoKerkly", nombre_completo)
        val f = PresupuestosPreviewFragment()
        f.arguments = args
        var fm = supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
        }
    }

    private fun setFragmentNotificaciones() {
        val f = PresupuestoFragment()
        var fm = supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
    }



    private fun setFragmentAgenda() {
        val f = PresupuestoFragment()
        var fm = supportFragmentManager.beginTransaction().add(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
    }

    private fun setFragmentHistorial() {
        val args = Bundle()
        val num = b.getString("numT")
        args.putString("numNR", num)
        args.putString("Curp", curp)
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

                   // Log.e("nosee", output)

                }

                override fun failure(error: RetrofitError) {
                    println("error $error")
                }

            }
        )
    }

    private fun getOficiosKerkly() {
        val ROOT_URL = Url().URL
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("$ROOT_URL/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val oficios = retrofit.create(ObtenerKerklyaOficiosInterface::class.java)
        val call = oficios.getOficios_kerkly(curp)

        call?.enqueue(object : Callback<List<OficioKerkly?>?> {
            override fun onResponse(
                call: Call<List<OficioKerkly?>?>,
                response: retrofit2.Response<List<OficioKerkly?>?>
            ) {
                 postList = response.body() as ArrayList<OficioKerkly>

                 ofi  = mutableListOf()
                var acumulador = ""
                for (i in 0 until postList.size){
                    if(i == (postList.size-1)) {
                        acumulador += "${postList[i].nombreOficio}"
                        ofi.add(acumulador)
                    } else {
                        acumulador += "${postList[i].nombreOficio}, "
                    }
                }
                txt_oficios.text = acumulador
                setProgressDialog.dialog!!.dismiss()

            }

            override fun onFailure(call: Call<List<OficioKerkly?>?>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            val alert: AlertDialog.Builder = AlertDialog.Builder(this)
            alert.setTitle(getString(R.string.cerrar_app))
            alert.setMessage(getString(R.string.mensaje_alertaCerrarApp))
            alert.setCancelable(false)
            alert.setPositiveButton(getString(R.string.confirmar_alertCerrarApp)) { dialogo1, id -> finish() }
            alert.setNegativeButton(getString(R.string.cancelar_alertCerrarApp)) { dialogo1, id -> dialogo1.dismiss() }
            alert.show()
        }
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
        val call = presupuestoGET.getKerkly(telefonoKerkly)
        call?.enqueue(object: Callback<List<com.example.kerklytv2.modelo.serial.Kerkly?>?> {
            override fun onResponse(
                call: Call<List<com.example.kerklytv2.modelo.serial.Kerkly?>?>,
                response: retrofit2.Response<List<com.example.kerklytv2.modelo.serial.Kerkly?>?>
            ) {
                val postList: ArrayList<com.example.kerklytv2.modelo.serial.Kerkly> = response.body() as
                        ArrayList<com.example.kerklytv2.modelo.serial.Kerkly>

                if (postList.size == 0){
                    Toast.makeText(this@InterfazKerkly, "lista vacia", Toast.LENGTH_SHORT).show()
                }else{
                    correo = postList[0].correo
                    val nombre = postList[0].nombre
                    val ap = postList[0].ap
                    val am = postList[0].am
                    curp = postList[0].curp
                    nombreKerkly = nombre
                    nombre_completo = "$nombre $ap $am"

                    txt_nombre.text = nombre_completo
                    txt_correo.text = correo

                    Log.d("correo", correo)
                    getOficiosKerkly()
                }

            }

            override fun onFailure(
                call: Call<List<com.example.kerklytv2.modelo.serial.Kerkly?>?>,
                t: Throwable
            ) {
                Toast.makeText(
                    applicationContext,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    //metodos para la Autenticacion con cuenta de google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MY_REQUEST_CODE) {
            val response = IdpResponse.fromResultIntent(data)
            if (requestCode == RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser

                Toast.makeText(this, "saludos" + user!!.email, Toast.LENGTH_SHORT).show()
                correo = currentUser!!.getEmail()!!



            }
        }
    }
    fun muestraOpciones() {
        startActivityForResult(
            AuthUI.getInstance().createSignInIntentBuilder()
                .setIsSmartLockEnabled(false)
                .setAvailableProviders(providers!!)
                .build(),MY_REQUEST_CODE
        )
    }

    override fun onStart() {
        super.onStart()
        currentUser = mAuth!!.currentUser
        val user = FirebaseAuth.getInstance().currentUser
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            if (currentUser != null) {
                //obtenerToken
                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                        return@OnCompleteListener
                    }
                     token = task.result


                   // Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()
                    name = currentUser!!.displayName.toString()
                    correoKerkly = currentUser!!.email.toString()
                    photoUrl = currentUser!!.photoUrl.toString()
                    val uid = currentUser!!.uid
                    val foto = photoUrl.toString()

                    // Toast.makeText(this, "entro : $email ", Toast.LENGTH_SHORT).show()
                    // Toast.makeText(MainActivity.this, "demtro onStart usauru " +  name, Toast.LENGTH_LONG).show();
                    val database = FirebaseDatabase.getInstance()


                    val databaseReference = database.getReference("UsuariosR").child("$telefonoKerkly")
                    val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
                    //val usuario = usuarios()

                    //val u = usuarios(uid, email, name, foto, currentDateTimeString)
                    databaseReference.child("MisDatos").setValue(usuarios(telefonoKerkly, correoKerkly.toString(), name.toString(), foto.toString(), currentDateTimeString.toString(), token)) { error, ref -> //txtprueba.setText(uid + "latitud " + latitud + " longitud " + longitud);
                        // Toast.makeText(this@InterfazKerkly, "Bienvenido $token", Toast.LENGTH_SHORT) .show()

                    }
                })


            }else{
                muestraOpciones()
            }

        } else {
            Toast.makeText(this@InterfazKerkly, "No hay conexion a Internet", Toast.LENGTH_LONG)
                .show()
        }

    }

    fun metodoSalir() {
        AuthUI.getInstance()
            .signOut(applicationContext)
            .addOnCompleteListener { muestraOpciones() }.addOnFailureListener { e ->
                Toast.makeText(
                    applicationContext, ""
                            + e.message, Toast.LENGTH_LONG
                ).show()
            }
    }

//Permisos en tiempo de ejecucion para recibir Notificaciones
// Declare the launcher at the top of your Activity/Fragment:
private val requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
    if (isGranted) {
        // FCM SDK (and your app) can post notifications.
        Toast.makeText(this, "su aplicacion si Podra Recibir Notificaciones", Toast.LENGTH_SHORT).show()
    } else {
        // TODO: Inform user that that your app will not show notifications.
        Toast.makeText(this, "Lo sentimos su aplicacion No Podra Recibir Notificaciones", Toast.LENGTH_SHORT).show()
    }
}
    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                // TODO: display an educational UI explaining to the user the features that will be enabled
                //       by them granting the POST_NOTIFICATION permission. This UI should provide the user
                //       "OK" and "No thanks" buttons. If the user selects "OK," directly request the permission.
                //       If the user selects "No thanks," allow the user to continue without notifications.
            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}