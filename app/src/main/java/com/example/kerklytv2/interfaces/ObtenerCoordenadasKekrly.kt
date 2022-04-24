package com.example.kerklytv2.interfaces

import com.example.kerklytv2.modelo.serial.CoordenadasKerkly
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ObtenerCoordenadasKekrly {

    @GET("Obtener_coordenadas.php")
    open fun getCoordenadas(@Query("curp") curp: String?):
            Call<List<CoordenadasKerkly?>?>?
}