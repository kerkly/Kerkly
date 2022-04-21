package com.example.kerklytv2.modelo.serial

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Kerkly {

    @SerializedName("Curp")
    @Expose
    val curp: String = ""

    @SerializedName("Nombre")
    @Expose
    val nombre: String = ""

    @SerializedName("Apellido_Paterno")
    @Expose
    val ap: String = ""

    @SerializedName("Apellido_Materno")
    @Expose
    val am: String = ""

    @SerializedName("correo_electronico")
    @Expose
    val correo: String = ""
}