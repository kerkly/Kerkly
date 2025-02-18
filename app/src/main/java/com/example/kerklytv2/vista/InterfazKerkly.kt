package com.example.kerklytv2.vista
import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.SQLException
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.location.LocationManager
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.kerklytv2.ProgresSQL.conexionPostgreSQL
import com.example.kerklytv2.R
import com.example.kerklytv2.SQLite.usuariosSqlite
import com.example.kerklytv2.clases.NetworkSpeedChecker
import com.example.kerklytv2.controlador.SetProgressDialog
import com.example.kerklytv2.databinding.ActivityInterfazKerklyBinding
import com.example.kerklytv2.interfaces.CerrarSesionInterface
import com.example.kerklytv2.interfaces.ObtenerKerklyInterface
import com.example.kerklytv2.interfaces.ObtenerKerklyaOficiosInterface
import com.example.kerklytv2.interfaces.SesionAbiertaInterface
import com.example.kerklytv2.modelo.Kerkly
import com.example.kerklytv2.modelo.serial.OficioKerkly
import com.example.kerklytv2.modelo.usuarios
import com.example.kerklytv2.vista.fragments.HistorialFragment
import com.example.kerklytv2.vista.fragments.PresupuestoFragment
import com.example.kerklytv2.url.Instancias
import com.example.kerklytv2.url.Url
import com.example.kerklyv5.SQLite.DataManager
import com.firebase.ui.auth.AuthUI
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
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
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStreamReader
import java.text.DateFormat
import java.util.*

