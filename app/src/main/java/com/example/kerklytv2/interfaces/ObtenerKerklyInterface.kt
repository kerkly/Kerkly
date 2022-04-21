package com.example.kerklytv2.interfaces

import com.example.kerklytv2.modelo.serial.Kerkly
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ObtenerKerklyInterface {

    @GET("Obtener_kerkly.php")
    open fun getKerkly(@Query("telefono") telefono: String?):
            Call<List<Kerkly?>?>?
}