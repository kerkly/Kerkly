package com.example.kerklytv2.modelo.serial

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class TrabajoUrgencia {
    @SerializedName("idPresupuesto")
    @Expose
    var idPresupuesto: String? = null

    @SerializedName("problema")
    @Expose
    var problema: String? = null


    @SerializedName("fechaP")
    @Expose
    var fechaP: String? = null

    @SerializedName("Nombre")
    @Expose
    var Nombre: String? = null

    @SerializedName("Apellido_Paterno")
    @Expose
    var Apellido_Paterno: String? = null

    @SerializedName("Apellido_Materno")
    @Expose
    var Apellido_Materno: String? = null

    @SerializedName("telefonoCliente")
    @Expose
    var telefonoCliente: String? = null

    @SerializedName("Correo")
    @Expose
    var Correo: String? = null

    @SerializedName("latitud")
    @Expose
    var latitud: String? = null

    @SerializedName("longitud")
    @Expose
    var longitud: String? = null

    @SerializedName("Ciudad")
    @Expose
    var Ciudad: String? = null

    @SerializedName("Estado")
    @Expose
    var Estado: String? = null

    @SerializedName("Pais")
    @Expose
    var Pais: String? = null

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

    @SerializedName("nombreO")
    @Expose
    var nombreOficio: String? = null

    @SerializedName("uidCliente")
    @Expose
    var uidCliente: String? = null
}