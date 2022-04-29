package com.example.kerklytv2.trazar_rutas


import retrofit.http.FormUrlEncoded
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ObtenerCoordenadasNoRegistrado {
    @FormUrlEncoded
    @GET("CoordenadasDelClienteNoRegistradoPresu.php")
    open fun getNoRegistrado(@Query("telefono") Telefono: String?):
            Call<List<CoordanadasClienteNoR?>?>?

    @FormUrlEncoded
    @GET("CoordenadasDelClienteSiRegistradoPresu.php")
    open fun getRegistrado(@Query("telefono") Telefono: String?):
            Call<List<CoordanadasClienteNoR?>?>?
}