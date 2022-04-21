package com.example.kerklytv2.interfaces

import com.example.kerklytv2.modelo.serial.HistorialNormal
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ObtenerHistorialInterface {

    @GET("ObtenerHistorialNormal.php")
    open fun getPost(@Query("Telefono") Telefono: String?):
            Call<List<HistorialNormal?>?>?
}