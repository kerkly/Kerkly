package com.example.kerklytv2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.kerklytv2.Notificacion.llamartopico
import com.example.kerklytv2.controlador.TablaDinamica
import com.example.kerklytv2.interfaces.*
import com.example.kerklytv2.modelo.Pdf
import com.example.kerklytv2.modelo.TablaP
import com.example.kerklytv2.modelo.usuarios
import com.example.kerklytv2.url.Url
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.lowagie.text.*
import com.lowagie.text.pdf.*
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.Response
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

class Presupuesto : AppCompatActivity() {
    private lateinit var tabla: TableLayout
    private lateinit var edit_concepto: TextInputEditText
    private lateinit var edit_pago: TextInputEditText
    private lateinit var layout_pago: TextInputLayout
    private lateinit var layout_concepto: TextInputLayout
    private var header: ArrayList<String> = ArrayList<String>()
    private lateinit var tablaDinamica: TablaDinamica
    private lateinit var arrayAdapter: ArrayAdapter<ArrayList<String>>
    private var folio = 0
    private lateinit var edot_n: TextInputEditText
    private lateinit var layour_n: TextInputLayout
    private lateinit var dialog: Dialog
    private var numero = 0
    private lateinit var edit_descripcion: TextInputEditText
    private lateinit var edit_precio: TextInputEditText
    private lateinit var total_txt: TextView
    private var total = 0.0
    private lateinit var boton: MaterialButton
    private lateinit var problemacliente: String
    private lateinit var clientenombre: String
    private lateinit var direccioncliente: String
    private lateinit var correocliente: String
    lateinit var telefonoCliente: String
    private var TipoServicio = ""

    lateinit var longitudCliente: String
    lateinit var  latitudCliente: String
    private lateinit var b: Bundle
    private lateinit var telefonoK: String


    var databaseReference: DatabaseReference? = null
    var currentDateTimeString: String? = null
    //private val database: FirebaseDatabase? = null

