package com.example.kerklytv2.interfaces

import com.example.kerklytv2.modelo.serial.PresupuestoDatos
import com.example.kerklytv2.modelo.serial.PresupuestoDatosClienteRegistrado
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PresupuestoInterface {
    @GET("ConsultaPresupuestoClienteNR.php")
    // fun getPost(): Call<List<presupuestok?>?>?
    open fun getPresupuestoClienteRegistrado(@Query("Telefono") Telefono: String?,
                     @Query("oficio") oficio: String?,):
            Call<List<PresupuestoDatosClienteRegistrado?>?>?
    open fun getPresupuestoCliente_NO_Registrado(@Query("Telefono") Telefono: String?,
                                             @Query("oficio") oficio: String?,):
            Call<List<PresupuestoDatos?>?>?
}