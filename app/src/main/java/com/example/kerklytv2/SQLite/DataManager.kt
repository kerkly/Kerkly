package com.example.kerklyv5.SQLite

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Base64
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.request.RequestOptions
import com.example.kerklytv2.SQLite.MisOficios
import com.example.kerklytv2.SQLite.usuariosSqlite
import com.example.kerklytv2.modelo.serial.OficioKerkly
import com.squareup.picasso.Picasso
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class DataManager(context: Context) {
    private val databaseHelper: DatabaseHelper = DatabaseHelper(context)

    fun deleteAllTablas() {
        val db = databaseHelper.writableDatabase
        // Borrar todos los registros de la tabla Usuarios
        db.delete(DatabaseHelper.TABLE_NAME_USUARIOS, null, null)
        // Borrar todos los registros de la tabla Oficio
        db.delete(DatabaseHelper.TABLA_OFICIO, null, null)

        db.close()
    }

    fun verificarSiElUsarioExiste(context: Context, imageView: ImageView, txt_nombre: TextView, txt_correo: TextView, fotoByteArray: ByteArray, usuario: usuariosSqlite, telefono: String, nombre: String, apellidoPa:String, apellidoMa:String, correo: String){
        val isUsuarioTableExists = databaseHelper.isTableExists("Usuario")
        if (isUsuarioTableExists) {
            // La tabla "Usuario" existe en la base de datos
           // println("La tabla \"Usuario\" existe en la base de datos")
            val db = databaseHelper.writableDatabase
            // Consulta para verificar si ya existe un registro con el mismo valor de id
            val query = "SELECT * FROM ${DatabaseHelper.TABLE_NAME_USUARIOS} WHERE ${DatabaseHelper.COLUMN_ID_USUARIOS} = ?"
            val cursor = db.rawQuery(query, arrayOf(usuario.telefono.toString()))
            if (cursor.moveToFirst()) {
               // println("el usuario si se encuentra")
                val datos = DatosDelUsuario(context)
                for (usuario in datos) {
                    txt_nombre.text = "${usuario.nombre}. ${usuario.apellidoPa} ${usuario.apellidoMa}"
                    txt_correo.text = correo
                    val urlString: String = "data:image/jpeg;base64," + Base64.encodeToString(fotoByteArray, Base64.DEFAULT)
                    cargarImagen(urlString,context,imageView)
                }

            } else {
                misDatos(telefono,fotoByteArray,nombre,apellidoPa,apellidoMa,correo)
                val datos = DatosDelUsuario(context)
                for (usuario in datos) {
                    txt_nombre.text = "${usuario.nombre}. ${usuario.apellidoPa} ${usuario.apellidoMa}"
                    txt_correo.text = correo
                    val urlString: String = "data:image/jpeg;base64," + Base64.encodeToString(fotoByteArray, Base64.DEFAULT)
                    cargarImagen(urlString,context,imageView)
                }
                //println("el usuario no se encuentra")

            }
            cursor.close()
            db.close()
        } else {
            // La tabla "Usuario" no existe en la base de datos
          //  println("La tabla \"Usuario\" no existe en la base de datos")
            databaseHelper.onCreate(databaseHelper.writableDatabase) // Crear la tabla
            misDatos(telefono, fotoByteArray, nombre, apellidoPa, apellidoMa, correo)

        }

    }
     fun InsertarOficios(oficio: MisOficios){
         val values = ContentValues()
         val db = databaseHelper.writableDatabase
         val isUsuarioTableExists = databaseHelper.isTableExists(DatabaseHelper.TABLA_OFICIO)
         if (isUsuarioTableExists) {
             println("la tabla ${DatabaseHelper.TABLA_OFICIO} existe")

             val query = "SELECT * FROM ${DatabaseHelper.TABLA_OFICIO} WHERE ${DatabaseHelper.COLUMN_ID_OFICIO} = ?"
             val cursor = db.rawQuery(query, arrayOf(arrayOf(oficio.id).toString()))
             if (cursor.moveToFirst()) {
                 // Si se encuentra un registro con el mismo id, puedes decidir si quieres actualizarlo o ignorar la inserción
                 // Por ejemplo, puedes actualizar el registro existente con los nuevos datos
                 // updateOficio(oficio)
                 println("si se encuentra")
             } else {
                 println("la tabla ${DatabaseHelper.TABLA_OFICIO} no  existe")
                 values.put(DatabaseHelper.COLUMN_ID_OFICIO, oficio.id.toLong())
                 values.put(DatabaseHelper.COLUMN_OFICIOS, oficio.nombreOfi)
                 db.insert(DatabaseHelper.TABLA_OFICIO, null, values)

             }
             cursor.close()
             db.close()
         }else{
             println("no existe la tabla oficio")
             databaseHelper.onCreate(databaseHelper.writableDatabase) // Crear la tabla
             println("la tabla ${DatabaseHelper.TABLA_OFICIO} no  existe")
             values.put(DatabaseHelper.COLUMN_ID_OFICIO, oficio.id.toLong())
             values.put(DatabaseHelper.COLUMN_OFICIOS, oficio.nombreOfi)
             db.insert(DatabaseHelper.TABLA_OFICIO, null, values)
         }
        // mostrarOficios(textView)
    }
    @SuppressLint("Range")
    fun mostrarOficios(textView: TextView) {
        val db = databaseHelper.readableDatabase
        val query = "SELECT * FROM ${DatabaseHelper.TABLA_OFICIO}"
        val cursor = db.rawQuery(query, null)
        val totalRows = cursor.count
        var acumulador = ""
        while (cursor.moveToNext()) {
            val oficio = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_OFICIOS))
           // println("oficio----> $oficio")
            acumulador += if (acumulador.isNotEmpty()) ", $oficio" else oficio
        }

        textView.text = acumulador
        db.close()
    }

    private fun misDatos(telefono: String, foto: ByteArray, nombre: String,apellidoPa:String,apellidoMa:String,correo: String){
        val values = ContentValues()
        values.put(DatabaseHelper.COLUMN_ID_USUARIOS,telefono)
        values.put(DatabaseHelper.COLUMN_FOTO, foto)
        values.put(DatabaseHelper.COLUMN_NOMBRE, nombre)
        values.put(DatabaseHelper.COLUMN_APELIIDO_PA, apellidoPa)
        values.put(DatabaseHelper.COLUMN_APELIIDO_MA, apellidoMa)
        values.put(DatabaseHelper.COLUMN_CORREO, correo)

        val db = databaseHelper.writableDatabase
        db.insert(DatabaseHelper.TABLE_NAME_USUARIOS, null, values)
        //println("dato insertado $nombreOfi")
        db.close()
    }

    @SuppressLint("Range", "SuspiciousIndentation")
    fun DatosDelUsuario(context: Context): ArrayList<usuariosSqlite> {
        val db = databaseHelper.readableDatabase
        val datosUsuario = mutableListOf<usuariosSqlite>()
        val query = "SELECT * FROM ${DatabaseHelper.TABLE_NAME_USUARIOS}"
        val cursor = db.rawQuery(query, null)

        val totalRows = cursor.count
        var loadedRows = 0

        while (cursor.moveToNext()) {
            val idTelefono = cursor.getLong(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_USUARIOS))
            val foto = cursor.getBlob(cursor.getColumnIndex(DatabaseHelper.COLUMN_FOTO))
            val NOMBRE = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_NOMBRE))
            val ApellidoPa = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_APELIIDO_PA))
            val ApellidoMa = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_APELIIDO_MA))
            val correo = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_CORREO))

            val usuarios = usuariosSqlite(idTelefono, foto, NOMBRE, ApellidoPa, ApellidoMa, correo)
                        datosUsuario.add(usuarios)
        }
        db.close()
        return datosUsuario as ArrayList<usuariosSqlite>
    }

    private fun cargarImagen(urlImagen: String, context: Context, fotoPerfil: ImageView) {
        val file: Uri = Uri.parse(urlImagen)

        Picasso.get().load(urlImagen).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                // Aquí puedes realizar alguna acción con la imagen cargada si es necesario
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                val multi = MultiTransformation<Bitmap>(RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.ALL))
                Glide.with(context).load(file)
                    .apply(RequestOptions.bitmapTransform(multi))
                    .into(fotoPerfil)
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                //Log.e("ImageLoadError", "Error loading image: ${e.toString()}")
                // Aquí puedes manejar el error de carga de la imagen
            }
        })
    }

}