package com.example.kerklytv2.interfaces

import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface LoginInterface {
    @FormUrlEncoded
    @POST("/login.php")
    fun LoginKerkly(
        @Field("Telefono") Telefono: String?,
        @Field("Contrasena") Contrasena: String?,
        callback: Callback<Response?>
    )
}