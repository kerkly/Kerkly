package com.example.kerklytv2.vista.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.AdapterUrgencia
import com.example.kerklytv2.interfaces.TrabajoUrgenteInterface
import com.example.kerklytv2.modelo.serial.TrabajoUrgencia
import com.example.kerklytv2.url.Url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TrabajosUrgenciaFragment : Fragment() {

    private lateinit var context: Activity
    private lateinit var recycler: RecyclerView
    lateinit var MiAdapter: AdapterUrgencia
    private lateinit var numeroTelefono: String
    private lateinit var curp: String
    private lateinit var img: ImageView
    private lateinit var txt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_trabajos_urgencia, container, false)
        context = requireActivity()
        recycler = v.findViewById(R.id.recycler_trabajos_urgencia_rw)
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(context)
        numeroTelefono = arguments?.getString("numNR").toString()
        curp = arguments?.getString("Curp").toString()



        img = v.findViewById(R.id.img_servicio_urgente)
        txt = v.findViewById(R.id.txt_servicio_urgente)
        getJson()
        return v
    }


    fun getJson() {
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
        val call = presupuestoGET.getPost(numeroTelefono)
        call?.enqueue(object : Callback<List<TrabajoUrgencia?>?> {

            override fun onResponse(call: Call<List<TrabajoUrgencia?>?>, response: Response<List<TrabajoUrgencia?>?>) {
                val postList: ArrayList<TrabajoUrgencia> = response.body() as
                        ArrayList<TrabajoUrgencia>

                if(postList.size == 0) {
                    recycler.visibility = View.GONE

                } else {
                    img.visibility = View.GONE
                    txt.visibility = View.GONE

                    MiAdapter = AdapterUrgencia(postList)

                    MiAdapter.setOnClickListener {
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
                        val correo = postList[recycler.getChildAdapterPosition(it)].Correo
                        val fecha = postList[recycler.getChildAdapterPosition(it)].fechaP
                        val uidCliente = postList[recycler.getChildAdapterPosition(it)].uidCliente

                        Log.d("Problema", problema!!)
                        if (ext == "0") {
                            ext = "S/N"
                        }

                        val direccion = "$calle $colonia $ext $cp $referencia \n$ciudad, $estado, $pais"
                        val nombre = "$n $ap $am"


                        val b = Bundle()
                        b.putString("NombreCliente", nombre)
                        b.putString("telefonoCliente", numero)
                        b.putString("uidCliente", uidCliente)
                        b.putString("correoCliente", correo)
                        b.putString("Problema", problema)
                        b.putString("Dirección", direccion)
                        b.putString("Fecha", fecha)
                        b.putString("Fragment", "0")
                        b.putString("folio", folio)
                        b.putString("Curp", curp)
                       /* val f = AgendaFragment()
                        b.putBoolean("urgente", true)
                        f.arguments = b
                        var fm = requireActivity().supportFragmentManager.beginTransaction().apply {
                            replace(R.id.nav_host_fragment_content_interfaz_kerkly, f).commit()
                        }*/
                        val fragment = BlankFragmentSeguimiento()
                        var f = requireActivity().supportFragmentManager.beginTransaction().apply {
                            fragment.arguments = b
                            replace(R.id.nav_host_fragment_content_interfaz_kerkly, fragment).commit()
                        }
                    }

                    recycler.adapter = MiAdapter
                }

            }

            override fun onFailure(call: Call<List<TrabajoUrgencia?>?>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show();
            }

        })
    }
}