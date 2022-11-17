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

class Marcador(var nMap: GoogleMap, var context: Context, var telefono: String) {

    var u = Url().URL
    var poslist: ArrayList<CoordanadasClienteNoR>? =null


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
        System.out.println("telefono $telefono")

        val retrofit = Retrofit.Builder()
            .baseUrl(u + "/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val obtenerDatos = retrofit.create(ObtenerCoordenadasNoRegistrado::class.java)
        val call = obtenerDatos.getRegistrado(telefono)
        call?.enqueue(object : retrofit2.Callback<List<CoordanadasClienteNoR?>?> {

            override fun onResponse(
                call: Call<List<CoordanadasClienteNoR?>?>,
                response: retrofit2.Response<List<CoordanadasClienteNoR?>?>) {
                poslist = response.body() as ArrayList<CoordanadasClienteNoR>

                val location1 = Location("punto 1")
                location1.latitude = poslist!!.get(0).latitud
                location1.longitude = poslist!!.get(0).longitud
                val nombre = poslist!!.get(0).nombre_noR
                System.out.println("telefono: $telefono latitud del marcador" +poslist!!.size)
                CrearMarcador(location1, nombre.toString())
            }

            override fun onFailure(call: Call<List<CoordanadasClienteNoR?>?>, t: Throwable) {
                // Toast.makeText(this, "Codigo de respuesta de error: " + t.toString(), Toast.LENGTH_SHORT).show();
                System.out.println("error ${t.toString()}")
            }
        })

    }
}