package com.example.kerklytv2.modelo

class Mensaje(m: String, h: String, mensajeLeido: String, archivo: String, tipoArchivo: String){
    var mensaje = m
    var hora = h
    var tipo_usuario = "Kerkly"
    var mensajeLeido = mensajeLeido
    var archivo = archivo
    var tipoArchivo = tipoArchivo

    constructor(): this("", "", "","", "")

}