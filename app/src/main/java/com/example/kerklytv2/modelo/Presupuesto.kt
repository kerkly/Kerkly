package com.example.kerklytv2.modelo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Presupuesto {

    @SerializedName("problema")
    @Expose
    var problema: String? = null

    @SerializedName("telefonoCliente")
    @Expose
    var telefonoCliente: String? = null

    @SerializedName("Nombre")
    @Expose
    var Nombre: String? = null

    @SerializedName("Apellido_Paterno")
    @Expose
    var Apellido_Paterno: String? = null

    @SerializedName("Apellido_Materno")
    @Expose
    var Apellido_Materno: String? = null

    @SerializedName("Calle")
    @Expose
    var Calle: String? = null

    @SerializedName("Colonia")
    @Expose
    var Colonia: String? = null

    @SerializedName("No_Exterior")
    @Expose
    var No_Exterior: String? = null

    @SerializedName("Codigo_Postal")
    @Expose
    var Codigo_Postal: String? = null

    @SerializedName("Referencia")
    @Expose
    var Referencia: String? = null

    @SerializedName("fechaP")
    @Expose
    val fechaP: String? = null

    @SerializedName("idPresupuesto")
    @Expose
    var idPresupuesto = 0
}