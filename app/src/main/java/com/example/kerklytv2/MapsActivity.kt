package com.example.kerklytv2

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.kerklytv2.Notificacion.llamartopico
import com.example.kerklytv2.databinding.ActivityMapsBinding
import com.example.kerklytv2.interfaces.AceptarServicioUrgente
import com.example.kerklytv2.modelo.usuarios
import com.example.kerklytv2.trazar_rutas.GPSTracker
import com.example.kerklytv2.trazar_rutas.GeoTask
import com.example.kerklytv2.trazar_rutas.Utils_k
import com.example.kerklytv2.url.Url
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.button.MaterialButton
import com.google.firebase.database.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.Response
import java.io.BufferedReader
import java.io.InputStreamReader


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
    lateinit var latitud2: String
    lateinit var longitud2: String
    lateinit var nombreCliente: String
    lateinit var nombrekerkly: String
    var minutos: Double = 0.0
    var distancia = 0
    private lateinit var firebase_databaseUsu: FirebaseDatabase
    private lateinit var databaseUsu: DatabaseReference
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
    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        b = intent.extras!!
        //recibimos las coordenas
        telefonoCliente = b.getString("telefonoCliente").toString()
        telefonoKerkly = b.getString("telefonok").toString()

        latitud2 = b.getString("latitud").toString()
        longitud2 = b.getString("longitud").toString()
        nombreCliente = b.getString("nombreCompletoCliente").toString()
        nombrekerkly = b.getString("nombreCompletoKerkly").toString()
        problema = b.getString("problema").toString()
        direccion = b.getString("direccion").toString()
        correoCliente = b.getString("correoCliente").toString()
        folio = b.getString("Folio").toString()
         TipoServicio  = b.getString("tipoServicio").toString()
        Curp = b.getString("Curp").toString()
        correoKerly = b.getString("correoKerly").toString()
        direccionKerly = b.getString("direccionkerkly").toString()
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
    val buttonCancelarServicio : MaterialButton
    buttonCancelarServicio = findViewById(R.id.buttonCancelarServicio)
    buttonCancelarServicio.setOnClickListener{
        mostrarDialogRechazar()
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
                lat = latitud2.toDouble()
                lon = longitud2.toDouble()

                val position = LatLng(lat, lon)
                TrazarLineas(position)
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
        Utils_k.Marcador(mMap, applicationContext, latitud2, longitud2, nombrekerkly)
        mMap.setOnMapLongClickListener(this)
        mMap.setOnMarkerClickListener(this)
            obtenerToken(telefonoCliente,telefonoKerkly)
        }
    }
    override fun onMapLongClick(p0: LatLng) {
        Utils_k.coordenadas.origenLat = p0.latitude
        Utils_k.coordenadas.origenLongi = p0.longitude
    }
    override fun onMarkerClick(p0: Marker): Boolean {
        AlertShow(p0.title, p0.position)
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

    private fun AlertShow(title: String?, position: LatLng) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage("Desea ir  adonde $nombreCliente?")
        builder.setTitle(title)
        builder.setCancelable(false)
        builder.setPositiveButton("Si") { dialog, which ->
            TrazarLineas(position)
            System.out.println(" " +(minutos/60).toInt() + " hr " + (minutos % 60).toInt() + " mins")
        }
        builder.setNegativeButton("No") { dialog, which -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun TrazarLineas(latLng: LatLng){
        Utils_k.coordenadas.destinoLat = latLng.latitude
        Utils_k.coordenadas.destinoLng = latLng.longitude
        try {
            while (location == null) {
                location = gpsTracker!!.location
            }
            location = gpsTracker!!.location
            Utils_k.coordenadas.origenLat = location!!.latitude
            Utils_k.coordenadas.origenLongi = location!!.longitude
            AsignarRuta(
                Utils_k.coordenadas.origenLat.toString(),
                Utils_k.coordenadas.origenLongi.toString(),
                Utils_k.coordenadas.destinoLat.toString(),
                Utils_k.coordenadas.destinoLng.toString()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun AsignarRuta(latInicial: String, lngInicial: String, latFinal: String, lngFinal: String) {
        val url = "https://maps.googleapis.com/maps/api/directions/json?origin=$latInicial,$lngInicial&destination=$latFinal,$lngFinal&key=AIzaSyD9i-yAGqAoYnIcm8KcMeZ0nsHyiQxl_mo&mode=drive"
        jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null, { response ->
            var jRoutes: JSONArray? = null
            var jLegs: JSONArray? = null
            var jSteps: JSONArray? = null
            try {
                jRoutes = response.getJSONArray("routes")
                for (i in 0 until jRoutes.length()) {
                    jLegs = (jRoutes[i] as JSONObject).getJSONArray("legs")
                    val path: MutableList<HashMap<String, String>> = java.util.ArrayList()
                    for (j in 0 until jLegs.length()) {
                        jSteps = (jLegs[j] as JSONObject).getJSONArray("steps")
                        for (k in 0 until jSteps.length()) {
                            var polyline = ""
                            polyline = ((jSteps[k] as JSONObject)["polyline"] as JSONObject)["points"] as String
                            val list = decodePoly(polyline)
                            for (l in list.indices) {
                                val hm = HashMap<String, String>()
                                hm["lat"] = java.lang.Double.toString(list[l].latitude)
                                hm["lng"] = java.lang.Double.toString(list[l].longitude)
                                path.add(hm)
                            }
                        }
                        Utils_k.routes.add(path)
                        //Toast.makeText(this, "hola", Toast.LENGTH_LONG).show()
                        Utils_k.Marcador(mMap, applicationContext, latitud2, longitud2, nombreCliente)
                        var points: java.util.ArrayList<LatLng?>? = null
                        var lineOptions: PolylineOptions? = null

                        for (i in 0 until Utils_k.routes.size) {
                            points = java.util.ArrayList()
                            lineOptions = PolylineOptions()
                            // Obteniendo el detalle de la ruta
                            val path: List<HashMap<String, String>> = Utils_k.routes.get(i)
                            for (j in path.indices) {
                                val point = path[j]
                                val lat = Objects.requireNonNull(point["lat"])!!.toDouble()
                                val lng = Objects.requireNonNull(point["lng"])!!.toDouble()
                                val position = LatLng(lat, lng)
                                points.add(position)
                            }
                            lineOptions.addAll(points)
                            lineOptions.width(9f)
                            //Definimos el color de la Polilíneas
                            lineOptions.color(Color.BLUE)
                        }
                        assert(lineOptions != null)
                        mMap.addPolyline(lineOptions!!)
                    }
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: java.lang.Exception) {
            }
        }) { error ->
            Toast.makeText(applicationContext, "No se puede conectar $error", Toast.LENGTH_LONG).show()
        }
        request!!.add(jsonObjectRequest)
        val url2 = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=$latInicial,$lngInicial&destinations=$latFinal,$lngFinal&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyD9i-yAGqAoYnIcm8KcMeZ0nsHyiQxl_mo"
        GeoTask(this).execute(url2)
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
         textView_result1.setText(" " +(minutos / 60).toInt() + " hr " + (minutos % 60).toInt() + " mins")
          textView_result2.setText("$distancia kilometros")


       val btnAceptar: Button = view!!.findViewById(R.id.btnAceptar)
        btnAceptar.setOnClickListener(object : android.view.View.OnClickListener{

            override fun onClick(p0: android.view.View?) {
                //Toast.makeText(applicationContext, "Aceptado $latitud, $longitud", Toast.LENGTH_SHORT).show()
                if (TipoServicio == "normal"){
                    val i = Intent(context, Presupuesto::class.java)
                    i.putExtra("telefonoCliente", telefonoCliente)
                    i.putExtra("telefonok", telefonoKerkly)
                    i.putExtra("latitud", latitud2)
                    i.putExtra("longitud", longitud2)
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
                    startActivity(i)
                    dialog.dismiss()
                }
                if(TipoServicio == "urgente"){
                    dialog.dismiss()
                    AceptarServicio()
                }
                if(TipoServicio == "clienteNoRegistrado"){
                    val i = Intent(context, Presupuesto::class.java)
                    i.putExtra("latitud", latitud2)
                    i.putExtra("longitud", longitud2)
                    i.putExtra("Folio", folio)
                    i.putExtra("clientenombre", nombreCliente)
                    i.putExtra("Dirección", direccion)
                    i.putExtra("problemacliente", problema)
                    i.putExtra("nombreCompletoKerkly", nombrekerkly)
                    i.putExtra("numerocliente", telefonoCliente)
                    i.putExtra("tipoServicio", "clienteNoRegistrado")
                    i.putExtra("telefonok", telefonoKerkly)
                    i.putExtra("correoCliente", correoCliente)
                    i.putExtra("direccionkerkly", direccionKerly)
                    startActivity(i)
                    dialog.dismiss()
                }

                if(TipoServicio == "ServicioNR"){
                    val intent = Intent(this@MapsActivity, Presupuesto::class.java)
                    intent.putExtra("telefonoCliente", telefonoCliente)
                    intent.putExtra("telefonok", telefonoKerkly)
                    intent.putExtra("latitud", latitud2)
                    intent.putExtra("longitud", longitud2)
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
    fun verificarDatoNoExistente(numeroCliente: String, numeroKerkly: String) {
        val database = FirebaseDatabase.getInstance()
        val reference = database.reference.child("UsuariosR").child(telefonoKerkly).child("Lista de Usuarios")
        val query = reference.orderByChild("telefono").equalTo(telefonoCliente)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
              println(dataSnapshot.child("telefono").value)
                if (!dataSnapshot.exists()) {
                    // El dato no existe en la base de datos
                    //println("El dato no se encuentra en la base de datos "+ dataSnapshot.value)
                    agregarContacto(telefonoCliente, telefonoKerkly)
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                // Ocurrió un error al realizar la consulta
                println("Error al consultar la base de datos: ${databaseError.message}")
            }
        })
    }

    private fun agregarContacto(numeroCliente: String, numeroKerkly: String) {
        //Primero agregamos numeros  a la lista de usuarios
        var firebaseDatabaseLista: FirebaseDatabase
        var databaseReferenceCliente: DatabaseReference
        var databaseReferencekerkly: DatabaseReference

        firebaseDatabaseLista = FirebaseDatabase.getInstance()
        databaseReferenceCliente = firebaseDatabaseLista.getReference("UsuariosR").child(numeroCliente)
            .child("Lista de Usuarios")
        databaseReferenceCliente.push().child("telefono").setValue(numeroKerkly)

        databaseReferencekerkly = firebaseDatabaseLista.getReference("UsuariosR").child(numeroKerkly)
            .child("Lista de Usuarios")
        databaseReferencekerkly.push().child("telefono").setValue(numeroCliente)

    }

    private fun AceptarServicio() {
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
                    if(Res == "1"){
                         llamartopico.llamartopico(this@MapsActivity, tokenCliente, "Su Solicitud a Sido Aceptada, Por favor espere un momento...", nombrekerkly)
                        val intent = Intent(this@MapsActivity, MainActivityChats::class.java)
                        intent.putExtra("nombreCompletoCliente", nombreCliente)
                        intent.putExtra("correoCliente", correoCliente)
                        intent.putExtra("telefonoCliente",telefonoCliente)
                        intent.putExtra("telefonoKerkly", telefonoKerkly)
                        intent.putExtra("urlFotoCliente", urlfoto)
                        intent.putExtra("nombreCompletoKerkly", nombrekerkly)
                        intent.putExtra("tokenCliente", tokenCliente)
                        intent.putExtra("directoMaps","Maps")
                        startActivity(intent)
                        finish()
                    }else{
                        Toast.makeText(this@MapsActivity, "El Servicio No se Pudo Acompletar $Res", Toast.LENGTH_SHORT).show()
                    }
                }catch (e: java.lang.Exception){
                    e.printStackTrace()
                    Toast.makeText(this@MapsActivity, "error ${e.printStackTrace()}", Toast.LENGTH_SHORT).show()
                }

            }
            override fun failure(error: RetrofitError?) {
                Toast.makeText(this@MapsActivity, "error ${error.toString()}", Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun obtenerToken(telefonoCliente:String,telefonoKerkly:String){
        firebase_databaseUsu = FirebaseDatabase.getInstance()
        databaseUsu = firebase_databaseUsu.getReference("UsuariosR")
            .child(telefonoCliente.toString()).child("MisDatos")
        databaseUsu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val u2 = snapshot.getValue(usuarios::class.java)

                // println(" 576: " + u2!!.correo.trim())
                //telefonoCliente =  u2!!.telefono.trim()
                urlfoto =  u2!!.foto.trim()
                val nombreCliente =  u2!!.nombre.trim()
                val correoCliente =  u2!!.correo.trim()
                tokenCliente = u2!!.token.trim()
                // Toast.makeText(this@MapsActivity,"$nombreCliente",Toast.LENGTH_SHORT).show()
                //enviar Notificacion
                //   agregarContacto(telefonoCliente, telefonoKerkly)
                verificarDatoNoExistente(telefonoCliente, telefonoKerkly)

            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }


}
