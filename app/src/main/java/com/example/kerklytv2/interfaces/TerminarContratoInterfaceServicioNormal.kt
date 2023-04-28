package com.example.kerklytv2.interfaces

import retrofit.http.FormUrlEncoded
import retrofit.http.POST
import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field

interface TerminarContratoInterfaceServicioNormal {
    @FormUrlEncoded
    @POST("/TerminarContrato.php")
    fun terminarContrato(
        @Field("Fecha_Final") Fecha_Final: String,
        @Field("id_presupuesto") id_presupuesto: String,
        callback: Callback<Response?>
    )
}