package com.example.kerklytv2.interfaces

import com.example.kerklytv2.modelo.serial.PresupuestoDatos
import com.example.kerklytv2.modelo.serial.PresupuestoDatosClienteNoRegistrado
import com.example.kerklytv2.modelo.serial.PresupuestourgentesDatosCliente
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface PresupuestoInterface {
    @GET("ConsultaPresupuestoURgenteClienteRegistrado.php")
    // fun getPost(): Call<List<presupuestok?>?>?
    open fun getPresupuestoClienteRegistrado(@Query("Telefono") Telefono: String?):
            Call<List<PresupuestourgentesDatosCliente?>?>?


    @GET("ConsultaPresupuestourgenteClienteNR.php")
    open fun getPresupuestoCliente_NO_Registrado(@Query("Telefono") Telefono: String?):
            Call<List<PresupuestoDatosClienteNoRegistrado?>?>?
}