package com.example.kerklytv2.interfaces

import com.example.kerklytv2.modelo.serial.TrabajoUrgencia
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrabajoUrgenteInterface {

    @GET("obtenerTrabajosPendientesUrgente.php")
    open fun getPost(@Query("Telefono") Telefono: String?):
            Call<List<TrabajoUrgencia?>?>?
}