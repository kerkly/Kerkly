package com.example.kerklytv2

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.kerklytv2.Notificacion.llamartopico
import com.example.kerklytv2.databinding.ActivityMapsBinding
import com.example.kerklytv2.interfaces.AceptarServicioUrgente
import com.example.kerklytv2.interfaces.PresupuestoInterface
import com.example.kerklytv2.modelo.serial.modeloVerificarSolictud
import com.example.kerklytv2.modelo.usuarios
import com.example.kerklytv2.trazar_rutas.GPSTracker
import com.example.kerklytv2.trazar_rutas.GeoTask
import com.example.kerklytv2.trazar_rutas.Utils_k
import com.example.kerklytv2.url.Instancias
import com.example.kerklytv2.url.Url
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*


class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMarkerClickListener,  GeoTask.Geo {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    var latitud: Double = 0.0
    var longitud: Double = 0.0
    lateinit var context: Context
    private var locationManager: LocationManager? = null
    var location: Location? = null
    var gpsTracker: GPSTracker? = null
    var request: RequestQueue? = null
    var jsonObjectRequest: JsonObjectRequest? = null
    private lateinit var telefonoCliente: String
    private lateinit var telefonoKerkly: String
    private lateinit var b: Bundle
     var latitudFinal:Double = 0.0
    var longitudFinal: Double = 0.0
    lateinit var nombreCliente: String
    lateinit var nombrekerkly: String
    var minutos: Double = 0.0
    var distancia = 0
    private val llamartopico = llamartopico()
    private  var TipoServicio: String = ""
    private lateinit var folio : String
    private lateinit var correoCliente: String
    private lateinit var problema:String
    private lateinit var direccion:String
    private lateinit var Curp: String
    private lateinit var correoKerly: String
    private lateinit var direccionKerly: String
    private var locationPermissionGranted: Boolean = false
   lateinit var urlfoto: String
    lateinit var tokenCliente: String
    private lateinit var uidCliente:String
    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null
    private lateinit var instancias: Instancias
    private lateinit var Noti:String
    private var solicutdAceptada:Int = 0
    private var handler: Handler? = null
    private lateinit var btnMasIfo: MaterialButton
    private lateinit var fechaSolicitud:String
    private lateinit var nombreOficio:String
    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Registro de la Activity en EventBus
        if (intent != null && intent.extras != null) {
            b = intent.extras!!
        }

        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser
        instancias = Instancias()
        //recibimos las coordenas
        val latitudStr = b.getString("latitud")
        val longitudStr = b.getString("longitud")
        folio = b.getString("Folio").toString()
        if (!latitudStr.isNullOrEmpty() && !longitudStr.isNullOrEmpty()) {
            latitudFinal = latitudStr.toDouble()
            longitudFinal = longitudStr.toDouble()
            Log.d("maps --->", "folio $folio latitud recibida $latitudFinal, $longitudFinal")
        } else {
            Log.e("maps --->", "folio $folio Error: latitud o longitud nulas o vacías")
        }
        nombreCliente = b.getString("nombreCompletoCliente").toString()
        direccion = b.getString("direccion").toString()
        problema = b.getString("problema").toString()
        telefonoCliente = b.getString("telefonoCliente").toString()
        TipoServicio  = b.getString("tipoServicio").toString()

        Curp = b.getString("Curp").toString()
        telefonoKerkly = b.getString("telefonok").toString()
        correoCliente = b.getString("correoCliente").toString()
        correoKerly = b.getString("correoKerkly").toString()
        nombrekerkly = b.getString("nombreCompletoKerkly").toString()
        direccionKerly = b.getString("direccionkerkly").toString()
        uidCliente = b.getString("uidCliente").toString()
        fechaSolicitud = b.getString("fechaSolicitud").toString()
        nombreOficio = b.getString("nombreOficio").toString()
        obtenerToken(currentUser!!.uid, uidCliente)
        Noti = b.getString("Noti").toString()
        println("uidClienteMaps ----- > $folio  p $problema")
        context = this

