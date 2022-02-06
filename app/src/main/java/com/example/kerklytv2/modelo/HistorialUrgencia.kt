package com.example.kerklytv2.modelo

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class HistorialUrgencia {

    @SerializedName("Fecha_Inicio_NoRegistrado")
    @Expose
    var Fecha_Inicio_NoRegistrado: String? = null

    @SerializedName("Fecha_Final_NoRegistrado")
    @Expose
    var Fecha_Final_NoRegistrado: String? = null

    @SerializedName("idContraNoRegistrado")
    @Expose
    var idContraNoRegistrado: Int = 0

    @SerializedName("nombre_noR")
    @Expose
    var nombre_noR: String? = null

    @SerializedName("apellidoP_noR")
    @Expose
    var apellidoP_noR: String? = null

    @SerializedName("apellidoM_noR")
    @Expose
    var apellidoM_noR: String? = null

    @SerializedName("telefono_NoR")
    @Expose
    var telefono_NoR: String? = null

    @SerializedName("problema")
    @Expose
    var problema: String? = null

    @SerializedName("idPresupuestoNoRegistrado")
    @Expose
    var idPresupuestoNoRegistrado: Int = 0

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
}