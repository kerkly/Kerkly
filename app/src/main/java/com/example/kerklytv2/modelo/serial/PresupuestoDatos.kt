package com.example.kerklytv2.modelo.serial

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class PresupuestoDatos {
    @SerializedName("problema")
    @Expose
    var problema: String? = null

    @SerializedName("telefono_NoR")
    @Expose
    var idNoRTelefono: String? = null

    @SerializedName("nombre_noR")
    @Expose
    var nombre_noR: String? = null

    @SerializedName("apellidoP_noR")
    @Expose
    var apellidoP_noR: String? = null

    @SerializedName("apellidoM_noR")
    @Expose
    var apellidoM_noR: String? = null

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

    @SerializedName("fechaPresupuesto")
    @Expose
    val fechaPresupuesto: String? = null

    @SerializedName("idPresupuestoNoRegistrado")
    @Expose
    var idPresupuestoNoRegistrado =0

    @SerializedName("Correo")
    @Expose
    var Correo = ""

    @SerializedName("Nombre")
    @Expose
    var Nombre = ""

    @SerializedName("Apellido_Paterno")
    @Expose
    var Apellido_Paterno = ""

    @SerializedName("Apellido_Materno")
    @Expose
    var Apellido_Materno = ""

    @SerializedName("telefonoCliente")
    @Expose
    var telefonoCliente: String? = null
}
