package com.example.kerklytv2

import android.Manifest
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.TableLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.kerklytv2.controlador.TablaDinamica
import com.example.kerklytv2.interfaces.*
import com.example.kerklytv2.modelo.Pdf
import com.example.kerklytv2.modelo.TablaP
import com.example.kerklytv2.modelo.serial.CoordenadasKerkly
import com.example.kerklytv2.modelo.serial.OficioKerkly
import com.example.kerklytv2.url.Url
import com.example.kerklytv2.vista.InterfazKerkly
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.lowagie.text.*
import com.lowagie.text.pdf.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.Response
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.*
import java.text.SimpleDateFormat
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
    private var problema = "Mi problema"
    private var cliente = "José Luis López Durán"
    private var direccion = "Mi direccion"
    private var telefono = "7474747474"
    private var correo = "josem_rl@hotmail.com"
    private var band = false
    private lateinit var btn_ubicacion: MaterialButton
    private lateinit var curp: String
    private var latitud = 0.0
    private var longitud = 0.0
    lateinit var longitudCliente: String
    lateinit var  latitudCliente: String
    private lateinit var b: Bundle
    private lateinit var telK: String


    var databaseReference: DatabaseReference? = null
    var currentDateTimeString: String? = null
    //private val database: FirebaseDatabase? = null

    private lateinit var database: DatabaseReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_presupuesto)

        database = Firebase.database.reference
        //val database = Firebase.database
        //val myRef = database.getReference("message")

        // myRef.setValue("Hello, World!")

        tabla = findViewById(R.id.table)
        edit_concepto = findViewById(R.id.edit_concepto)
        edit_pago = findViewById(R.id.edit_pago)
        layout_concepto = findViewById(R.id.layout_concepto)
        layout_pago = findViewById(R.id.layout_pago)
        total_txt = findViewById(R.id.total_txt)
        btn_ubicacion = findViewById(R.id.btn_ubicacion)

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
            dbFirebase()
            val p = Pdf(cliente, direccion)
            p.telefono = telefono
            p.cabecera = header
            val lista = tablaDinamica.getData()
            p.lista = lista
            p.correo = correo
            p.problema = problema
            p.folio = folio
            p.total = total
            p.generarPdf()
            //generarPdf()
            Toast.makeText(this, "Se creo tu archivo pdf", Toast.LENGTH_SHORT).show()

            if (band) {
                mandarNormalPago()
            } else {
                mandarPago()
            }

            val i = Intent(this, InterfazKerkly::class.java)
            i.putExtras(b)
            startActivity(i)

        }

        b = intent.extras!!
        latitudCliente =  b.get("latitud") as String
        longitudCliente = b.get("longitud") as String
        folio = b.get("Folio") as Int
        problema = b.get("Problema") as String
        cliente = b.getString("Nombre").toString()
        direccion = b.get("Dirección").toString()
        telefono = b.get("Número").toString()
       // System.out.println("$folio clase presupuesto $telefono")
        band = b.getBoolean("Normal")
        curp = b.getString("Curp").toString()
        telK = b.getString("numT").toString()
        Log.d("telefono kerkly", telK)


        tablaDinamica = TablaDinamica(tabla, applicationContext)
        header.add("Item")
        header.add("Descripción")
        header.add("Precio")
        tablaDinamica.addHeader(header)

        btn_ubicacion.setOnClickListener {

            val i = Intent(this, MapsActivity::class.java)
            i.putExtra("Telefono", telefono)
            i.putExtra("Normal", band)
            i.putExtra("latitud", latitudCliente)
            i.putExtra("longitud", longitudCliente)
           //System.out.println("clase presupuesto $cliente $latitudCliente $longitudCliente")
            i.putExtra("Nombre", cliente)
            i.putExtra("Folio", folio)
            startActivity(i)
        }

       // getCoordenadas()

        inicializarTabla()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            tablaDinamica.fondoCabecera(getColor(R.color.verdeBajoTabla))
        }
    }

    private fun mandarNormalPago() {
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
                    Toast.makeText(applicationContext, R.toString(), Toast.LENGTH_SHORT).show()

                }

                override fun failure(error: RetrofitError?) {
                    Toast.makeText(applicationContext, "error" + error.toString(), Toast.LENGTH_SHORT).show()
                }

            }
        )
    }

    private fun mandarPago() {
        val ROOT_URL = Url().URL
        val adapter = RestAdapter.Builder()
            .setEndpoint(ROOT_URL)
            .build()
        val api: PagoPInterface = adapter.create(PagoPInterface::class.java)
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
                    Toast.makeText(applicationContext, R.toString(), Toast.LENGTH_SHORT).show()

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
                if (band) {
                    database.child("Presupuesto Normal $folio").child((i+1).toString()).setValue(t)
                } else {
                    database.child("Presupuesto $folio").child((i+1).toString()).setValue(t)
                }


            }
        }
        Log.d("Bieeen","todo bien")
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

    private fun getCoordenadas() {
        val ROOT_URL = Url().URL
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("$ROOT_URL/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val coordenadas = retrofit.create(ObtenerCoordenadasKekrly::class.java)
        val call = coordenadas.getCoordenadas(curp)

        call?.enqueue(object : retrofit2.Callback<List<CoordenadasKerkly?>?> {
            override fun onResponse(
                call: Call<List<CoordenadasKerkly?>?>,
                response: retrofit2.Response<List<CoordenadasKerkly?>?>
            ) {
                val postList: ArrayList<CoordenadasKerkly> = response.body() as ArrayList<CoordenadasKerkly>
                longitud = postList[0].longitud!!
                latitud = postList[0].latitud!!

                Log.d("latitud", "$latitud")
                Log.d("longitud", "$longitud")


            }

            override fun onFailure(call: Call<List<CoordenadasKerkly?>?>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show();
            }

        })
    }


    /*  fun generarRecibo (v: View) {
          mandarPago()
      }*/

}