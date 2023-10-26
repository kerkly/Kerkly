package com.example.kerklytv2

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.kerklytv2.MisServicios.LocationService


class MainActivityCompartirUbicacion : AppCompatActivity() {
    // Define un código de solicitud único, por ejemplo, 1
    val REQUEST_CODE = 1
    private lateinit var locationManager: LocationManager
    private val REQUEST_CODE_SHARE_LOCATION = 1000

    private  var latitud: Double = 0.0
    private  var longitud:Double = 0.0


    private val locationServiceIntent: Intent by lazy {
        Intent(this, LocationService::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_compartir_ubicacion)
        // Inicializa el locationManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager

        // Verifica y solicita permiso en tiempo de ejecución
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), REQUEST_CODE)
        }
        locationStart()


        val buttonGPs = findViewById<Button>(R.id.buttonGPs)
        buttonGPs.setOnClickListener {
            //stopLocationService()
            //Seguimiento()
            val gmmIntentUri =
                Uri.parse("https://www.google.com/maps/dir/?api=1&destination=$latitud,$longitud&travelmode=driving")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps") // Abre Google Maps

            startActivity(mapIntent)


            val shareLocationIntent = Intent(Intent.ACTION_SEND)
            shareLocationIntent.type = "text/plain"
            startActivityForResult(shareLocationIntent, REQUEST_CODE_SHARE_LOCATION)


        }

        val botonDetener = findViewById<Button>(R.id.buttonDetenerGPS)
        botonDetener.setOnClickListener {
           /* val locationServiceIntent: Intent = Intent(this, LocationService::class.java)
            stopService(locationServiceIntent)*/

        }
    }

    private fun Seguimiento(){
        val options = arrayOf("Si", "No")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Deseas Compartir la ubicacion en tiempo real con el cliente?")
        builder.setItems(options) { dialog: DialogInterface, which: Int ->
            when (which) {
                0 -> {
                    locationServiceIntent.putExtra("uid", "gf")
                    locationServiceIntent.putExtra("parametro2", "valor2")
                    startService(locationServiceIntent)
                    dialog.dismiss()
                }
                1 -> {
                    dialog.dismiss()

                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog: DialogInterface, _ ->
            dialog.dismiss()

        }

        val dialog = builder.create()
        dialog.show()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                // Puedes iniciar el servicio que requiere el permiso aquí si es necesario
                showMessage("Permiso concedido")
            } else {
                // Permiso denegado, muestra un mensaje al usuario o toma alguna acción apropiada
                showMessage("permiso Denegado")
            }
        }
    }

    fun showMessage(message: String){
        Toast.makeText(this,message, Toast.LENGTH_SHORT).show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_SHARE_LOCATION) {
            if (resultCode == Activity.RESULT_OK) {
                // Recibe el enlace compartido
                val sharedLink = data?.getStringExtra(Intent.EXTRA_TEXT)
                showMessage("link $sharedLink")
                println("link $sharedLink")
                // Ahora puedes guardar el enlace en tu base de datos o realizar otras acciones con él.
            }
        }
        if (REQUEST_CODE == 1000) {
                locationStart()
                return
        }
    }



    private fun locationStart() {
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val Local = Localizacion()
        Local.MainActivityCompartirUbicacion = this
        val gpsEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)

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
        locationManager!!.requestLocationUpdates(
            LocationManager.NETWORK_PROVIDER,
            0,
            0f,
            (Local as LocationListener)!!
        )
        locationManager!!.requestLocationUpdates(
            LocationManager.GPS_PROVIDER,
            0,
            0f,
            (Local as LocationListener)!!
        )
    }

    inner class Localizacion : LocationListener {
        var MainActivityCompartirUbicacion: MainActivityCompartirUbicacion? = null
        override fun onLocationChanged(loc: Location) {
            // if(loc != null){
            // Este metodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
            // debido a la deteccion de un cambio de ubicacion
          latitud =loc.latitude
       longitud = loc.longitude
            val sLatitud = java.lang.String.valueOf(loc.latitude)
            val sLongitud = java.lang.String.valueOf(loc.longitude)
            println("latitud $latitud")
            println("longitud $longitud")
            //MainActivityCompartirUbicacion?.setLocation(loc)
            MainActivityCompartirUbicacion?.locationManager?.removeUpdates(this)
            //  }
            //mainActivityConsultaSinRegistro?.locationManager!!.removeUpdates((mainActivityConsultaSinRegistro?.locationManager as LocationListener?)!!)
        }
        override fun onProviderDisabled(provider: String) {
            //mainActivityConsultaSinRegistro?.txt.setText("GPS Desactivado")
            Toast.makeText(MainActivityCompartirUbicacion, "GPS Desactivado", Toast.LENGTH_SHORT)
                .show()
        }
        override fun onProviderEnabled(provider: String) {
            Toast.makeText(MainActivityCompartirUbicacion, "GPS activado", Toast.LENGTH_SHORT)
                .show()
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