package com.example.kerklytv2.modelo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class Presupuesto {

    @SerializedName("problema")
    @Expose
    var problema: String = ""

    @SerializedName("telefonoCliente")
    @Expose
    var telefonoCliente: String = ""

    @SerializedName("cliente.Nombre")
    @Expose
    var Nombre: String = ""

    @SerializedName("cliente.Apellido_Paterno")
    @Expose
    var Apellido_Paterno: String = ""

    @SerializedName("cliente.Apellido_Materno")
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
}