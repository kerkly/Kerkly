package com.example.kerklytv2.vista

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.MainActivity_Seguimiento
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.AdapterChat
import com.example.kerklytv2.controlador.AdapterNormalTrabajos
import com.example.kerklytv2.controlador.AdapterUrgencia
import com.example.kerklytv2.controlador.SetProgressDialog
import com.example.kerklytv2.interfaces.TrabajoNormalnterface
import com.example.kerklytv2.interfaces.TrabajoUrgenteInterface
import com.example.kerklytv2.modelo.serial.TrabajoNormal
import com.example.kerklytv2.modelo.serial.TrabajoUrgencia
import com.example.kerklytv2.url.Url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivityMostrarTrabajosPendientes : AppCompatActivity() {
    private lateinit var TipoServicio:String
    private lateinit var recycler: RecyclerView
    private lateinit var b: Bundle

    private lateinit var telefonoKerkly:String
    private lateinit var curp:String
    private lateinit var nombrekerkly:String

    private lateinit var img: ImageView
    private lateinit var txtError: TextView

    lateinit var MiAdapterNormal: AdapterNormalTrabajos
    lateinit var MiAdapterUrgente: AdapterUrgencia
    private lateinit var setProgress: SetProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_mostrar_trabajos_pendientes)

        recycler = findViewById(R.id.recycler_trabajosP)
        img = findViewById(R.id.img_servicio)
        txtError = findViewById(R.id.txt_servicio)

        setProgress  = SetProgressDialog()
        b = intent.extras!!
        TipoServicio = b.getString("tipoServicio").toString()
        telefonoKerkly = b.getString("telefonoKerkly").toString()
        curp = b.getString("Curp").toString()
        nombrekerkly = b.getString("nombrekerkly").toString()
        println("tel $telefonoKerkly curp $curp nom $nombrekerkly")
        if (TipoServicio == "normal"){

            obtenerTrabajosPendientesNormal()
        }
        if (TipoServicio == "urgente"){
            obtenerTrabajosPendientesUrgente()
        }
    }

    private fun obtenerTrabajosPendientesUrgente() {
        setProgress.setProgressDialog(this)
        val ROOT_URL = Url().URL
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("$ROOT_URL/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val presupuestoGET = retrofit.create(TrabajoUrgenteInterface::class.java)
        val call = presupuestoGET.getPost(telefonoKerkly)
        call?.enqueue(object : Callback<List<TrabajoUrgencia?>?> {
            override fun onResponse(
                call: Call<List<TrabajoUrgencia?>?>,
                response: Response<List<TrabajoUrgencia?>?>
            ) {
                val postList: ArrayList<TrabajoUrgencia> = response.body() as
                        ArrayList<TrabajoUrgencia>

                if (postList.size == 0) {
                    recycler.visibility = View.GONE
                    img.visibility = View.VISIBLE
                    txtError.visibility = View.VISIBLE
                } else {
                    recycler.visibility = View.VISIBLE
                    img.visibility = View.GONE
                    txtError.visibility = View.GONE

                    MiAdapterUrgente = AdapterUrgencia(postList)
                    recycler.layoutManager = LinearLayoutManager(this@MainActivityMostrarTrabajosPendientes)
                    recycler.adapter = MiAdapterUrgente

                    MiAdapterUrgente.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                            super.onItemRangeInserted(positionStart, itemCount)
                            setScrollBar()
                        }
                    })
                    MiAdapterUrgente.setOnClickListener {
                        val folio = postList[recycler.getChildAdapterPosition(it)].idPresupuesto
                        val colonia = postList[recycler.getChildAdapterPosition(it)].Colonia
                        val calle = postList[recycler.getChildAdapterPosition(it)].Calle
                        val cp = postList[recycler.getChildAdapterPosition(it)].Codigo_Postal
                        val referencia = postList[recycler.getChildAdapterPosition(it)].Referencia
                        var ext = postList[recycler.getChildAdapterPosition(it)].No_Exterior

                        val n = postList[recycler.getChildAdapterPosition(it)].Nombre
                        val ap = postList[recycler.getChildAdapterPosition(it)].Apellido_Paterno
                        val am = postList[recycler.getChildAdapterPosition(it)].Apellido_Materno

                        val numero = postList[recycler.getChildAdapterPosition(it)].telefonoCliente

                        val problema = postList[recycler.getChildAdapterPosition(it)].problema
                        val ciudad = postList[recycler.getChildAdapterPosition(it)].Ciudad
                        val estado = postList[recycler.getChildAdapterPosition(it)].Estado
                        val pais = postList[recycler.getChildAdapterPosition(it)].Pais
                        val correoCliente = postList[recycler.getChildAdapterPosition(it)].Correo
                        val uidCliente = postList[recycler.getChildAdapterPosition(it)].uidCliente
                        val fecha = postList[recycler.getChildAdapterPosition(it)].fechaP

                        Log.d("Problema", problema!!)
                        if (ext == "0") {
                            ext = "S/N"
                        }
                        val direccion = "$calle $colonia $ext $cp $referencia \n$ciudad, $estado, $pais"
                        val nombre = "$n $ap $am"
                        val intent = Intent(this@MainActivityMostrarTrabajosPendientes, MainActivity_Seguimiento::class.java)
                        intent.putExtra("NombreCliente", nombre)
                        intent.putExtra("telefonoCliente", numero)
                        intent.putExtra("uidCliente", uidCliente)
                        intent.putExtra("correoCliente", correoCliente)
                        intent.putExtra("Problema", problema)
                        intent.putExtra("Dirección", direccion)
                        intent.putExtra("Fecha", fecha)
                        intent.putExtra("Fragment", "0")
                        intent.putExtra("folio", folio)
                        intent.putExtra("Curp", curp)
                        startActivity(intent)
                    }
                    recycler.adapter = MiAdapterUrgente
                    setProgress.dialog!!.dismiss()
                }
            }

            override fun onFailure(call: Call<List<TrabajoUrgencia?>?>, t: Throwable) {
                Toast.makeText(this@MainActivityMostrarTrabajosPendientes, "Codigo de respuesta de error: $t", Toast.LENGTH_SHORT).show();
                setProgress.dialog!!.dismiss()
                recycler.visibility = View.GONE
                img.visibility = View.VISIBLE
                txtError.visibility = View.VISIBLE
            }

        })
    }

    private fun obtenerTrabajosPendientesNormal() {
        setProgress.setProgressDialog(this)
        val ROOT_URL = Url().URL
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("$ROOT_URL/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val presupuestoGET = retrofit.create(TrabajoNormalnterface::class.java)
        val call = presupuestoGET.getPost(telefonoKerkly)
        call?.enqueue(object : Callback<List<TrabajoNormal?>?> {
            override fun onResponse(
                call: Call<List<TrabajoNormal?>?>,
                response: Response<List<TrabajoNormal?>?>
            ) {
                val postList: ArrayList<TrabajoNormal> = response.body() as
                        ArrayList<TrabajoNormal>

                if (postList.size == 0) {
                    recycler.visibility = View.GONE
                    img.visibility = View.VISIBLE
                    txtError.visibility = View.VISIBLE
                } else {
                    recycler.visibility = View.VISIBLE
                    img.visibility = View.GONE
                    txtError.visibility = View.GONE

                    MiAdapterNormal = AdapterNormalTrabajos(postList, this@MainActivityMostrarTrabajosPendientes)
                    recycler.layoutManager = LinearLayoutManager(this@MainActivityMostrarTrabajosPendientes)
                    recycler.adapter = MiAdapterNormal

                    MiAdapterNormal.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
                        override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                            super.onItemRangeInserted(positionStart, itemCount)
                            setScrollBar()
                        }
                    })
                    MiAdapterNormal.setOnClickListener {
                        val folio = postList[recycler.getChildAdapterPosition(it)].idPresupuesto
                        val colonia = postList[recycler.getChildAdapterPosition(it)].Colonia
                        val calle = postList[recycler.getChildAdapterPosition(it)].Calle
                        val cp = postList[recycler.getChildAdapterPosition(it)].Codigo_Postal
                        val referencia = postList[recycler.getChildAdapterPosition(it)].Referencia
                        var ext = postList[recycler.getChildAdapterPosition(it)].No_Exterior

                        val n = postList[recycler.getChildAdapterPosition(it)].Nombre
                        val ap = postList[recycler.getChildAdapterPosition(it)].Apellido_Paterno
                        val am = postList[recycler.getChildAdapterPosition(it)].Apellido_Materno

                        val numero = postList[recycler.getChildAdapterPosition(it)].telefonoCliente

                        val problema = postList[recycler.getChildAdapterPosition(it)].problema
                        val ciudad = postList[recycler.getChildAdapterPosition(it)].Ciudad
                        val estado = postList[recycler.getChildAdapterPosition(it)].Estado
                        val pais = postList[recycler.getChildAdapterPosition(it)].Pais
                        val correoCliente = postList[recycler.getChildAdapterPosition(it)].Correo
                        val uidCliente = postList[recycler.getChildAdapterPosition(it)].uidCliente
                        val fecha = postList[recycler.getChildAdapterPosition(it)].fechaP

                        Log.d("Problema", problema!!)
                        if (ext == "0") {
                            ext = "S/N"
                        }
                        val direccion = "$calle $colonia $ext $cp $referencia \n$ciudad, $estado, $pais"
                        val nombre = "$n $ap $am"
                        val intent = Intent(this@MainActivityMostrarTrabajosPendientes, MainActivity_Seguimiento::class.java)
                        intent.putExtra("NombreCliente", nombre)
                        intent.putExtra("telefonoCliente", numero)
                        intent.putExtra("uidCliente", uidCliente)
                        intent.putExtra("correoCliente", correoCliente)
                        intent.putExtra("Problema", problema)
                        intent.putExtra("Dirección", direccion)
                        intent.putExtra("Fecha", fecha)
                        intent.putExtra("Fragment", "0")
                        intent.putExtra("folio", folio)
                        intent.putExtra("Curp", curp)
                        startActivity(intent)
                    }
                    recycler.adapter = MiAdapterNormal
                    setProgress.dialog!!.dismiss()
                }
            }

            override fun onFailure(call: Call<List<TrabajoNormal?>?>, t: Throwable) {
                Toast.makeText(this@MainActivityMostrarTrabajosPendientes, "Codigo de respuesta de error: $t", Toast.LENGTH_SHORT).show();
                setProgress.dialog!!.dismiss()
                recycler.visibility = View.GONE
                img.visibility = View.VISIBLE
                txtError.visibility = View.VISIBLE
            }

        })
    }
    private fun setScrollBar() {
        if (TipoServicio == "normal"){
            recycler.scrollToPosition(MiAdapterNormal.itemCount-1)
        }

        if (TipoServicio == "urgente"){
            recycler.scrollToPosition(MiAdapterUrgente.itemCount-1)
        }

    }
}