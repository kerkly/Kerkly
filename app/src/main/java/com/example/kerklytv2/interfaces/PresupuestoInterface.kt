package com.example.kerklytv2.interfaces

import com.example.kerklytv2.modelo.serial.PresupuestoDatos
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PresupuestoInterface {
    @GET("ConsultaPresupuestoClienteNR.php")
    // fun getPost(): Call<List<presupuestok?>?>?
    open fun getPost(@Query("Telefono") Telefono: String?,
                     @Query("oficio") oficio: String?,):
            Call<List<PresupuestoDatos?>?>?
}