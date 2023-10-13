package com.example.kerklytv2.interfaces

import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface SesionAbiertaInterface {

    @FormUrlEncoded
    @POST("/sesionAbierta.php")
    fun sesionAbierta(
        @Field("Telefono") Telefono: String?,
        @Field("deviceIDk") deviceIDk: String?,
        @Field("uidKerkly") uidKerkly: String?,
        callback: Callback<Response?>?
    )
}