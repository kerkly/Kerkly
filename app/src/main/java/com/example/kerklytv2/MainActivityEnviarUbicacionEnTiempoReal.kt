package com.example.kerklytv2

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.TextView
import androidx.core.app.ActivityCompat
import com.example.kerklytv2.EnviarUbicacionEnTiempoReal.modeloCoordenadas
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.IOException
import java.util.*

class MainActivityEnviarUbicacionEnTiempoReal : AppCompatActivity() {

    var txtlatitud: TextView? = null
    var txtlongitud: TextView? = null
    //lateinit var direccion: TextView
    lateinit var btngps: Button

    lateinit var mlocManager: LocationManager
    lateinit var locationListener: LocationListener
    lateinit var mDatabase: DatabaseReference

    var latitud: Double?=0.0
    var longitud: Double?=0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    //    setContentView(R.layout.activity_main_enviar_ubicacion_en_tiempo_real)

        mDatabase = FirebaseDatabase.getInstance().reference

    //    txtlatitud = findViewById(R.id.textViewLatitud);
      //  txtlongitud = findViewById(R.id.textViewLongitud);

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
        }


        /*btngps = findViewById(R.id.buttonGps)
        btngps.setOnClickListener{
            locationStart()
            /* Intent(this, ServicioEnSegundoPlano::class.java).also {
                 startService(it)
                // textViewLatitud.text = "servicio iniciado"
             }*/


        }*/


     /*   btnDetenerGPS.setOnClickListener {
            mlocManager.removeUpdates(locationListener)
            /*Intent(this, ServicioEnSegundoPlano::class.java).also {
                stopService(it)
               // textViewLongitud.text = "servicio Detenido"
            }*/
        }*/


    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("ServicioEnSegundoPlano", "Activity Destruida")
    }


    fun locationStart() {
        mlocManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val Local = Localizacion()
        Local.setMainActivity(this)
        val gpsEnabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        //En caso de que el Gps este desactivado, entrara en el if y nos mandara a la configuracion de nuestro Gps para activarlo
        if (!gpsEnabled) {
            val settingsIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(settingsIntent)
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1000
            )
            return
        }

        locationListener = Local
        mlocManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0f, locationListener)
        mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0f, locationListener)

        // Toast.makeText(MainActivity.this, "demtro onStart usauru " +  name, Toast.LENGTH_LONG).show();

    }

    fun setLocation(loc: Location) {
        //Obtener la direccion de la calle a partir de la latitud y la longitud
        if (loc.getLatitude() !== 0.0 && loc.getLongitude() !== 0.0) {
            try {
                val geocoder = Geocoder(this, Locale.getDefault())
                val list: List<Address> = geocoder.getFromLocation(
                    loc.getLatitude(), loc.getLongitude(), 1)!!
                if (!list.isEmpty()) {
                    val DirCalle: Address = list[0]
                   // textViewDireccion.setText(DirCalle.getAddressLine(0))
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions!!, grantResults)
        if (requestCode == 1000) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationStart()
                return
            }
        }
    }

    /* Aqui empieza la Clase Localizacion */
    class Localizacion : LocationListener {
        lateinit var mainActivity: MainActivityEnviarUbicacionEnTiempoReal
        var latitud: Double?=0.0
        var longitud: Double?=0.0


        @JvmName("getMainActivity1")
        fun getMainActivity(): MainActivityEnviarUbicacionEnTiempoReal? {
            return mainActivity
        }

        @JvmName("setMainActivity1")
        fun setMainActivity(mainActivity: MainActivityEnviarUbicacionEnTiempoReal?) {
            this.mainActivity = mainActivity!!
        }

        override fun onLocationChanged(loc: Location) {
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
            loc.latitude
            loc.longitude
            val sLatitud = loc.latitude.toString()
            val sLongitud = loc.longitude.toString()

            latitud = loc.latitude
            longitud = loc.longitude

            var  mDatabase: DatabaseReference = FirebaseDatabase.getInstance().getReference("Coordenadas en Tiempo Real").child("usuario1")
            val use = modeloCoordenadas(latitud, longitud)
            mDatabase.child("1").setValue(use) {
                    error, ref -> //txtprueba.setText(uid + "latitud " + latitud + " longitud " + longitud);
                //Toast.makeText(getMainActivity(), "Bienvenido ", Toast.LENGTH_SHORT).show()
                mainActivity?.txtlatitud?.setText(sLatitud)
                mainActivity?.txtlongitud?.setText(sLongitud)

            }
            mainActivity?.setLocation(loc)
            // System.out.println("latitud $sLatitud")
            //System.out.println("longitud $sLongitud")

        }

        override fun onProviderDisabled(provider: String) {
            // Este metodo se ejecuta cuando el GPS es desactivado
            mainActivity?.txtlatitud?.setText("GPS Desactivado")
        }

        override fun onProviderEnabled(provider: String) {
            // Este metodo se ejecuta cuando el GPS es activado
            mainActivity!!.txtlatitud!!.setText("GPS Activado")
        }

        override fun onStatusChanged(provider: String, status: Int, extras: Bundle) {
            when (status) {
                LocationProvider.AVAILABLE -> Log.d("debug", "LocationProvider.AVAILABLE")
                LocationProvider.OUT_OF_SERVICE -> Log.d("debug", "LocationProvider.OUT_OF_SERVICE")
                LocationProvider.TEMPORARILY_UNAVAILABLE -> Log.d(
                    "debug",
                    "LocationProvider.TEMPORARILY_UNAVAILABLE"
                )
            }
        }
    }
}