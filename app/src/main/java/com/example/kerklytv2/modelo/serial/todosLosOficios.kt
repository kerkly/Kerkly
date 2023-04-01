package com.example.kerklytv2.modelo.serial

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class todosLosOficios: Serializable {
    @SerializedName("nombreO")
    @Expose
    var nombreO: String? = null

}