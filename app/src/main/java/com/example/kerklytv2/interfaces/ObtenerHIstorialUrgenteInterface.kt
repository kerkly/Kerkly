package com.example.kerklytv2.interfaces

import com.example.kerklytv2.modelo.serial.HistorialUrgencia
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ObtenerHIstorialUrgenteInterface {
    @GET("ObtenerHistorial.php")
    open fun getPost(@Query("Telefono") Telefono: String?):
            Call<List<HistorialUrgencia?>?>?
}