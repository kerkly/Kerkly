package com.example.kerklytv2.interfaces

import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST


interface AceptarServicioUrgente {
    @FormUrlEncoded
    @POST("/AceptarServicioUrgente.php")
    fun AceptarServicio (
        @Field("idPresupuestoNoRegistrado") idPresupuesto: String,
        @Field("kerkly_aceptado") Aceptado: Int,
        callback: Callback<Response?>
    )
}