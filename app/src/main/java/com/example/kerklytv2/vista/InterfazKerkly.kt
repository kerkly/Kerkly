package com.example.kerklytv2.vista
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
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
import com.example.kerklytv2.R
import com.example.kerklytv2.clases.NetworkSpeedChecker
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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
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
    private lateinit var ImageViewPerfil : ImageView
    private lateinit var direccionKerly: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_interfaz_kerkly)
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        setProgressDialog.setProgressDialog(this)
        drawerLayout = findViewById(R.id.drawer_layout)
        val navView: NavigationView = findViewById(R.id.nav_view)
        val navController = findNavController(R.id.nav_host_fragment_content_interfaz_kerkly)

        NetworkSpeedChecker(this)
        val view = navView.getHeaderView(0)
        txt_correo = view.findViewById(R.id.correo_header)
        txt_nombre = view.findViewById(R.id.nombre_header)
        txt_oficios = view.findViewById(R.id.oficios_header)

        ImageViewPerfil = view.findViewById(R.id.imageViewPerfil)
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
    }

    fun <T> join(first: Array<T>, second: Array<T>): Array<T> {
        return first.plus(second)
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
    }
    private fun setFragmentcontactos() {
        val b2 = Bundle()
        val f = ContactosFragment()
        f.arguments = b2
        b2!!.putString("telefonoKerkly", telefonoKerkly)
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
        args.putString("correoKerly", correoKerkly)
        args.putString("direccionkerkly", direccionKerly)
        println("------> direccion kerkly $direccionKerly ")
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
                call: Call<List<OficioKerkly?>?>, response: retrofit2.Response<List<OficioKerkly?>?>) {
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
                setProgressDialog.dialog!!.dismiss()
            }
        })
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            //finish()
            setFragmentHome()
           /* val alert: AlertDialog.Builder = AlertDialog.Builder(this)
            alert.setTitle(getString(R.string.cerrar_app))
            alert.setMessage(getString(R.string.mensaje_alertaCerrarApp))
            alert.setCancelable(false)
            alert.setPositiveButton(getString(R.string.confirmar_alertCerrarApp)) {
                    dialogo1, id -> finish() }
            alert.setNegativeButton(getString(R.string.cancelar_alertCerrarApp)) { dialogo1, id -> dialogo1.dismiss() }
            alert.show()*/
        }
    }

    private fun getKerkly(foto:String) {
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
                    val nombre = postList[0].nombre
                    val ap = postList[0].ap
                    val am = postList[0].am
                    curp = postList[0].curp
                    nombreKerkly = nombre
                    nombre_completo = "$nombre $ap $am"

                    txt_nombre.text = nombre_completo
                    txt_correo.text = correo
                    direccionKerly = postList[0].Pais.trim() + " " + postList[0].Ciudad.trim() + " " + postList[0].Colonia.trim() + " " + postList[0].Calle.trim()
                        //cargarImagen(foto)
                        getOficiosKerkly()
                        setProgressDialog.dialog!!.dismiss()
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
                setProgressDialog.dialog!!.dismiss()
            }
        })
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
            setProgressDialog.dialog!!.dismiss()
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
                        val nombre = postList[0].nombre
                        val ap = postList[0].ap
                        val am = postList[0].am
                        curp = postList[0].curp
                        nombreKerkly = nombre
                        nombre_completo = "$nombre $ap $am"

                        txt_nombre.text = nombre_completo
                        txt_correo.text = correo
                        direccionKerly = postList[0].Pais + " " + postList[0].Ciudad + " " + postList[0].Colonia + " " + postList[0].Calle

                        if(currentUser!!.email == correo){
                           // Toast.makeText(applicationContext,"Todo Bien si es el correo", Toast.LENGTH_SHORT).show()
                            photoUrl = currentUser!!.photoUrl.toString()
                            correoKerkly = currentUser!!.email.toString()
                            name = currentUser!!.displayName.toString()

                                cargarImagen(photoUrl)
                                Log.d("correo", correo)

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
                                token = task.result
                                // Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()

                                // Toast.makeText(MainActivity.this, "demtro onStart usauru " +  name, Toast.LENGTH_LONG).show();
                                val database = FirebaseDatabase.getInstance()
                                val databaseReference = database.getReference("UsuariosR").child("$telefonoKerkly")
                                val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
                                //val usuario = usuarios()
                                //val u = usuarios(uid, email, name, foto, currentDateTimeString)
                                databaseReference.child("MisDatos").setValue(usuarios(telefonoKerkly, correoKerkly.toString(), name.toString(), photoUrl.toString(), currentDateTimeString.toString(), token)) { error, ref -> //txtprueba.setText(uid + "latitud " + latitud + " longitud " + longitud);
                                    // Toast.makeText(this@InterfazKerkly, "Bienvenido $token", Toast.LENGTH_SHORT) .show()
                                   // getKerkly(foto)
                                    getOficiosKerkly()
                                    setProgressDialog.dialog!!.dismiss()
                                }
                            })

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
        currentUser = mAuth!!.currentUser
        val user = FirebaseAuth.getInstance().currentUser
        val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo = connectivityManager.activeNetworkInfo
        if (networkInfo != null && networkInfo.isConnected) {
            if (currentUser != null) {
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
                              //  Toast.makeText(this@InterfazKerkly, "si son iguales", Toast.LENGTH_SHORT).show()
                                photoUrl = currentUser!!.photoUrl.toString()
                                correoKerkly = currentUser!!.email.toString()
                                name = currentUser!!.displayName.toString()
                                val foto = photoUrl.toString()
                                cargarImagen(foto)
                                Log.d("correo", correo)

                                val nombre = postList[0].nombre
                                val ap = postList[0].ap
                                val am = postList[0].am
                                curp = postList[0].curp
                                nombreKerkly = nombre
                                nombre_completo = "$nombre $ap $am"

                                txt_nombre.text = nombre_completo
                                txt_correo.text = correo
                                direccionKerly = postList[0].Pais + " " + postList[0].Ciudad + " " + postList[0].Colonia + " " + postList[0].Calle


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
                                    token = task.result
                                    // Toast.makeText(baseContext, token, Toast.LENGTH_SHORT).show()

                                    // Toast.makeText(MainActivity.this, "demtro onStart usauru " +  name, Toast.LENGTH_LONG).show();
                                    val database = FirebaseDatabase.getInstance()
                                    val databaseReference = database.getReference("UsuariosR").child("$telefonoKerkly")
                                    val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
                                    //val usuario = usuarios()
                                    //val u = usuarios(uid, email, name, foto, currentDateTimeString)
                                    databaseReference.child("MisDatos").setValue(usuarios(telefonoKerkly, correoKerkly.toString(), name.toString(), foto.toString(), currentDateTimeString.toString(), token)) { error, ref -> //txtprueba.setText(uid + "latitud " + latitud + " longitud " + longitud);
                                        // Toast.makeText(this@InterfazKerkly, "Bienvenido $token", Toast.LENGTH_SHORT) .show()
                                        // getKerkly(foto)
                                        getOficiosKerkly()
                                        setProgressDialog.dialog!!.dismiss()
                                    }
                                })
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
    }

}