    private lateinit var database: DatabaseReference
    val llamartopico= llamartopico()
    private lateinit var firebaseDatabaseUsu: FirebaseDatabase
    private lateinit var databaseUsu: DatabaseReference
    private lateinit var token: String
    private lateinit var nombrekerkly: String
    private lateinit var CURP: String
    private lateinit var correoKerly: String
    private lateinit var direccionKerly:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presupuesto)

        database = Firebase.database.reference

        b = intent.extras!!
        telefonoCliente  = b.getString("telefonoCliente").toString()
        telefonoK = b.getString("telefonok").toString()
        latitudCliente =  b.getString("latitud") as String
        longitudCliente = b.getString("longitud") as String
        clientenombre = b.getString("nombreCompletoCliente").toString()
        nombrekerkly = b.getString("nombreCompletoKerkly")!!
        problemacliente = b.getString("problema") as String
        direccioncliente = b.getString("direccion").toString()
        correocliente = b.getString("correoCliente")!!
        folio = b.getInt("Folio")
        TipoServicio = b.getString("tipoServicio").toString()
        CURP = b.getString("Curp").toString()
        correoKerly = b.getString("correoKerly").toString()
        direccionKerly = b.getString("direccionKerly").toString()

        //val database = Firebase.database
        //val myRef = database.getReference("message")

        // myRef.setValue("Hello, World!")

        tabla = findViewById(R.id.table)
        edit_concepto = findViewById(R.id.edit_concepto)
        edit_pago = findViewById(R.id.edit_pago)
        layout_concepto = findViewById(R.id.layout_concepto)
        layout_pago = findViewById(R.id.layout_pago)
        total_txt = findViewById(R.id.total_txt)


        edot_n = findViewById(R.id.editEdicion)
        layour_n = findViewById(R.id.layoutEdcion)
        dialog = Dialog(this)

        boton = findViewById(R.id.generarRecibo_btn)

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                1000
            )
        }

        // Generaremos el documento al hacer click sobre el boton.
        boton.setOnClickListener {


            if (TipoServicio == "normal") {
                dbFirebase()
                val p = Pdf(clientenombre,direccionKerly ,folio, TipoServicio, nombrekerkly)
                p.telefono = telefonoK
                p.cabecera = header
                val lista = tablaDinamica.getData()
                p.lista = lista
                p.correo = correoKerly
                p.problema = problemacliente
                p.total = total
                p.generarPdf()
                mandarNormalElPagoTotal(folio, total)


            } else {
                if (TipoServicio == "ServicioNR"){
                    dbFirebase()
                    val p = Pdf(clientenombre, direccionKerly,folio, TipoServicio, nombrekerkly)
                    p.telefono = telefonoK
                    p.cabecera = header
                    val lista = tablaDinamica.getData()
                    p.lista = lista
                    p.correo = ""
                    p.problema = problemacliente
                    p.total = total
                    p.generarPdf()
                    println("tipo: $TipoServicio nombreC: $clientenombre, Direccion: $direccionKerly" +
                            " folio: $folio nombre kerly : $nombrekerkly telefonoClien: $telefonoCliente" +
                            "priblema: $problemacliente total: $total")
                    mandarPagoTotalPresupuestoNR()
                    Toast.makeText(this, "Se creo tu archivo pdf", Toast.LENGTH_SHORT).show()

                }
            }
        }
        tablaDinamica = TablaDinamica(tabla, applicationContext)
        header.add("Item")
        header.add("Descripción")
        header.add("Precio")
        tablaDinamica.addHeader(header)
       // getCoordenadas()
        inicializarTabla()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tablaDinamica.fondoCabecera(getColor(R.color.verdeBajoTabla))
        }
    }

    private fun mandarpagoTotalClienteNR(folio: Int, total: Double) {

    }

    private fun mandarNormalElPagoTotal(folio: Int, total: Double) {
        val ROOT_URL = Url().URL
        val adapter = RestAdapter.Builder()
            .setEndpoint(ROOT_URL)
            .build()
        val api = adapter.create(PrecioNormalInterface::class.java)
        api.MandarPago(folio, total.toString(),
            object : Callback<Response?> {
                override fun success(t: Response?, response: Response?) {
                    var entrada: BufferedReader?=null
                    var R = ""
                    try {
                        entrada = BufferedReader(InputStreamReader(t?.body?.`in`()))
                        R= entrada.readLine()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    if (entrada.toString() == "pago enviado"){
                        Toast.makeText(applicationContext, "Se creo tu archivo pdf", Toast.LENGTH_SHORT).show()
                        //mandar notificacion
                        obtenerToken(telefonoCliente,nombrekerkly, this@Presupuesto )
                        //  llamartopico.llamartopico(this@Presupuesto, "","","")
                        Toast.makeText(applicationContext, R, Toast.LENGTH_SHORT).show()

                    }else{
                        Toast.makeText(applicationContext, "$entrada", Toast.LENGTH_SHORT).show()
                    }

                   
                }

                override fun failure(error: RetrofitError?) {
                    Toast.makeText(applicationContext, "error" + error.toString(), Toast.LENGTH_SHORT).show()
                }

            }
        )
    }

    fun obtenerToken(telefoK: String, nombreK: String, context: Context){
        firebaseDatabaseUsu = FirebaseDatabase.getInstance()
        databaseUsu = firebaseDatabaseUsu.getReference("UsuariosR").child(telefoK).child("MisDatos")
        databaseUsu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val u2 = snapshot.getValue(usuarios::class.java)
                token = u2!!.token
                System.out.println("el token del kerkly " + token)
                llamartopico.llamartopico(context, token, " En un momento se le atendera", "Gracias por su confianza-> $nombreK")
                val intent = Intent(this@Presupuesto, PantallaInicio::class.java)
                startActivity(intent)
            }

            override fun onCancelled(error: DatabaseError) {
                System.out.println("Firebase: $error")
            }

        })

    }

    private fun mandarPagoTotalPresupuestoNR() {
        val ROOT_URL = Url().URL
        val adapter = RestAdapter.Builder()
            .setEndpoint(ROOT_URL)
            .build()
        val api: PagoPInterfaceNR = adapter.create(PagoPInterfaceNR::class.java)
        api.MandarPago(folio, total.toString(), CURP,
            object : Callback<Response?> {
                override fun success(t: Response?, response: Response?) {
                    var entrada: BufferedReader?=null
                    var R = ""
                    try {
                        entrada = BufferedReader(InputStreamReader(t?.body?.`in`()))
                        R= entrada.readLine()
                        Log.d("--------------)", "$folio, $total")
                        Toast.makeText(applicationContext, entrada.toString(), Toast.LENGTH_SHORT).show()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }


                }

                override fun failure(error: RetrofitError?) {
                    Toast.makeText(applicationContext, error.toString(), Toast.LENGTH_SHORT).show()
                }

            }
        )
    }


    fun dbFirebase(){
        val lista = tablaDinamica.getData()
        val num = lista.size

        // Insertamos una tabla.
        for (i in 0 until num) {
            var l = lista[i]

            for (j in 0 until l.size) {
                Log.d("lista", l[j])
                val t = TablaP(l[1], l[2])
                if (TipoServicio == "normal") {
                  //  println("entroooo --->telfono kerkly $telefonoK folio $folio  datos $t 285")
                    database.child("UsuariosR").child(telefonoK).child("Presupuestos Normal").child("Presupuesto Normal $folio").child((i+1).toString()).setValue(t)

                }
                if (TipoServicio == "ServicioNR"){
                    database.child("UsuariosR").child(telefonoK).child("Presupuestos NR").child("Presupuesto NR $folio").child((i+1).toString()).setValue(t)
                }

            }
        }

        // mandarPago()
    }

    private fun inicializarTabla() {

        val lista: ArrayList<ArrayList<String>> = ArrayList()
        tablaDinamica.addData(lista)

    }

    @SuppressLint("SetTextI18n")
    fun save(view:View) {
        val concepto = edit_concepto.text.toString()
        val precio = edit_pago.text.toString()

        if (concepto.isEmpty()) {
            layout_concepto.error = getString(R.string.campoRequerico)
        } else {
            layout_concepto.error = null
        }

        if (precio.isEmpty()) {
            layout_pago.error = getString(R.string.campoRequerico)
        } else {
            layout_pago.error = null
        }

        if(!(concepto.isEmpty() && precio.isEmpty())) {
            val item: ArrayList<String> = ArrayList<String>()
            var num = tablaDinamica.getItems()+1
            item.add("$num")
            item.add(concepto)
            item.add(precio)
            //Log.d("items", "Los items son ${tablaDinamica.getItems()}")
            tablaDinamica.addItems(item)

        }

        total = tablaDinamica.getTotal()
        total_txt.text = "Total: $$total"
        edit_concepto.setText("")
       edit_pago.setText("")
    }

    @SuppressLint("SetTextI18n")
    fun delete(v:View) {
        val s = edot_n.text.toString()

        if (s.isEmpty()) {
            layour_n.error = getString(R.string.campoRequerico)
        } else {
            layour_n.error = null
            numero = s.toInt()
            tablaDinamica.eliminar(numero)
        }

        total = tablaDinamica.getTotal()
        total_txt.text = "Total: $$total"
    }

    fun update(v: View) {
        val s = edot_n.text.toString()
        if (s.isEmpty()) {
            layour_n.error = getString(R.string.campoRequerico)
        } else {
            layour_n.error = null
            numero = s.toInt()
            dialog.setContentView(R.layout.modificar_presupuesto)
            edit_descripcion = dialog.findViewById(R.id.edit_concpetoEmergente)
            edit_precio = dialog.findViewById(R.id.edit_PrecioEmergente)

            dialog.show()
        }
    }


    @SuppressLint("SetTextI18n")
    fun guardar(v:View) {
        tablaDinamica.actualizar(numero,
            edit_descripcion.text.toString(),
            edit_precio.text.toString())

        dialog.dismiss()

        total = tablaDinamica.getTotal()
        total_txt.text = "Total: $$total"
    }
}