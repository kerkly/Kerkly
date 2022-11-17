package com.example.kerklytv2.interfaces


import com.example.kerklytv2.modelo.serial.todosLosOficios
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface obtenerTodosLosOficios {
    @GET("obtenerTodosLosOficiosDelKerkly.php")
    // fun getPost(): Call<List<presupuestok?>?>?
    open fun getPost(@Query("Telefono") Telefono: String?):
            Call<List<todosLosOficios?>?>?
}