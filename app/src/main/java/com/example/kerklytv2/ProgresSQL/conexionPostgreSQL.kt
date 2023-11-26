package com.example.kerklytv2.ProgresSQL

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import org.postgresql.util.PGobject
import java.sql.Connection
import java.sql.DriverManager
import java.sql.SQLException

class conexionPostgreSQL {
   private var conexion: Connection? = null
    @SuppressLint("SuspiciousIndentation")
    fun obtenerConexion(context: Context):Connection? {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            val threadPolicy = ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(threadPolicy)
        }
        try {
            Class.forName("org.postgresql.Driver") // Cargar el driver JDBC
            val host = "6.tcp.ngrok.io"
            val port = "16646"
            val databaseName = "Kerkly"
            val username = "luis_admin"
            val password = "Lu0599@"
            val url = "jdbc:postgresql://$host:$port/$databaseName"

            conexion = DriverManager.getConnection(url, username, password)
           // Toast.makeText(context, "Conexión exitosa", Toast.LENGTH_SHORT).show()
            return conexion
        } catch (e: SQLException) {
            // Manejar la excepción (lanzar, notificar al usuario, etc.)
            println("Error al establecer la conexión: ${e.message}")
            e.printStackTrace()
            conexion = null
            // Verificar si la excepción se debe a que el host o el puerto no están disponibles
            if (e.message?.contains("Connection refused") == true) {
                // Manejar la situación en la que el host o el puerto no están disponibles
                println("El host o el puerto no están disponibles.")
                // Notificar al usuario u realizar acciones adecuadas.
            }
        }
        return null
    }
    fun cerrarConexion(){
        conexion!!.close()
    }
    fun insertOrUpdateSeccionKerkly(curp: String, idpoligono: Int, uidKerkly: String, latitud: Double, longitud: Double) {
        val existsQuery = "SELECT COUNT(*) FROM \"KerklyEnMovimiento\" WHERE \"curp\" = ?"
        val updateQuery = "UPDATE \"KerklyEnMovimiento\" SET \"IdPoligono\" = ?, \"ubicacion\" = ST_SetSRID(ST_MakePoint(?, ?), 4326) WHERE \"curp\" = ?"
        val insertQuery = "INSERT INTO \"KerklyEnMovimiento\" (\"curp\", \"IdPoligono\", \"uidKerkly\", \"ubicacion\") VALUES (?, ?, ?, ST_SetSRID(ST_MakePoint(?, ?), 4326))"

        var exists: Boolean = false
        conexion?.prepareStatement(existsQuery)?.use { existsStmt ->
            existsStmt.setString(1, curp)
            val existsResult = existsStmt.executeQuery()
            exists = existsResult.next() && existsResult.getInt(1) > 0
        }

        if (exists) {
            conexion?.prepareStatement(updateQuery)?.use { updateStmt ->
                updateStmt.setInt(1, idpoligono)
                updateStmt.setDouble(2, longitud)
                updateStmt.setDouble(3, latitud)
                updateStmt.setString(4, curp)

                updateStmt.executeUpdate()
            }
        } else {
            conexion?.prepareStatement(insertQuery)?.use { insertStmt ->
                insertStmt.setString(1, curp)
                insertStmt.setInt(2, idpoligono)
                insertStmt.setString(3, uidKerkly)
                insertStmt.setDouble(4, longitud)
                insertStmt.setDouble(5, latitud)
                insertStmt.executeUpdate()
            }
        }
    }


    fun obtenerSeccionKekrly(editText: EditText, textView: TextView){
        val curp = editText.text.toString()
        conexion!!.createStatement().use { stmt ->
            //  val query = "SELECT * FROM public.kerkly"
            val query = "select \"curp\", \"id_0\"  from kerkly  inner join \"poligonoChilpo\" on id_0 = \"idPoligono\"where \"curp\" ='$curp'"
            stmt.executeQuery(query).use { rs ->
                while (rs.next()) {
                    val idKerkly = rs.getString("curp")
                    val idpoligono = rs.getInt("id_0")
                    println(idKerkly)
                    println(idpoligono)
                    textView.text = "idKerkly: $idKerkly \n idSeccion: $idpoligono"
                }
            }
        }
    }

    fun ObtenerDatosGeom(longitud: Double, latitud: Double): String{
        var geom:String = ""
        conexion!!.createStatement().use { stmt ->
            //  val query = "SELECT * FROM public.kerkly"
            val query = "SELECT ST_AsText(geom) FROM \"poligonoChilpo\" WHERE ST_Contains(geom, ST_SetSRID(ST_MakePoint($longitud,$latitud), 4326))"
            stmt.executeQuery(query).use { rs ->
                while (rs.next()) {
                  //  idpoligono = rs.getInt("id_0").toString()
                     geom = rs.getString("st_astext")
                    println(geom)
                  //  println(idpoligono)
                  //  textView.text = "idSeccion: $idpoligono \n $geom "
                    return geom
                }
            }
        }
      return geom
    }
    fun ObtenerSeccionCoordenadas(longitud: Double, latitud: Double): Int {
        var idSeccion: Int = 0
        try {
            conexion!!.createStatement().use { stmt ->
                val query = "SELECT * FROM \"poligonoChilpo\" WHERE ST_Contains(geom, ST_SetSRID(ST_MakePoint($longitud,$latitud), 4326))"
                stmt.executeQuery(query).use { rs ->
                    while (rs.next()) {
                        idSeccion = rs.getInt("id_0")
                        println(idSeccion)
                        return idSeccion.toInt()
                    }
                }
            }
        } catch (e: SQLException) {
            // Manejar la excepción, imprimir un mensaje de registro o realizar alguna acción adecuada.
            e.printStackTrace()
            println("erro 140")
        }
        return idSeccion
    }


    fun crearTodosLosPoligonos(): ArrayList<String>{
        var ListaDePoligonos :ArrayList<String> = ArrayList()
        conexion!!.createStatement().use { stmt ->
            //  val query = "SELECT * FROM public.kerkly"
            /*ST_AsText es una función utilizada en sistemas de bases de datos geoespaciales,
             como PostGIS en PostgreSQL, para convertir objetos geométricos en su representación
              en formato de texto legible por humanos. Esta función toma una geometría como entrada
              y la devuelve en forma de cadena de texto en un formato específico, generalmente en el
               formato Well-Known Text (WKT) o en otro formato de texto geoespacial.
             */
            val query = "SELECT ST_AsText(geom) FROM \"poligonoChilpo\""
            stmt.executeQuery(query).use { rs ->
                while (rs.next()) {
                    val geomText = rs.getString("st_astext")
                    ListaDePoligonos.add(geomText)
                   // println("mi lista "+ListaDePoligonos)
                  //  return ListaDePoligonos
                }
            }
        }
        return ListaDePoligonos
    }

    //metodo que mediante un un poligono circular creado. determina sobre que secciones pertenece
    fun poligonoCircular(latitud: Double,longitud: Double,radio: Double): ArrayList<geom>{
        var listaDeSecciones :ArrayList<geom> = ArrayList()
        conexion!!.createStatement().use { stmt ->
           /* ST_Intersects es una función utilizada en sistemas de bases de datos geoespaciales,
           como PostGIS en PostgreSQL, para determinar si dos objetos geométricos se intersectan
           en el espacio. En otras palabras, verifica si dos geometrías tienen algún punto en común
            o se superponen de alguna manera.
            */

            /*ST_Buffer es una función utilizada en sistemas de bases de datos geoespaciales,
            como PostGIS en PostgreSQL, para generar un nuevo objeto geométrico que representa
            el área resultante de expandir o "bufar" una geometría existente alrededor de sus
            límites. La expansión se realiza creando un nuevo polígono que abarca una distancia
             específica desde los bordes de la geometría original.
             */

            /*ST_MakePoint es una función utilizada en sistemas de bases de datos geoespaciales,
             como PostGIS en PostgreSQL, para crear un objeto geométrico de tipo punto en un espacio
             bidimensional o tridimensional. Esta función permite definir las coordenadas del punto
             directamente en la llamada a la función.
             */

            /*La palabra clave "geography" se refiere a un tipo de dato utilizado en sistemas de bases
             de datos geoespaciales para representar datos geográficos en la superficie de la Tierra,
              teniendo en cuenta la forma esférica del planeta. A diferencia de los tipos de datos geométricos
              tradicionales, que trabajan en un plano cartesiano, los datos geográficos consideran la curvatura
               de la Tierra y permiten realizar cálculos precisos de distancia, área y otros aspectos
               en un contexto geoespacial.
             */
            val query = "Select id_0, geom from \"poligonoChilpo\" where ST_Intersects(geom, ST_Buffer(ST_MakePoint($longitud,$latitud)::geography,$radio))"
            stmt.executeQuery(query).use { rs ->
                while (rs.next()) {
                    var id_0 = rs.getString("id_0")
                    var geom = rs.getString("geom")
                    var wktGeometry = geom
                    var pgObject = PGobject()
                    pgObject.type = "geometry"
                    pgObject.value = wktGeometry

                    var statement = conexion!!.prepareStatement("SELECT ST_AsText(?)")
                    statement.setObject(1, pgObject)

                    var resultSet = statement.executeQuery()
                    if (resultSet.next()) {
                        var geomText = resultSet.getString(1)
                        println("Geometry as text: $geomText")
                        var g =  geom(id_0,geomText)
                        listaDeSecciones.add(g)
                    }


                }
            }
        }
        return listaDeSecciones
    }


}