        gpsTracker = GPSTracker(applicationContext)
        location = gpsTracker!!.location
        request = Volley.newRequestQueue(applicationContext)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION),
                PERMISSION_REQUEST_CODE
            )
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        getLocalizacion()

       val buttonAceptarServicio: MaterialButton
        buttonAceptarServicio = findViewById(R.id.buttonAceptarServicio)
        buttonAceptarServicio.setOnClickListener {
        // Toast.makeText(this, "click", Toast.LENGTH_SHORT).show()
        //pendiente
        //val i = Intent(this, MainActivityEnviarUbicacionEnTiempoReal::class.java)
        // startActivity(i)
        mostrarDialogoPersonalizado(minutos, distancia)
    }
        btnMasIfo = findViewById(R.id.buttonMasInfo)
        if (TipoServicio == "normal"){

            buttonAceptarServicio.text = "Presupuestar"
            btnMasIfo.isVisible = true
        }

        btnMasIfo.setOnClickListener {
            val intent = Intent(this@MapsActivity, MainActivityChats::class.java)
            intent.putExtra("nombreCompletoCliente", nombreCliente)
            intent.putExtra("correoCliente", correoCliente)
            intent.putExtra("telefonoCliente",telefonoCliente)
            intent.putExtra("telefonoKerkly", telefonoKerkly)
            intent.putExtra("urlFotoCliente", urlfoto)
            intent.putExtra("nombreCompletoKerkly", nombrekerkly)
            intent.putExtra("tokenCliente", tokenCliente)
            intent.putExtra("directoMaps","Maps")
            intent.putExtra("uidCliente",uidCliente)
            intent.putExtra("uidKerkly",currentUser!!.uid)
            startActivityForResult(intent,10)
        }
    val buttonCancelarServicio : MaterialButton
    buttonCancelarServicio = findViewById(R.id.buttonCancelarServicio)
    buttonCancelarServicio.setOnClickListener{
        mostrarDialogRechazar()
    }

        val res = VerificarSolicitud(folio)
        if (res == 1){
            showMensaje("Lo sentimos pero la solicutud ya ha sido aceptada")
            buttonAceptarServicio.isEnabled = false
            //buttonCancelarServicio.isEnabled = false
            buttonCancelarServicio.text ="Regresar"
        }

    }
    override fun setDouble(min: String?) {
        val res = min!!.split(",").toTypedArray()
        minutos = res[0].toDouble() / 60
        distancia = res[1].toInt() / 1000
        mostrarDialogoPersonalizado(minutos, distancia)
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true
                    onMapReady(mMap)
                    // Puedes realizar las operaciones relacionadas con la ubicación aquí
                    // ...
                } else {
                    // Permiso de ubicación denegado
                    locationPermissionGranted = false
                    // Puedes mostrar un mensaje o realizar alguna acción adicional
                    // ...
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun mostrarDialogRechazar() {
        val builder = AlertDialog.Builder(this@MapsActivity)
        val inflater = layoutInflater
        val view: android.view.View? = inflater.inflate(R.layout.dialog_rechazar_servicio, null)
        builder.setView(view)
        val dialog = builder.create()
        dialog.show()
        val txt: TextView = view!!.findViewById(R.id.text_dialog)
        txt.text = "¿Seguro que deseas Rechazar el servicio?"
        val btnAceptar: Button = view!!.findViewById(R.id.btnRechazar)
        btnAceptar.setOnClickListener(object : android.view.View.OnClickListener{

            override fun onClick(p0: android.view.View?) {
                //Toast.makeText(applicationContext, "Rechazado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
                finishActivity(1)
                val intent  = Intent(this@MapsActivity, PantallaInicio::class.java)
                startActivity(intent)
            }


        })

        val btnRechazar: Button = view!!.findViewById(R.id.btncancelar1)
        btnRechazar.setOnClickListener(object: android.view.View.OnClickListener{
            override fun onClick(p0: android.view.View?) {
                Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        })
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        val constructor = LatLngBounds.Builder()
        mMap = googleMap
        // Verificar los permisos de ubicación en tiempo de ejecución
        if (locationPermissionGranted) {
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val miUbicacion = LatLng(location.getLatitude(), location.getLongitude())
                latitud = location.latitude
                longitud = location.longitude
                locationManager.removeUpdates(this)
                mMap.setMyLocationEnabled(true)
                mMap.isMyLocationEnabled = true
                mMap.moveCamera(CameraUpdateFactory.newLatLng(miUbicacion))
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                val cameraPosition = CameraPosition.Builder()
                    .target(miUbicacion)
                    .zoom(20f)
                    .bearing(90f)
                    .tilt(45f)
                    .build()
                // mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))

                constructor.include(miUbicacion)

                val lat: Double
                val lon: Double
                lat = latitudFinal.toDouble()
                lon = longitudFinal.toDouble()

                val position = LatLng(lat, lon)
                //TrazarLineas(position)
                constructor.include(position)
                val limites = constructor.build()

                val ancho = resources.displayMetrics.widthPixels
                val alto = resources.displayMetrics.heightPixels
                val padding = (alto * 0.100).toInt() // 25% de espacio (padding) superior e inferior

                val centrarmarcadores = CameraUpdateFactory.newLatLngBounds(limites, ancho, alto, padding)

                mMap.animateCamera(centrarmarcadores)

            }
            override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
                when (status) {
                    LocationProvider.AVAILABLE -> Log.d("debug", "LocationProvider.AVAILABLE")
                    LocationProvider.OUT_OF_SERVICE -> Log.d("debug", "LocationProvider.OUT_OF_SERVICE")
                    LocationProvider.TEMPORARILY_UNAVAILABLE -> Log.d("debug", "LocationProvider.TEMPORARILY_UNAVAILABLE")
                }
            }
            override fun onProviderEnabled(provider: String) {
                Toast.makeText(context, "GPS activado", Toast.LENGTH_SHORT).show()
            }
            override fun onProviderDisabled(provider: String) {
                Toast.makeText(context, "GPS Desactivado", Toast.LENGTH_SHORT).show()
            }
        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)

        //los marcadores
        Utils_k.Marcador(mMap, applicationContext, latitudFinal, longitudFinal, problema,nombreCliente,folio)
        mMap.setOnMapLongClickListener(this)
        mMap.setOnMarkerClickListener(this)
            //if(TipoServicio == "ServicioNR"){

           // }else{

           // }
        }
    }
    override fun onMapLongClick(p0: LatLng) {
        Utils_k.coordenadas.origenLat = p0.latitude
        Utils_k.coordenadas.origenLongi = p0.longitude
    }
    override fun onMarkerClick(p0: Marker): Boolean {
       // AlertShow(p0.title, p0.position)
        return false
    }
    private fun getLocalizacion() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!gpsEnabled) {
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1000)
            onMapReady(mMap)
        }
    }

    private fun AlertShowDespues(latFinal: Double, longFinal:Double) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Desea usar Google Maps o chatear Con el cliente?")
        builder.setTitle(nombrekerkly)
        builder.setCancelable(false)
        builder.setPositiveButton("Google Mapas") { dialog, which ->
           // TrazarLineas(position)
            //System.out.println(" " +(minutos/60).toInt() + " hr " + (minutos % 60).toInt() + " mins")
            val usuarioLatLng = LatLng(latitud, longitud)
            val marcadorLatLng = LatLng(latFinal, longFinal)
            // Crear una URL con las coordenadas
            val uri = "http://maps.google.com/maps?saddr=${usuarioLatLng.latitude},${usuarioLatLng.longitude}&daddr=${marcadorLatLng.latitude},${marcadorLatLng.longitude}"
            // Crear un Intent con la acción para abrir Google Maps
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(uri))
            // Establecer la aplicación de Google Maps como el selector de aplicaciones
            intent.setPackage("com.google.android.apps.maps")
            // Verificar si Google Maps está instalado en el dispositivo
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
                finish()
            } else {
                // Manejar el caso en que Google Maps no esté instalado en el dispositivo
                // Puedes mostrar un mensaje de error o proporcionar una alternativa
                showMensaje("Verifica Si tienes Google Maps Instalado")
            }
        }
        builder.setNegativeButton("Chatear con el cliente") { dialog, which ->
            dialog.cancel()

            val intent = Intent(this@MapsActivity, MainActivityChats::class.java)
            intent.putExtra("nombreCompletoCliente", nombreCliente)
            intent.putExtra("correoCliente", correoCliente)
            intent.putExtra("telefonoCliente",telefonoCliente)
            intent.putExtra("telefonoKerkly", telefonoKerkly)
            intent.putExtra("urlFotoCliente", urlfoto)
            intent.putExtra("nombreCompletoKerkly", nombrekerkly)
            intent.putExtra("tokenCliente", tokenCliente)
            intent.putExtra("directoMaps","Maps")
            intent.putExtra("uidCliente",uidCliente)
            intent.putExtra("uidKerkly",currentUser!!.uid)
            startActivityForResult(intent,10)
            finish()
        }
        val alertDialog = builder.create()
        alertDialog.show()


    }


    private fun decodePoly(encoded: String): List<LatLng> {
        val poly: MutableList<LatLng> = ArrayList()
        var index = 0
        val len = encoded.length
        var lat = 0
        var lng = 0
        while (index < len) {
            var b: Int
            var shift = 0
            var result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat
            shift = 0
            result = 0
            do {
                b = encoded[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng
            val p = LatLng(
                lat.toDouble() / 1E5,
                lng.toDouble() / 1E5
            )
            poly.add(p)
        }
        return poly
    }

     fun mostrarDialogoPersonalizado(min: Double, dist: Int) {
        val builder = AlertDialog.Builder(this@MapsActivity)
        val inflater = layoutInflater
        val view: android.view.View? = inflater.inflate(R.layout.dialog_aceptar_servicio, null)
        builder.setView(view)

       val dialog = builder.create()
        dialog.show()
         val textView_result1: TextView = view!!.findViewById(R.id.textView_result1)
         val textView_result2: TextView = view.findViewById(R.id.textView_result2)
         //textView_result1.setText(" " +(minutos / 60).toInt() + " hr " + (minutos % 60).toInt() + " mins")
        //  textView_result2.setText("$distancia kilometros")

         textView_result1.setText("$nombreCliente")
         textView_result2.setText(" folio $folio, $problema")
       val btnAceptar: Button = view!!.findViewById(R.id.btnAceptar)
        btnAceptar.setOnClickListener(object : android.view.View.OnClickListener{

            override fun onClick(p0: android.view.View?) {
                //Toast.makeText(applicationContext, "Aceptado $latitud, $longitud", Toast.LENGTH_SHORT).show()
                if (TipoServicio == "normal"){
                    val i = Intent(context, Presupuesto::class.java)
                    i.putExtra("telefonoCliente", telefonoCliente)
                    i.putExtra("telefonok", telefonoKerkly)
                    i.putExtra("latitud", latitudFinal.toString())
                    i.putExtra("longitud", longitudFinal.toString())
                    i.putExtra("nombreCompletoCliente", nombreCliente)
                    i.putExtra("nombreCompletoKerkly", nombrekerkly)
                    i.putExtra("problema", problema)
                    i.putExtra("direccion", direccion)
                    i.putExtra("correoCliente", correoCliente)
                    i.putExtra("Folio", folio)
                    i.putExtra("tipoServicio", "normal")
                    i.putExtra("Curp", Curp)
                    i.putExtra("correoKerly", correoKerly)
                    i.putExtra("direccionkerkly", direccionKerly)
                    i.putExtra("uidCliente",uidCliente)
                    i.putExtra("uidKerkly",currentUser!!.uid)
                    i.putExtra("fechaSolicitud",fechaSolicitud)
                    i.putExtra("nombreOficio",nombreOficio)
                    println("folio---> $folio")
                    startActivity(i)
                    dialog.dismiss()
                }
                if(TipoServicio == "urgente"){
                    dialog.dismiss()
                   AceptarServicio(folio)
                    //obtenerToken(currentUser!!.uid,uidCliente)
                   // obtenerToken(currentUser!!.uid, uidCliente)
                }
                if(TipoServicio == "ServicioNR"){
                    val intent = Intent(this@MapsActivity, Presupuesto::class.java)
                    intent.putExtra("telefonoCliente", telefonoCliente)
                    intent.putExtra("telefonok", telefonoKerkly)
                    intent.putExtra("latitud", latitudFinal)
                    intent.putExtra("longitud", longitudFinal)
                    intent.putExtra("nombreCompletoCliente", nombreCliente)
                    intent.putExtra("nombreCompletoKerkly", nombrekerkly)
                    intent.putExtra("problema", problema)
                    intent.putExtra("direccion", direccion)
                    intent.putExtra("correoCliente", correoCliente)
                    intent.putExtra("Folio", folio)
                    intent.putExtra("tipoServicio", "ServicioNR")
                    intent.putExtra("Curp", Curp)
                    intent.putExtra("correoKerly", correoKerly)
                    intent.putExtra("direccionKerly", direccionKerly)
                    intent.putExtra("uidCliente",uidCliente)
                    intent.putExtra("uidKerkly",currentUser!!.uid)
                    startActivity(intent)
                    dialog.dismiss()
                    finish()
                }

            }

        })
         val btnRechazar: Button = view!!.findViewById(R.id.btnCancel)
         btnRechazar.setOnClickListener(object: android.view.View.OnClickListener{
             override fun onClick(p0: android.view.View?) {
                // Toast.makeText(applicationContext, "Cancelado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
             }
         })
    }

    private fun AceptarServicio(folio: String) {
        val ROOT_URL = Url().URL
        val adapter = RestAdapter.Builder()
            .setEndpoint(ROOT_URL)
            .build()
        val api: AceptarServicioUrgente = adapter.create(AceptarServicioUrgente::class.java)
        api.AceptarServicio(folio.toString(), Curp,
        object : Callback<Response?>{
            override fun success(t: Response?, response: Response?) {
              var entrada: BufferedReader? = null
                var Res = ""
                try{
                    entrada = BufferedReader(InputStreamReader(t?.body?.`in`()))
                    Res = entrada.readLine()
                  //  showMensaje("respuesta $Res")
                    if(Res == "1"){
                        //obtenerToken(currentUser!!.uid,uidCliente)
                        verificarDatoNoExistente(currentUser!!.uid, uidCliente)
                       llamartopico.llamarTopicAceptarSolicitudUrgente(this@MapsActivity, folio, tokenCliente,
                           "Su solicitud ha sido Aceptada, Por favor espere un momento... ","Mensaje de $nombrekerkly","urgente",telefonoCliente, nombreCliente
                            ,uidCliente,fechaSolicitud,problema, "0.0",nombreOficio,
                           telefonoKerkly,nombrekerkly,direccionKerly,currentUser!!.email.toString(),
                           currentUser!!.uid.toString())

                    }else{
                      //  showMensaje("El Servicio No se Pudo completar $Res")
                    }
                }catch (e: java.lang.Exception){
                    e.printStackTrace()
                    showMensaje("Error! ${e!!.message}")
                }

            }
            override fun failure(error: RetrofitError?) {
               showMensaje("Error 606! ${error!!.message}")
            }
        })
    }
    @SuppressLint("SuspiciousIndentation")
    private fun obtenerToken(uidKerkly:String, uidCliente: String){
       // databaseUsu = firebase_databaseUsu.getReference("UsuariosR").child(telefonoCliente.toString()).child("MisDatos")
      val reference =  instancias.referenciaInformacionDelCliente(uidCliente)
        reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val u2 = snapshot.getValue(usuarios::class.java)
                //telefonoCliente =  u2!!.telefono.trim()
                urlfoto =  u2!!.foto.trim()
                val nombreCliente =  u2!!.nombre.trim()
                val correoCliente =  u2!!.correo.trim()
                tokenCliente = u2!!.token.trim()
                // Toast.makeText(this@MapsActivity,"$nombreCliente",Toast.LENGTH_SHORT).show()
                //enviar Notificacion
                //   agregarContacto(telefonoCliente, telefonoKerkly)
            }
            override fun onCancelled(error: DatabaseError) {
                showMensaje("Erorr! ${error.message}")
            }
        })
    }

    fun verificarDatoNoExistente(uidKerkl: String, uidCliente: String) {
        val reference = instancias.referenciaListaDeUsuariosKerkly(uidKerkl)
        // val reference = database.reference.child("UsuariosR").child(telefonoKerkly).child("Lista de Usuarios")
        val query = reference.orderByChild("uid").equalTo(uidCliente)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                println(dataSnapshot.child("uid").value)
                if (!dataSnapshot.exists()) {
                   AlertShowDespues(latitudFinal.toDouble(),longitudFinal.toDouble())
                    agregarContacto(uidKerkl, uidCliente)
                }else{
                    // showMensaje("ya existe el contacto")
                   // llamartopico.llamartopico(this@MapsActivity, tokenCliente, "Su Solicitud a Sido Aceptada, Por favor espere un momento...", nombrekerkly)
                    AlertShowDespues(latitudFinal.toDouble(),longitudFinal.toDouble())
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Ocurrió un error al realizar la consulta
                println("Error al consultar la base de datos: ${databaseError.message}")
                showMensaje("Error! ${databaseError.message}")
            }
        })
    }

  fun showMensaje(mensaje:String){
      Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show()
  }

    private fun agregarContacto(uidKerkly: String, uidCliente: String) {
        //Primero agregamos numeros  a la lista de usuarios
        var databaseReferenceCliente = instancias.referenciaListaDeUsuariosCliente(uidCliente)
        var databaseReferencekerkly = instancias.referenciaListaDeUsuariosKerkly(uidKerkly)
        databaseReferenceCliente.push().child("uid").setValue(uidKerkly)
        databaseReferencekerkly.push().child("uid").setValue(uidCliente)
      //  showMensaje("Agregado los datos ")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (Noti == "Noti"){
            val intent = Intent(this, PantallaInicio::class.java)
            intent.putExtra("numT", telefonoKerkly)
            startActivity(intent)
            finish()
        }
        finish()
    }

    private fun VerificarSolicitud(idPresupuesto: String) : Int{
        val ROOT_URL = Url().URL
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("$ROOT_URL/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val presupuestoGET = retrofit.create(PresupuestoInterface::class.java)
        val call = presupuestoGET.getVerificarSolictudUrgente(idPresupuesto)

        call?.enqueue(object : retrofit2.Callback<List<modeloVerificarSolictud?>?> {

            override fun onResponse(
                call: Call<List<modeloVerificarSolictud?>?>,
                response: retrofit2.Response<List<modeloVerificarSolictud?>?>
            ) {
                if (response.isSuccessful) {
                    val postList = response.body()

                    // Verificar si la lista no es nula y contiene elementos
                    if (postList != null && postList.isNotEmpty()) {
                        // Iterar sobre la lista para obtener los datos
                        for (sol: modeloVerificarSolictud? in postList) {
                            // Aquí puedes trabajar con los datos del presupuesto
                            val idPresupuesto = sol?.idPresupuesto
                             solicutdAceptada = sol?.aceptoK!!.toInt()
                            // ...
                        }
                    } else {
                        // La lista está vacía, manejar según sea necesario
                      //  showMensaje("La lista de presupuestos está vacía.")
                    }
                } else {
                    // La solicitud no fue exitosa, manejar según sea necesario
                    showMensaje("Error en la solicitud. Código: ${response.code()}")
                }
            }

            override fun onFailure(
                call: Call<List<modeloVerificarSolictud?>?>,
                t: Throwable
            ) {
                showMensaje("error.. $t")
            }
        })
        return solicutdAceptada
    }

    override fun onDestroy() {
        super.onDestroy()
        // Desregistro de la Activity en EventBus al destruirse
       // EventBus.getDefault().unregister(this)
        // showMensaje("actitvy destruida")
        latitudFinal = 0.0
        longitudFinal =0.0
    }


}
