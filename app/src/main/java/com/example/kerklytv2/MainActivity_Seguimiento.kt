package com.example.kerklytv2

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.example.kerklytv2.modelo.usuarios
import com.example.kerklytv2.url.Instancias
import com.example.kerklytv2.ui.vista.fragments.AgendaFragment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MainActivity_Seguimiento : AppCompatActivity() {
    private lateinit var b: Bundle
    private lateinit var instancias: Instancias
    private lateinit var nombreCliente: String
    private lateinit var correoCliente:String
    private lateinit var telefonoCliente:String
    //  private lateinit var telefonokerkly:String
    private lateinit var urlfotoCliente:String
    private lateinit var tokenCliente: String
    private lateinit var uidCliente:String
    private lateinit var uidKerkly:String

    private lateinit var problema:String
    private lateinit var direccion:String
    private lateinit var fecha:String
    private lateinit var fragment:String
    private lateinit var folio:String
    private lateinit var curp:String

    private var mAuth: FirebaseAuth? = null
    private var currentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_seguimiento)
        b = intent.extras!!
        instancias = Instancias()
        uidCliente = b.getString("uidCliente").toString()
        telefonoCliente = b.getString("telefonoCliente").toString()
        correoCliente = b.getString("correoCliente").toString()
        problema = b.getString("correoCliente").toString()
        direccion = b.getString("Dirección").toString()
        fecha = b.getString("Fecha").toString()
        fragment = b.getString("Fragment").toString()
        folio = b.getString("folio").toString()
        curp = b.getString("Curp").toString()
        mAuth = FirebaseAuth.getInstance()
        currentUser = mAuth!!.currentUser
        uidKerkly = currentUser!!.uid


        obtenerCliente(uidCliente)

        val btnChat = findViewById<Button>(R.id.buttonChat)
        btnChat.setOnClickListener {
            val intent = Intent(this, MainActivityChats::class.java)
            b!!.putString("nombreCompletoCliente", nombreCliente)
            b!!.putString("correoCliente", correoCliente)
            b!!.putString("telefonoCliente",telefonoCliente)
            // b!!.putString("telefonoKerkly", telefonokerkly)
            b!!.putString("urlFotoCliente", urlfotoCliente)
            b!!.putString("tokenCliente", tokenCliente)
            b!!.putString("uidCliente", uidCliente)
            b!!.putString("uidKerkly", uidKerkly)

            intent.putExtras(b!!)
            startActivity(intent)
        }

        val btnFinalizar =  findViewById<Button>(R.id.buttonFinalizar)
        btnFinalizar.setOnClickListener {
           /* b.putString("NombreCliente", nombreCliente)
            b.putString("telefonoCliente", telefonoCliente)
            b.putString("uidCliente", uidCliente)
            b.putString("correoCliente", correoCliente)
            b.putString("Problema", problema)
            b.putString("Dirección", direccion)
            b.putString("Fecha", fecha)
            b.putString("Fragment", "0")
            b.putString("folio", folio)
            b.putString("Curp", curp)
            val f = AgendaFragment()
            b.putBoolean("urgente", true)*/

            val b = Bundle()
            b.putString("NombreCliente", nombreCliente)
            b.putString("Problema", problema)
            b.putString("Dirección", direccion)
            b.putString("Fragment", "0")
            b.putString("Curp", curp)
            b.putString("folio", folio)

            val f = AgendaFragment()

            b.putBoolean("Historial", false)
            b.putString("folio", folio.toString())
            f.arguments = b
           /* var fm = this().supportFragmentManager.beginTransaction().apply {
                replace(R.id.nav_host_fragment_content_interfaz_kerkly, f).commit()
            }*/

        }

        val botonCompartirUbicacion = findViewById<Button>(R.id.btn_compartir_ubicacion)
        botonCompartirUbicacion.setOnClickListener {
            val intent = Intent(this, MainActivityCompartirUbicacion::class.java)
            startActivity(intent)
        }
    }
    private fun obtenerCliente(uidCliente: String){
        val databaseUsu =instancias.referenciaInformacionDelCliente(uidCliente)
        databaseUsu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val u2 = snapshot.getValue(usuarios::class.java)
                urlfotoCliente = u2!!.foto
                tokenCliente = u2!!.token
                nombreCliente = u2!!.nombre
            }
            override fun onCancelled(error: DatabaseError) {
                println(error)
            }
        })
    }
}