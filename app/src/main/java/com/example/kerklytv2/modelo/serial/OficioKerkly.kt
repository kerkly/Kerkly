package com.example.kerklytv2.modelo.serial

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class OficioKerkly: Serializable {

    @SerializedName("nombreO")
    @Expose
    val nombreOficio: String = ""

    @SerializedName("idOficio")
    @Expose
    val idOficio: Int = 0

    @SerializedName("idoficio_trabajador")
    @Expose
    val idoficio_trabajador : Int = 0
}