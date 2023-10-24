package com.example.kerklytv2

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.kerklytv2.MisServicios.LocationService


class MainActivityCompartirUbicacion : AppCompatActivity() {

    private val locationServiceIntent: Intent by lazy {
        Intent(this, LocationService::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_compartir_ubicacion)

        val buttonGPs = findViewById<Button>(R.id.buttonGPs)
        buttonGPs.setOnClickListener {
            //stopLocationService()
            Seguimiento()

        }

        val botonDetener = findViewById<Button>(R.id.buttonDetenerGPS)
        botonDetener.setOnClickListener {
            val locationServiceIntent: Intent = Intent(this, LocationService::class.java)
            stopService(locationServiceIntent)

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



}