package com.example.kerklytv2.trazar_rutas

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CoordanadasClienteNoR {

    @SerializedName("nombre_noR")
    @Expose
    var nombre_noR: String? = ""

    @SerializedName("latitud")
    @Expose
    var latitud: Double = 0.0

    @SerializedName("longitud")
    @Expose
    var longitud: Double = 0.0
}