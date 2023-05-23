package com.example.kerklytv2.modelo.serial

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PresupuestoDatosClienteNoRegistrado {
    @SerializedName("idPresupuestoNoRegistrado")
    @Expose
    var idPresupuestoNoRegistrado =0

    @SerializedName("problema")
    @Expose
    var problema: String? = null

    @SerializedName("fechaPresupuesto")
    @Expose
    val fechaPresupuesto: String? = null

    @SerializedName("telefono_NoR")
    @Expose
    var telefono_NoR: String? = null

    @SerializedName("nombre_noR")
    @Expose
    var nombre_noR = ""

    @SerializedName("apellidoP_noR")
    @Expose
    var apellidoP_noR = ""

    @SerializedName("apellidoM_noR")
    @Expose
    var apellidoM_noR = ""

    @SerializedName("latitud")
    @Expose
    var latitud: String?=null

    @SerializedName("longitud")
    @Expose
    var longitud: String?=null

    @SerializedName("Pais")
    @Expose
    var Pais: String? = null

    @SerializedName("Ciudad")
    @Expose
    var Ciudad: String? = null

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
    var nombreO: String? = null

}