class InterfazKerkly : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var binding: ActivityInterfazKerklyBinding
    private lateinit var kerkly: Kerkly
    private lateinit var b: Bundle
    private lateinit var idKerkly: String
    private lateinit var telefonoKerkly: String
    private lateinit var nombre_completo: String
    private lateinit var nombreKerkly: String
    private lateinit var correo: String
    private lateinit var curp: String
    private lateinit var txt_nombre: TextView
    private lateinit var txt_correo: TextView
    private lateinit var txt_oficios: TextView
    lateinit var ofi: MutableList<String>

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
    private lateinit var tokenKerkly: String
    val setProgressDialog = SetProgressDialog()
    private lateinit var ImageViewPerfil : ImageView
    private lateinit var direccionKerly: String
    private lateinit var dataManager: DataManager
    private lateinit var instancias: Instancias

    //Ubicacion
    private val REQUEST_LOCATION_PERMISSION = 1
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    private var locationManager: LocationManager? = null
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private var handler: Handler? = null

    //controlar los procesos
    private var procesosEjecutados = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      //  setContentView(R.layout.activity_interfaz_kerkly)
       // val toolbar: Toolbar = findViewById(R.id.toolbar)
      //  setSupportActionBar(toolbar)

        binding = ActivityInterfazKerklyBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.appBarInterfazKerkly.toolbar)
        drawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_interfaz_kerkly)
        val view = navView.getHeaderView(0)

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.nav_home,
                R.id.nav_gallery,
                R.id.nav_slideshow
            ), drawerLayout
        )

        setupActionBarWithNavController(navController,appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.setNavigationItemSelectedListener { menuItem ->
            when(menuItem.itemId){
                R.id.nav_home -> {
                    // Hacer algo cuando se selecciona Home
                   // navegarAHome()
                    true
                }
                R.id.nav_gallery -> {
                    // Llamar al método navegarAGallery() cuando se selecciona Gallery
                    navegarAGallery()
                    true
                }
                R.id.nav_slideshow -> {
                    // Hacer algo cuando se selecciona Slideshow
                    navegarASlideshow()
                    true
                }
                R.id.cerrar_sesion_nav -> {
                    cerrarSesion()
                    true
                }

                else -> false
            }
        }

        setProgressDialog.setProgressDialog(this)
        getLocation()
        instancias = Instancias()

        NetworkSpeedChecker(this)

        txt_correo = view.findViewById(R.id.correo_header)
        txt_nombre = view.findViewById(R.id.nombre_header)
        txt_oficios = view.findViewById(R.id.oficios_header)

        ImageViewPerfil = view.findViewById(R.id.imageViewPerfil)
        //Autenticacion
        providers = Arrays.asList(
            // EmailBuilder().build(),
            AuthUI.IdpConfig.GoogleBuilder().build())
        mAuth = FirebaseAuth.getInstance()

        idKerkly = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        kerkly = Kerkly()
        b = intent.extras!!
        telefonoKerkly = b.getString("numT").toString()


     /*   setupActionBarWithNavController(navController, appBarConfiguration)
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
        }*/
        dataManager = DataManager(this)
        locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Verifica si se concedió el permiso de ubicación
        val gpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!gpsEnabled) { val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
        } else {
            // El permiso no está concedido, solicítalo en tiempo de ejecución
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_LOCATION_PERMISSION
            )
        }
    }

    private fun navegarASlideshow() {
          val bundle = Bundle()
         bundle.putString("telefonoKerkly", telefonoKerkly)
        bundle.putString("Curp", curp)
        bundle.putString("nombrekerkly", nombre_completo)
        val navController = findNavController(R.id.nav_host_fragment_content_interfaz_kerkly)
        navController.navigate(R.id.nav_slideshow, bundle)
        // Cierra el DrawerLayout después de la navegación
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun navegarAGallery() {
        val bundle = Bundle()
        bundle.putString("telefonoKerkly",telefonoKerkly)
        bundle.putString("urlFotoKerkly",currentUser!!.photoUrl.toString())
        bundle.putString("nombreCompletoKerkly",currentUser!!.displayName.toString())
        bundle.putString("tokenKerkly",tokenKerkly)
        bundle.putString("correoKerkly",currentUser!!.email.toString())
        bundle.putString("direccionkerkly",direccionKerly)
        bundle.putString("uidKerkly",currentUser!!.uid.toString())
        val navController = findNavController(R.id.nav_host_fragment_content_interfaz_kerkly)
        navController.navigate(R.id.nav_gallery, bundle)
        // Cierra el DrawerLayout después de la navegación
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    private fun navegarAHome() {
        val bundle = Bundle()
        bundle.putString("telefonoKerkly",telefonoKerkly)
        bundle.putString("urlFotoKerkly",currentUser!!.photoUrl.toString())
        bundle.putString("nombreCompletoKerkly",currentUser!!.displayName.toString())
        bundle.putString("Curp",curp)
        bundle.putString("correoKerkly",currentUser!!.email.toString())
        bundle.putString("direccionkerkly",direccionKerly)

        val navController = findNavController(R.id.nav_host_fragment_content_interfaz_kerkly)
        navController.navigate(R.id.nav_home, bundle)
        // Cierra el DrawerLayout después de la navegación
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // Configurar la callback para recibir actualizaciones de ubicación
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult?.lastLocation?.let {
                    // Aquí puedes obtener las coordenadas de ubicación en tiempo real
                    latitud = it.latitude
                    longitud = it.longitude

                }
            }
        }

        // Solicitar actualizaciones de ubicación
        val locationRequest = LocationRequest.create()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(10000) // Intervalo en milisegundos para recibir actualizaciones (10 segundos)
            .setFastestInterval(5000) // Intervalo mínimo en milisegundos entre actualizaciones (5 segundos)
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

        return
    }

    private fun cerrarSesion() {
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
                        val miConexion = conexionPostgreSQL()
                        val conexion = miConexion.obtenerConexion(applicationContext)
                        if (conexion != null) {
                            try {
                                val i = Intent(applicationContext, MainActivity::class.java)
                                startActivity(i)
                                miConexion.offKerkly(curp)
                            } catch (e: SQLException) {
                                // Manejar la excepción (lanzar, notificar al usuario, etc.)
                                println("Error al realizar operaciones de base de datos: ${e.message}")
                              // Otras acciones de manejo de errores según sea necesario
                            }
                        } else {
                            // Manejar el caso en el que la conexión no se pudo establecer
                            showMessage("problemas con la conexión BD Espacial")
                        }

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

  /*  private fun setFragmentHome() {
        val f = HomeFragment()
     supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly, f).commit()
        }
    }*/
  /*  private fun setFragmenTrabajos() {
        val args = Bundle()
        val num = b.getString("numT")
        args.putString("numNR", telefonoKerkly)
        args.putString("Curp", curp)
        args.putString("nombrekerkly", nombre_completo)
        val f = TrabajosPendientesFragment()
        f.arguments = args
        var fm = supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
        }
    }*/
   /* private fun setFragmentcontactos() {
        val b2 = Bundle()
        val f = ContactosFragment()
        f.arguments = b2
        if (telefonoKerkly.isNotEmpty() &&
            nombreKerkly.isNotEmpty() &&
            correoKerkly.isNotEmpty() &&
            currentUser!!.uid.isNotEmpty() &&
            tokenKerkly.isNotEmpty() &&
            currentUser!!.photoUrl.toString().isNotEmpty()
        ) {
            b2.putString("telefonoKerkly", telefonoKerkly)
            b2.putString("nombreKerkly", nombreKerkly)
            b2.putString("correoKerkly", correoKerkly)
            b2.putString("uidKerkly",  currentUser!!.uid)
            b2.putString("tokenKerkly", tokenKerkly)
            b2.putString("fotoKerkly", currentUser!!.photoUrl.toString())

            val f = ContactosFragment()
            f.arguments = b2

            val fm = supportFragmentManager.beginTransaction().apply {
                replace(R.id.nav_host_fragment_content_interfaz_kerkly, f)
                commit()
            }
        } else {
            // Manejar el caso en el que algún dato esté vacío
            Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
        }

    }*/

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
       // args.putSerializable("arrayOfcios", postList)
        args.putString("nombreCompletoKerkly", nombre_completo)
        args.putString("correoKerly", correoKerkly)
        args.putString("direccionkerkly", direccionKerly)
        //println("------> direccion kerkly $direccionKerly ")
      /*  val f = PresupuestosPreviewFragment()
        f.arguments = args
        var fm = supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
        }*/
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
    private fun sesion(telefono: String, idKerkly: String) {
        val ROOT_URL = Url().URL
        val adapter = RestAdapter.Builder()
            .setEndpoint(ROOT_URL)
            .build()
        val api = adapter.create(SesionAbiertaInterface::class.java)
        api.sesionAbierta(
            telefono,
            idKerkly,
            currentUser!!.uid,
            object : retrofit.Callback<retrofit.client.Response?> {
                override fun success(t: retrofit.client.Response?, response2: retrofit.client.Response?) {
                    var reader: BufferedReader? = null
                    var output = ""
                    try {
                        reader = BufferedReader(InputStreamReader(t?.body?.`in`()))
                        output = reader.readLine()
                        println("Respuesta de la sesion $output")
                       // showMessage("Respuesta de la sesion $output")
                        if (output== "1"){
                            //Toast.makeText(this, "entro" + currentUser!!.email, Toast.LENGTH_SHORT).show()
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
                                @SuppressLint("SuspiciousIndentation")
                                override fun onResponse(
                                    call: Call<List<com.example.kerklytv2.modelo.serial.Kerkly?>?>,
                                    response: retrofit2.Response<List<com.example.kerklytv2.modelo.serial.Kerkly?>?>
                                ) {
                                    val postList: ArrayList<com.example.kerklytv2.modelo.serial.Kerkly> = response.body() as
                                            ArrayList<com.example.kerklytv2.modelo.serial.Kerkly>

                                    if (postList.size == 0){
                                        Toast.makeText(this@InterfazKerkly, "ocurio un error", Toast.LENGTH_SHORT).show()
                                        setProgressDialog.dialog!!.dismiss()
                                    }else{
                                        correo = postList[0].correo
                                        val correoActual = currentUser?.email?.toLowerCase()
                                        val correoComparar = correo?.toLowerCase()
                                        if(correoActual == correoComparar){
                                            val nombre = postList[0].nombre
                                            val ap = postList[0].ap
                                            val am = postList[0].am
                                            curp = postList[0].curp
                                            val telefono = telefonoKerkly.toLong()
                                            nombreKerkly = nombre
                                            nombre_completo = "$nombre $ap $am"
                                            direccionKerly = postList[0].Pais + " " + postList[0].Ciudad + " " + postList[0].Colonia + " " + postList[0].Calle
                                            photoUrl = currentUser!!.photoUrl.toString()
                                            correoKerkly = currentUser!!.email.toString()
                                            name = currentUser!!.displayName.toString()
                                            //guardar datos del usaurio en sql
                                            Glide.with(this@InterfazKerkly)
                                                .asBitmap()
                                                .load(photoUrl)
                                                .into(object : SimpleTarget<Bitmap>() {
                                                    override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                                                        val outputStream = ByteArrayOutputStream()
                                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                                                        val photoByteArray = outputStream.toByteArray()
                                                        val usuarios: usuariosSqlite
                                                        usuarios = usuariosSqlite(telefono,photoByteArray, nombre!!,ap,am,correo)
                                                        dataManager.verificarSiElUsarioExiste(this@InterfazKerkly,ImageViewPerfil,txt_nombre,txt_correo, photoByteArray,usuarios,telefono.toString(),nombre.toString(),ap,am,correo)

                                                    }
                                                })
                                            //obtenerToken
                                            var firebaseMessaging = FirebaseMessaging.getInstance().subscribeToTopic("EnviarNoti")
                                            firebaseMessaging.addOnCompleteListener {
                                                //Toast.makeText(this@MainActivityChats, "Registrado:", Toast.LENGTH_SHORT).show()
                                            }
                                            FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                                if (!task.isSuccessful) {
                                                    Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                                                    return@OnCompleteListener
                                                }
                                                tokenKerkly = task.result
                                                // Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()

                                                // Toast.makeText(MainActivity.this, "demtro onStart usauru " +  name, Toast.LENGTH_LONG).show();
                                                val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
                                                val databaseReference = instancias.referenciaInformacionDelUsuarioKerkly(currentUser!!.uid)
                                                databaseReference.setValue(usuarios(currentUser!!.uid,telefonoKerkly, correoKerkly.toString(), name.toString(), photoUrl.toString(), currentDateTimeString.toString(), tokenKerkly,curp)) { error, ref -> //txtprueba.setText(uid + "latitud " + latitud + " longitud " + longitud);
                                                    // Toast.makeText(this@InterfazKerkly, "Bienvenido $token", Toast.LENGTH_SHORT) .show()
                                                    // getKerkly(foto)
                                                    getOficiosKerkly()
                                                    setProgressDialog.dialog!!.dismiss()
                                                }
                                            })

                                            navegarAHome()
                                        }else{
                                            setProgressDialog.dialog!!.dismiss()
                                            // Toast.makeText(applicationContext,"no es correo", Toast.LENGTH_SHORT).show()
                                            cerrarSesion()
                                            Toast.makeText(this@InterfazKerkly, "Este correo ${currentUser!!.email} no pertenece a esta cuenta", Toast.LENGTH_LONG).show()
                                        }
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
                        }else{
                            showMessage("no se actualizo")
                        }

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
                call: Call<List<OficioKerkly?>?>, response: retrofit2.Response<List<OficioKerkly?>?>) {
                 postList = response.body() as ArrayList<OficioKerkly>
                 ofi  = mutableListOf()
                val totalItems = postList.size
                var itemsInserted = 0
                var acumulador = ""
              //  var oficios: MisOficios

                for (i in 0 until postList.size){

                  var Nombreoficios =  postList[i].nombreOficio
                    var idOficio =  postList[i].idOficio
                    var idTra =  postList[i].idoficio_trabajador
                 //   dataManager.InsertarOficios(oficios)
                    println("lista a insertar ${i+1},${Nombreoficios} idoficio $idOficio idTra $idTra")
                    itemsInserted
                    acumulador += postList[i].nombreOficio +" "
                }

                //getLocation(currentUser!!.uid.toString(), postList)
                val handlerThread = HandlerThread("ActualizarBaseEspacial")
                handlerThread.start()
                handler = Handler(handlerThread.looper)
                handler?.post({
                    ActualizarUbicacionBaseEspacial(currentUser!!.uid, postList)
                })
                println("latitud $latitud longitud $longitud")

                txt_oficios.text = acumulador
              //  dataManager.DatosDelUsuario(this@InterfazKerkly)
                setProgressDialog.dialog!!.dismiss()
            }
            override fun onFailure(call: Call<List<OficioKerkly?>?>, t: Throwable) {
                Toast.makeText(applicationContext, "Codigo de respuesta de error: $t", Toast.LENGTH_SHORT).show()
                setProgressDialog.dialog!!.dismiss()
            }
        })
    }
    override fun onBackPressed() {
        super.onBackPressed()
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            // setFragmentHome(telefono)
            showExitConfirmationDialog()
        }
        // finish()
    }

    private fun showExitConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Salir de la aplicación")
        builder.setMessage("¿Está seguro de que desea salir?")

        builder.setPositiveButton("Sí") { _, _ ->
            // Si el usuario selecciona "Sí", cierra la aplicación
            finishAffinity()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            // Si el usuario selecciona "No", cierra el diálogo
            dialog.dismiss()
        }

        val alertDialog = builder.create()
        alertDialog.show()
    }
    private fun cargarImagen(urlImagen: String) {
        val file: Uri
        file = Uri.parse(urlImagen)
        System.out.println("imagen aqui: "+ file)

        Picasso.get().load(urlImagen).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {

            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                val multi = MultiTransformation<Bitmap>(RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.ALL))
                Glide.with(this@InterfazKerkly).load(file)
                    .apply(RequestOptions.bitmapTransform(multi))
                    .into(ImageViewPerfil)
            }
            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                System.out.println("Respuesta error 3 "+ e.toString())
                //Toast.makeText(this@SolicitarServicio, "si hay foto respuesta 3", Toast.LENGTH_SHORT).show()
            }


        })
    }
    //metodos para la Autenticacion con cuenta de google
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
       // println("entro "+ currentUser!!.email)

        if (requestCode == MY_REQUEST_CODE) {
            currentUser = mAuth!!.currentUser
            sesion(telefonoKerkly, idKerkly)

            setProgressDialog.dialog!!.dismiss()
        }else{
            setProgressDialog.dialog!!.dismiss()
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
//        metodoSalir()
        // Verifica si los procesos ya se han ejecutado al volver a la actividad
        if (!procesosEjecutados) {
            // Ejecuta tus procesos una vez
        currentUser = mAuth!!.currentUser
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            if (currentUser != null) {
                println("telefono kerkkly $telefonoKerkly")
                setProgressDialog.dialog!!.dismiss()
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
                            Toast.makeText(this@InterfazKerkly, "ocurio un error", Toast.LENGTH_SHORT).show()
                            setProgressDialog.dialog!!.dismiss()
                        }else{
                            correo = postList[0].correo
                            curp = postList[0].correo
                            if (currentUser!!.email.toString() == correo){
                                dataManager.mostrarOficios(txt_oficios)
                                sesion(telefonoKerkly, idKerkly)
                              //  Toast.makeText(this@InterfazKerkly, "si son iguales", Toast.LENGTH_SHORT).show()
                                photoUrl = currentUser!!.photoUrl.toString()
                                correoKerkly = currentUser!!.email.toString()
                                name = currentUser!!.displayName.toString()
                                val foto = photoUrl.toString()
                                val nombre = postList[0].nombre
                                val ap = postList[0].ap
                                val am = postList[0].am
                                curp = postList[0].curp
                                val telefono = telefonoKerkly.toLong()
                                nombreKerkly = nombre
                                nombre_completo = "$nombre $ap $am"
                                direccionKerly = postList[0].Pais + " " + postList[0].Ciudad + " " + postList[0].Colonia + " " + postList[0].Calle
                                //obtenerDatosSql
                                Glide.with(this@InterfazKerkly)
                                    .asBitmap()
                                    .load(photoUrl)
                                    .into(object : SimpleTarget<Bitmap>() {
                                        override fun onResourceReady(bitmap: Bitmap, transition: Transition<in Bitmap>?) {
                                            val outputStream = ByteArrayOutputStream()
                                            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
                                            val photoByteArray = outputStream.toByteArray()
                                            val usuarios: usuariosSqlite
                                            usuarios = usuariosSqlite(telefono,photoByteArray, nombre!!,ap,am,correo)
                                            dataManager.verificarSiElUsarioExiste(this@InterfazKerkly,ImageViewPerfil,txt_nombre,txt_correo, photoByteArray,usuarios,telefono.toString(),nombre.toString(),ap,am,correo)

                                        }
                                    })

                                //obtenerToken
                                var firebaseMessaging = FirebaseMessaging.getInstance().subscribeToTopic("EnviarNoti")
                                firebaseMessaging.addOnCompleteListener {
                                    //Toast.makeText(this@MainActivityChats, "Registrado:", Toast.LENGTH_SHORT).show()
                                }
                                FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                                    if (!task.isSuccessful) {
                                        Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                                        return@OnCompleteListener
                                    }
                                    tokenKerkly = task.result
                                    val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
                                    val databaseReference = instancias.referenciaInformacionDelUsuarioKerkly(currentUser!!.uid)
                                    databaseReference.setValue(usuarios(currentUser!!.uid,telefonoKerkly, correoKerkly.toString(), name.toString(), foto.toString(), currentDateTimeString.toString(), tokenKerkly,curp)) { error, ref -> //txtprueba.setText(uid + "latitud " + latitud + " longitud " + longitud);
                                        // Toast.makeText(this@InterfazKerkly, "Bienvenido $token", Toast.LENGTH_SHORT) .show()
                                        setProgressDialog.dialog!!.dismiss()
                                    }
                                })

                                navegarAHome()

                            }else{
                                //Toast.makeText(this@InterfazKerkly, "este correo ${currentUser!!.email} no pertenece a esta cuenta", Toast.LENGTH_SHORT).show()
                                cerrarSesion()
                            }
                        }
                    }
                    override fun onFailure(call: Call<List<com.example.kerklytv2.modelo.serial.Kerkly?>?>, t: Throwable) {
                        Toast.makeText(applicationContext, "Codigo de respuesta de error: $t", Toast.LENGTH_SHORT).show()
                        setProgressDialog.dialog!!.dismiss()
                    }
                })
            }else{
                muestraOpciones()
            }
        } else {
            Toast.makeText(this@InterfazKerkly, "No hay conexion a Internet", Toast.LENGTH_LONG)
                .show()
            setProgressDialog.dialog!!.dismiss()
        }

            // Marca la variable como true para indicar que los procesos se han ejecutado
            procesosEjecutados = true
        }

    }
    fun showMessage(message: String){
        Toast.makeText(this,message,Toast.LENGTH_SHORT).show()
    }
    fun metodoSalir() {
        AuthUI.getInstance()
            .signOut(applicationContext)
            .addOnCompleteListener {

            }.addOnFailureListener { e ->
                Toast.makeText(
                    applicationContext, ""
                            + e.message, Toast.LENGTH_LONG
                ).show()
            }
        dataManager.deleteAllTablas()
    }

   fun ActualizarUbicacionBaseEspacial(uidKerkly:String, listaDeOficios: ArrayList<OficioKerkly>) {
      /*val latitud= 17.536558
       val longitud = -99.495811*/
       val miConexion = conexionPostgreSQL()
       val conexion = miConexion.obtenerConexion(this)
       println("conexion---> $conexion")
       if (conexion != null) {
           try {
               val idSeccion = miConexion.ObtenerSeccionCoordenadas(longitud, latitud)
               if (idSeccion == 0) {
                  // showMessage("no se encuentra dentro de una sección conocida")
                //  miConexion.offKerkly(curp)
                   fusedLocationClient?.removeLocationUpdates(locationCallback)
                   miConexion.cerrarConexion()
                   val delayMillis = 3000L // Retardo de 3 segundos
                   //Handler(Looper.getMainLooper()).postDelayed({
                       handler?.looper?.quitSafely()
                 //  }, delayMillis)
               } else {
                   println("mis Datos geometricos $latitud $longitud mis Datos geometricos $idSeccion")
                   showMessage("sección $idSeccion")
                   miConexion.insertOrUpdateSeccionKerkly(curp,idSeccion, uidKerkly, latitud,longitud, listaDeOficios)
                   // Resto del código aquí, incluyendo la inserción o actualización en la base de datos.
                   miConexion.cerrarConexion()
                   val delayMillis = 3000L // Retardo de 3 segundos
                   Handler(Looper.getMainLooper()).postDelayed({
                       handler?.looper?.quitSafely()
                   }, delayMillis)
                   fusedLocationClient?.removeLocationUpdates(locationCallback)
               }
           } catch (e: SQLException) {
               // Manejar la excepción (lanzar, notificar al usuario, etc.)
               println("Error al realizar operaciones de base de datos: ${e.message}")
               e.printStackTrace()
               val delayMillis = 3000L // Retardo de 3 segundos
               Handler(Looper.getMainLooper()).postDelayed({
                   handler?.looper?.quitSafely()
               }, delayMillis)
               fusedLocationClient?.removeLocationUpdates(locationCallback)
               // Otras acciones de manejo de errores según sea necesario
           }
       } else {
           // Manejar el caso en el que la conexión no se pudo establecer
           showMessage("problemas con la conexión BD Espacial")
           val delayMillis = 3000L // Retardo de 3 segundos
           Handler(Looper.getMainLooper()).postDelayed({
               handler?.looper?.quitSafely()
           }, delayMillis)
           fusedLocationClient?.removeLocationUpdates(locationCallback)
       }
   }
}