package com.example.kerklytv2.modelo

import kotlin.properties.Delegates

class Kerkly (telefono: String, contrasenia: String) {
    private var t = telefono
    private var c = contrasenia
    private lateinit var correo: String
    private lateinit var nombre: String
    private lateinit var apellidoP: String
    private lateinit var apellidoM: String
    private lateinit var curp: String
    private var sexo: Char = ' '



    constructor(): this("", "")

    //métodos set
    fun setTelefono(telefono: String) { t = telefono }

    fun setContrasenia(contrasenia: String) { c = contrasenia }

    fun setCorreo(correo: String) { this.correo = correo }

    fun setNombre(nombre: String) { this.nombre = nombre }

    fun setApellidoPaterno(apellidoPaterno: String) { this.apellidoP = apellidoPaterno }

    fun setApellidoMaterno(apellidoMaterno: String) { this.apellidoM = apellidoMaterno }

    fun setCurp(curp: String) { this.curp = curp }

    fun setSexo(sexo: Char) { this.sexo = sexo }


    //métodos get
    fun getTelefono(): String { return t }

    fun getContrasenia(): String { return c }

    fun getCorreo(): String { return correo }

    fun getNombre(): String { return nombre }

    fun getApellidoPaterno(): String { return apellidoP }

    fun getApellidoMaterno(): String { return apellidoM }

    fun getCurp(): String { return curp }

    fun getSexo(): Char { return sexo }
}