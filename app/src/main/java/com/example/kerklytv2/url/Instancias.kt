package com.example.kerklytv2.url

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class Instancias() {
    val database = FirebaseDatabase.getInstance()
    private val usuario = "UsuariosR"
    private val kerkly = "kerkly"
    private val cliente = "clientes"
    private val misDatos = "MisDatos"
    private val SolicitudUrgente ="SolicitudUrgente"
    private val SolicitudNormal ="Presupuesto Normal"
    private val listaUsuarios = "Lista de Usuarios"
    val storage = FirebaseStorage.getInstance()
    val storageRef = storage.reference
    private val chats = "chats"
    private val PresupuestoNormal = "Presupuestos Normal"
    private val PresupuestosNR = "Presupuestos NR"

    fun referenciaInformacionDelUsuarioKerkly(id: String): DatabaseReference{
        val databaseReferenceMisDatos = database.getReference(usuario).child(kerkly).child(id).child(misDatos)
        return databaseReferenceMisDatos
    }
    fun referenciaInformacionDelCliente(id: String): DatabaseReference{
        val databaseReferenceMisDatos = database.getReference(usuario).child(cliente).child(id).child(misDatos)
        return databaseReferenceMisDatos
    }

    fun referenciaSolicitudUrgente(id: String):DatabaseReference{
        val databaseReference = database.getReference(usuario).child(kerkly).child(id).child(SolicitudUrgente)
        return databaseReference
    }
    fun referenciaSolicitudNormal(id: String):DatabaseReference{
        val databaseReference = database.getReference(usuario).child(kerkly).child(id).child(SolicitudNormal)
        return databaseReference
    }
    fun CalcularDistancia(latitud:Double,longitud:Double,latitudFinal:Double,longitudFinal:Double):String{
        val url2 = "https://maps.googleapis.com/maps/api/distancematrix/json?origins=$latitud,$longitud&destinations=$latitudFinal,$longitudFinal&mode=driving&language=fr-FR&avoid=tolls&key=AIzaSyD9i-yAGqAoYnIcm8KcMeZ0nsHyiQxl_mo"
        return url2
    }
    fun directions(latitud:Double,longitud:Double,latitudFinal:Double,longitudFinal:Double):String{
        val url2 =  "https://maps.googleapis.com/maps/api/directions/json?origin=$latitud,$longitud&destination=$latitudFinal,$longitudFinal&key=AIzaSyD9i-yAGqAoYnIcm8KcMeZ0nsHyiQxl_mo&mode=drive"
        return url2
    }
    fun referenciaListaDeUsuariosKerkly(id: String):  DatabaseReference{
        val databaseReference = database.getReference(usuario).child(kerkly).child(id).child(listaUsuarios)
        return databaseReference
    }
    fun referenciaListaDeUsuariosCliente(id: String):  DatabaseReference{
        val databaseReference = database.getReference(usuario).child(cliente).child(id).child(listaUsuarios)
        return databaseReference
    }

    fun referenciaPresupuestoNormal(id: String,folio:String): DatabaseReference{
        val reference = database.getReference(usuario).child(kerkly).child(id).child(PresupuestoNormal).child("Presupuesto Normal $folio")
        return reference
    }
    fun referenciaPresupuestoNR(id: String,folio:String): DatabaseReference{
        val reference = database.getReference(usuario).child(kerkly).child(id).child( PresupuestosNR).child("Presupuesto NR $folio")
        return reference
    }

    fun StorageReferenceKerkly(idKerkly: String,idCliente:String,Nombrearchivo: String): StorageReference {
        val ref =  storageRef.child(usuario).child(kerkly).child(idKerkly).child(chats).child("$idKerkly"+"_"+"$idCliente").child(Nombrearchivo)
        return ref
    }
    fun EnviarArchivoStorageReference(idCliente: String,idKerkly:String,Nombrearchivo: String): StorageReference {
        val ref =  storageRef.child(usuario).child(cliente).child(idCliente).child(chats).child("$idCliente"+"_"+"$idKerkly").child(Nombrearchivo)
        return ref
    }

    fun chatsKerkly (uidKerkly: String, uidCliente:String): DatabaseReference{
        val ref = database.getReference(usuario).child(kerkly).child(uidKerkly).child(chats).child(uidKerkly+"_"+uidCliente)
        return ref
    }

    fun chatsCliente (uidKerkly: String, uidCliente:String): DatabaseReference{
        val ref = database.getReference(usuario).child(cliente).child(uidCliente).child(chats).child(uidCliente+"_"+uidKerkly)
        return ref
    }

    fun  enviarUbicacion(uidKerkly: String): DatabaseReference {
        val ref  = database.getReference(usuario).child(kerkly).child(uidKerkly).child("ubicacion")
        return ref
    }
}