package com.example.kerklytv2.modelo.serial

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class CoordenadasKerkly {

    @SerializedName("longitud")
    @Expose
    var longitud: Double? = null

    @SerializedName("latitud")
    @Expose
    var latitud: Double? = null
}