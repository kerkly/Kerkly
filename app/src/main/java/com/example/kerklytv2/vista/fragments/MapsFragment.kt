package com.example.kerklytv2.vista.fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.kerklytv2.R
import com.example.kerklytv2.databinding.ActivityMapsBinding
import com.example.kerklytv2.trazar_rutas.GPSTracker
import com.example.kerklytv2.trazar_rutas.GeoTask
import com.example.kerklytv2.trazar_rutas.Utils_k
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMapLongClickListener,
    GoogleMap.OnMarkerClickListener, GeoTask.Geo {
    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    var latitud: Double = 0.0
    var longitud: Double = 0.0
    private var locationManager: LocationManager? = null
    var progressBar: ProgressBar? = null
    var location: Location? = null
    var gpsTracker: GPSTracker? = null
    var request: RequestQueue? = null
    var jsonObjectRequest: JsonObjectRequest? = null
    lateinit var dialog: Dialog
    private lateinit var telefono: String
    private lateinit var b: Bundle
    lateinit var latitud2: String
    lateinit var longitud2: String
    lateinit var nombreCliente: String
    lateinit var nombrekerkly: String
    private lateinit var context: Activity

    var minutos: Double = 0.0
    var distancia = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,  savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_maps, container, false)
        context = requireActivity()
       // b = intent.extras!!
        //recibimos las coordenas
        //telefono = b.getString("Telefono").toString()
        latitud2 = arguments?.getString("latitud").toString()
        longitud2 = arguments?.getString("longitud").toString()
        nombreCliente = arguments?.getString("Nombre").toString()
        nombrekerkly = arguments?.getString("nombrekerkly").toString()

        //  System.out.println("latitud : $latitud2 $longitud2 $nombreCliente latitud del marcador")

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        gpsTracker = GPSTracker(requireContext())
        location = gpsTracker!!.location
        request = Volley.newRequestQueue(requireContext())
        getLocalizacion()

    }


    private fun getLocalizacion() {
        locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        //val locationManager = activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
        //En caso de que el Gps este desactivado, entrara en el if y nos mandara a la configuracion de nuestro Gps para activarlo
        //En caso de que el Gps este desactivado, entrara en el if y nos mandara a la configuracion de nuestro Gps para activarlo

        if (!gpsEnabled) {
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        }
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                context,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
            return
        }

    }


    fun TrazarLineas(latLng: LatLng){
        Utils_k.coordenadas.destinoLat = latLng.latitude
        Utils_k.coordenadas.destinoLng = latLng.longitude
        setProgressDialog()
        try {
            while (location == null) {
                location = gpsTracker!!.location

            }
            location = gpsTracker!!.location
            Utils_k.coordenadas.origenLat = location!!.latitude
            Utils_k.coordenadas.origenLongi = location!!.longitude
            Log.d("latitud", location!!.latitude.toString())
            Log.d("longitud", location!!.longitude.toString())
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

    private fun mostrarDialogoPersonalizado(min: Double, dist: Int) {
        val builder = AlertDialog.Builder(context)
        val inflater = layoutInflater
        val view: android.view.View? = inflater.inflate(R.layout.dialog_aceptar_servicio, null)
        builder.setView(view)


        val dialog = builder.create()
        dialog.show()
        val txt: TextView = view!!.findViewById(R.id.text_dialog)

        //  txt.text = "¿Seguro que deseas aceptar el servicio?"
        val textView_result1: TextView = view.findViewById(R.id.textView_result1)
        val textView_result2: TextView = view.findViewById(R.id.textView_result2)
        textView_result2.setText(" " +(min / 60).toInt() + " hr " + (min % 60).toInt() + " mins")
        textView_result1.setText("$dist kilometros")

        val btnAceptar: Button = view!!.findViewById(R.id.btnAceptar)
        btnAceptar.setOnClickListener(object : android.view.View.OnClickListener{

            override fun onClick(p0: android.view.View?) {
                Toast.makeText(requireContext(), "Aceptado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
               // var intent = Intent(context, ::class.java)
              //  startActivity(intent)
            }


        })

        val btnRechazar: Button = view!!.findViewById(R.id.btnCancel)
        btnRechazar.setOnClickListener(object: android.view.View.OnClickListener{
            override fun onClick(p0: android.view.View?) {
                Toast.makeText(requireContext(), "Cancelado", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

        })
    }

    fun setProgressDialog() {
        val llPadding = 30
        val ll = LinearLayout(context)
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam
        progressBar = ProgressBar(context)

        progressBar!!.isIndeterminate = true
        progressBar!!.setPadding(0, 0, llPadding, 0)
        progressBar!!.layoutParams = llParam
        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(context)
        tvText.text = "Por favor espere un momento..."
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20f
        tvText.layoutParams = llParam
        ll.addView(progressBar)
        ll.addView(tvText)
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(ll)
        dialog = builder.create()
        dialog.show()

        val window: Window? = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window!!.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes = layoutParams
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
                        Utils_k.Marcador(
                            mMap,
                            requireContext(),
                            latitud2.toDouble(),
                            longitud2.toDouble(),
                            nombreCliente,
                            nombreCliente,
                            "folio"
                        )
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
                        dialog.dismiss()


                    }
                }

            } catch (e: JSONException) {
                e.printStackTrace()
            } catch (e: java.lang.Exception) {
            }
        }
        ) { error ->
            Toast.makeText(requireContext(), "No se puede conectar $error", Toast.LENGTH_LONG)
                .show()
            println()
            Log.d("ERROR: ", error.toString())
        }
        request!!.add(jsonObjectRequest)

        val url2 = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=$latInicial,$lngInicial&destinations=$latFinal,$lngFinal&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyD9i-yAGqAoYnIcm8KcMeZ0nsHyiQxl_mo"

        GeoTask(requireContext()).execute(url2)



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

    override fun onMapReady(googleMap: GoogleMap) {
        val constructor = LatLngBounds.Builder()
        mMap = googleMap
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mMap.isMyLocationEnabled = true
        mMap.uiSettings.isZoomControlsEnabled = true

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener: LocationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val miUbicacion = LatLng(location.getLatitude(), location.getLongitude())
                latitud = location.latitude
                longitud = location.longitude
                locationManager.removeUpdates(this)
                if (ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        requireActivity(),
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
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
              //  TrazarLineas(position)

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
        Utils_k.Marcador(
            mMap,
            requireContext(),
            latitud2.toDouble(),
            longitud2.toDouble(),
            nombrekerkly,
            nombreCliente,
            "folio"
        )
        mMap.setOnMapLongClickListener(this)
        mMap.setOnMarkerClickListener(this)
    }

    override fun onMapLongClick(p0: LatLng) {
        Utils_k.coordenadas.origenLat = p0.latitude
        Utils_k.coordenadas.origenLongi = p0.longitude
    }

    override fun onMarkerClick(p0: Marker): Boolean {
        AlertShow(p0.title, p0.position)
        return false
    }

  /*  override fun setDouble2(min: String?) {
        val res = min!!.split(",").toTypedArray()
        minutos = res[0].toDouble() / 60
        distancia = res[1].toInt() / 1000
        System.out.println("tiempo: $minutos")
    }*/

    override fun setDouble(min: String?) {
        val res = min!!.split(",").toTypedArray()
        minutos = res[0].toDouble() / 60
        distancia = res[1].toInt() / 1000
        System.out.println(" entrooooo en setDouble: "+ (minutos/60).toInt() +"hora" )
        mostrarDialogoPersonalizado(minutos, distancia)

    }
    private fun AlertShow(title: String?, position: LatLng) {
        val builder = AlertDialog.Builder(context)
        builder.setMessage("Desea ir  adonde $nombreCliente?")
        builder.setTitle(title)
        builder.setCancelable(false)
        builder.setPositiveButton("Si") { dialog, which ->
           TrazarLineas(position)
           // System.out.println("tiempoooooooooooooo: $minutos")

        }
        builder.setNegativeButton("No") { dialog, which -> dialog.cancel() }
        val alertDialog = builder.create()
        alertDialog.show()
    }

}