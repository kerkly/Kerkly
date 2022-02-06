package com.example.kerklytv2.interfaces

import com.example.kerklytv2.modelo.Presupuesto
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PresupuestoNormalInterface {

    @GET("ObtenerPresupuestos.php")
    // fun getPost(): Call<List<presupuestok?>?>?
    open fun getPost(@Query("Telefono") Telefono: String?):
            Call<List<Presupuesto?>?>?
}