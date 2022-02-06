package com.example.kerklytv2.interfaces

import com.example.kerklytv2.modelo.TrabajoNormal
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface TrabajoNormalnterface {

    @GET("ObtenerTrabajosNormal.php")
    open fun getPost(@Query("Telefono") Telefono: String?):
            Call<List<TrabajoNormal?>?>?
}