package com.example.kerklytv2.interfaces


import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface PagoPInterface {
    @FormUrlEncoded
    @POST("/precioTotal.php")
    fun MandarPago(
        @Field("idPresupuestoNoRegistrado") idPresupuestoNoRegistrado: Int,
        @Field("PagoTotal") PagoTotal: String,
        callback: Callback<Response?>
    )
}