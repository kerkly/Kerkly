package com.example.kerklytv2.MisServicios

import android.Manifest
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.kerklytv2.R
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class LocationService : Service() {
    private lateinit var locationManager: LocationManager
    var isServiceRunning = false
    private lateinit var databaseReference: DatabaseReference
    private lateinit var uid: String

    override fun onCreate() {
        super.onCreate()
        // Inicializa el LocationManager
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        // Inicializa la instancia de Firebase Realtime Database
        databaseReference = FirebaseDatabase.getInstance().reference
    }
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Verifica los permisos de ubicación en tiempo de ejecución (similar al ejemplo anterior)
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // Registra un LocationListener para escuchar las actualizaciones de ubicación
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0L,
                0f,
                locationListener
            )
            isServiceRunning = true
        }
        val foregroundServiceIntent = Intent(this, ForegroundLocationService::class.java)
        startService(foregroundServiceIntent)

        if (intent != null) {
             uid = intent.getStringExtra("uid").toString()
            val parametro2 = intent.getStringExtra("parametro2")
            println("uid " + uid)
            println("parametro2 "+ parametro2)
            // Realiza las operaciones necesarias con los parámetros
        }
        return START_STICKY
    }

    private val locationListener: LocationListener = object : LocationListener {
        override fun onLocationChanged(location: Location) {
            // Aquí puedes enviar la ubicación al servidor o realizar las acciones necesarias
            val latitude = location.latitude
            val longitude = location.longitude
            println("latitud " +latitude)
            println("longitud "+ longitude)
            val locationData = LocationData(latitude, longitude)

            // Envía los datos a Firebase Realtime Database
            databaseReference.child("ubicacion").child(uid).setValue(locationData)
            // ...
        }

        override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
       // override fun onProviderEnabled(provider: String?) {}
        //override fun onProviderDisabled(provider: String?) {}
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        // Detiene las actualizaciones de ubicación al destruir el servicio
        locationManager.removeUpdates(locationListener)
    }
    data class LocationData(val latitude: Double, val longitude: Double)
}
