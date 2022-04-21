package com.example.kerklytv2.interfaces

import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface CerrarSesionInterface {

    @FormUrlEncoded
    @POST("/cerrar_sesionKerkly.php")
    fun cerrar(
        @Field("curp") curp: String,
        callback: Callback<Response>
    )
}