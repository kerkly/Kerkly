package com.example.kerklytv2.interfaces

import com.example.kerklytv2.modelo.serial.OficioKerkly
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ObtenerKerklyaOficiosInterface {

    @GET("Obtener_oficios_kerkly.php")
    open fun getOficios_kerkly(@Query("curp") curp: String?):
            Call<List<OficioKerkly?>?>?

}