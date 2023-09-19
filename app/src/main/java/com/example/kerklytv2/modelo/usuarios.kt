package com.example.kerklytv2.modelo

class usuarios(uid:String, telefono: String, email: String, name: String, foto: String, currentDateTimeString: String, token: String,curp:String) {
    var uid:String = uid

    var telefono: String = telefono

    var correo: String = email

    var nombre: String = name

    var foto: String = foto

    var fechaHora: String = currentDateTimeString

    var token: String = token
    var curp:String = curp
    constructor() : this("",""",""","","","","","","")

}