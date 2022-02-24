package com.example.kerklytv2.interfaces

import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface PrecioNormalInterface {
    @FormUrlEncoded
    @POST("/precioTotal_Normal.php")
    fun MandarPago(
        @Field("idPresupuesto") idPresupuesto: Int,
        @Field("pago_total") PagoTotal: String,
        callback: Callback<Response?>
    )

}