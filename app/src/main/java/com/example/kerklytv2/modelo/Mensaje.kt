package com.example.kerklytv2.modelo

class Mensaje(m: String, h: String){
    var mensaje = m
    var hora = h
    val tipo_usuario = "Kerkly"

    constructor(): this("", "")

}