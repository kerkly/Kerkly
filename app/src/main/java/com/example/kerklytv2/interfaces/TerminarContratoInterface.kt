package com.example.kerklytv2.interfaces

import retrofit.http.FormUrlEncoded
import retrofit.http.POST
import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field

interface TerminarContratoInterface {
    @FormUrlEncoded
    @POST("/TerminarContratoNR.php")
    fun terminarContrato(
        @Field("idPresupuestoNoRegistrado") idPresupuestoNoRegistrado: String,
        @Field("Fecha_Final_NoRegistrado") Fecha_Final_NoRegistrado: String,
        callback: Callback<Response?>
    )
}