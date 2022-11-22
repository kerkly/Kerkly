package com.example.kerklytv2.trazar_rutas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import com.example.kerklytv2.url.Url
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.URL

class Marcador(var nMap: GoogleMap, var context: Context, var latitud: String, var longitud: String, var nombre: String) {

    fun CrearMarcador(location: Location, nombre: String) {
        val punto = LatLng(location.latitude, location.longitude)
        val height = 160
        val width = 165
        val jira = context.resources.getDrawable(android.R.drawable.ic_menu_myplaces) as BitmapDrawable
        val ji = jira.bitmap
        val jiras = Bitmap.createScaledBitmap(ji, width, height, false)
        nMap.addMarker(
            MarkerOptions()
                .position(punto)
                .title(nombre).snippet("desea ir donde $nombre?")
                .icon(BitmapDescriptorFactory.fromBitmap(jiras))
        )
    }

    fun ObetenerCoordenadasBaseDeDatos(){
        val location1 = Location(nombre)
        location1.latitude = latitud.toDouble()
        location1.longitude = longitud.toDouble()
        //val nombre = poslist!!.get(0).nombre_noR

        CrearMarcador(location1, nombre.toString())
    }
}