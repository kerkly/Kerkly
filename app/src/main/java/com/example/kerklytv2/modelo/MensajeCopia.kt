package com.example.kerklytv2.modelo

class MensajeCopia(m: String, h: String, mensajeLeido: String){
    var mensaje = m
    var hora = h
    var tipo_usuario = "Kerkly"
    var mensajeLeido = mensajeLeido

    constructor(): this("", "", "")

}