package com.example.kerklytv2.modelo.serial

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Presupuesto {
    @SerializedName("latitud")
    @Expose
    var latitud: String = ""

    @SerializedName("longitud")
    @Expose
    var longitud: String = ""

    @SerializedName("problema")
    @Expose
    var problema: String = ""

    @SerializedName("telefonoCliente")
    @Expose
    var telefonoCliente: String = ""

    @SerializedName("Nombre")
    @Expose
    var Nombre: String = ""

    @SerializedName("Apellido_Paterno")
    @Expose
    var Apellido_Paterno: String = ""

    @SerializedName("Apellido_Materno")
    @Expose
    var Apellido_Materno: String = ""

    @SerializedName("Calle")
    @Expose
    var Calle: String = ""

    @SerializedName("Colonia")
    @Expose
    var Colonia: String = ""

    @SerializedName("No_Exterior")
    @Expose
    var No_Exterior: String = ""

    @SerializedName("Codigo_Postal")
    @Expose
    var Codigo_Postal: String = ""

    @SerializedName("Referencia")
    @Expose
    var Referencia: String = ""

    @SerializedName("fechaP")
    @Expose
    var fechaP: String = ""

    @SerializedName("idPresupuesto")
    @Expose
    var idPresupuesto = 0
    @SerializedName("Correo")
    @Expose
    var Correo = ""
    @SerializedName("nombreO")
    @Expose
    var nombreO = ""
}