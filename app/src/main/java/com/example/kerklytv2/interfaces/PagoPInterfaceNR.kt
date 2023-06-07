package com.example.kerklytv2.interfaces


import retrofit.Callback
import retrofit.client.Response
import retrofit.http.Field
import retrofit.http.FormUrlEncoded
import retrofit.http.POST

interface PagoPInterfaceNR {
    @FormUrlEncoded
    @POST("/precioTotalPresupuestoNR.php")
    fun MandarPago(
        @Field("idPresupuestoNoRegistrado") idPresupuesto: Int,
        @Field("PagoTotal") PagoTotal: String,
        @Field("CURP") CURP: String,
        callback: Callback<Response?>
    )
}