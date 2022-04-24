package com.example.kerklytv2.trazar_rutas


import retrofit.http.FormUrlEncoded
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ObtenerCoordenadasNoRegistrado {
    @FormUrlEncoded
    @GET("CoordenadasDelClienteNoRegistradoPresu.php")
    open fun getDatost(@Query("telefono") Telefono: String?):
            Call<List<CoordanadasClienteNoR?>?>?
}