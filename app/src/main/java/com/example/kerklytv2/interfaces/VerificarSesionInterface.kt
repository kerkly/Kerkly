package com.example.kerklytv2.interfaces

import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface VerificarSesionInterface {

    @FormUrlEncoded
    @POST("/VerificarSesionKerkly.php")
    fun sesionAbierta(
        @Field("deviceIDk") deviceIDk: String?,
        callback: Callback<Response?>?
    )
}