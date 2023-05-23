package com.example.kerklytv2.interfaces


import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface PagoPInterface {
    @FormUrlEncoded
    @POST("/precioTotalUrgente.php")
    fun MandarPago(
        @Field("idPresupuesto") idPresupuesto: Int,
        @Field("PagoTotal") PagoTotal: String,
        callback: Callback<Response?>
    )
}