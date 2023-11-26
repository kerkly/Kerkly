package com.example.kerklytv2.modelo.serial

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class modeloVerificarSolictud {
    @SerializedName("idPresupuesto")
    @Expose
    var idPresupuesto =0

    @SerializedName("aceptoK")
    @Expose
    var aceptoK = ""

}