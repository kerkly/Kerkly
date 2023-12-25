package com.example.kerklytv2.trazar_rutas

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.example.kerklytv2.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class Marcador(
    var nMap: GoogleMap,
    var context: Context,
    var latitud: Double,
    var longitud: Double,
    var problema: String,
    var nombreCliente: String,
   var folio: String
) {

    fun CrearMarcador(location: Location, problema: String, nombreCliente: String) {
        val punto = LatLng(location.latitude, location.longitude)
        val height = 160
        val width = 165
        val jira = context.resources.getDrawable(R.drawable.programador) as BitmapDrawable
        val ji = jira.bitmap
        val jiras = Bitmap.createScaledBitmap(ji, width, height, false)

        nMap.addMarker(
            MarkerOptions()
                .position(punto)
                .title(nombreCliente)
                .snippet("$nombreCliente Folio: $folio Problematica: $problema")
                .icon(BitmapDescriptorFactory.fromBitmap(jiras))
        )

        nMap.setInfoWindowAdapter(object : GoogleMap.InfoWindowAdapter {
            override fun getInfoWindow(marker: Marker): View? {
                // Devuelve null para usar la ventana de información predeterminada
                return null
            }

            override fun getInfoContents(marker: Marker): View {
                val inflater = LayoutInflater.from(context)
                val view = inflater.inflate(R.layout.custom_info_window, null)

                // Personaliza la vista según tus necesidades
                val textView = view.findViewById<TextView>(R.id.textViewInfo)
                textView.text = "$nombreCliente Folio: $folio\nProblematica: $problema"
                return view
            }
        })
    }


    fun ObetenerCoordenadasBaseDeDatos(){
        val location1 = Location("marcador")
        location1.latitude = latitud.toDouble()
        location1.longitude = longitud.toDouble()
        //val nombre = poslist!!.get(0).nombre_noR

        CrearMarcador(location1, problema.toString(), nombreCliente)
    }
}