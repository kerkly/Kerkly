package com.example.kerklytv2.interfaces

import com.example.kerklytv2.modelo.serial.Presupuesto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PresupuestoNormalInterface {

    @GET("ObtenerPresupuestos.php")
    open fun getPost(@Query("Telefono") Telefono: String?,
                     @Query("pageNumber") pageNumber: Int,
                     @Query("pageSize") pageSize: Int):
            Call<List<Presupuesto?>?>?
}