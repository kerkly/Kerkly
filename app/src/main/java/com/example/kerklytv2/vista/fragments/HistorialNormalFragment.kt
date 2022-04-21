package com.example.kerklytv2.vista.fragments

import android.app.Activity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.AdapterHistorialNorm
import com.example.kerklytv2.interfaces.ObtenerHistorialInterface
import com.example.kerklytv2.modelo.serial.HistorialNormal
import com.example.kerklytv2.url.Url
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistorialNormalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistorialNormalFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var context: Activity
    private lateinit var recycler: RecyclerView
    lateinit var MiAdapter: AdapterHistorialNorm
    private lateinit var numeroTelefono: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_historial_normal, container, false)
        context = requireActivity()
        recycler = v.findViewById(R.id.recycler_historialUrgente)
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(context)
        numeroTelefono = arguments?.getString("numNR").toString()

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
        val presupuestoGET = retrofit.create(ObtenerHistorialInterface::class.java)
        val call = presupuestoGET.getPost(numeroTelefono)
        call?.enqueue(object : Callback<List<HistorialNormal?>?> {

            override fun onResponse(
                call: Call<List<HistorialNormal?>?>,
                response: Response<List<HistorialNormal?>?>
            ) {

                val postList: ArrayList<HistorialNormal> = response.body() as
                        ArrayList<HistorialNormal>

                Log.d("Lista", postList.toString())
                MiAdapter = AdapterHistorialNorm(postList)

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
                    val idContrato = postList[recycler.getChildAdapterPosition(it)].idContrato

                    val fecha = postList[recycler.getChildAdapterPosition(it)].Fecha_Inicio
                    val fechaF = postList[recycler.getChildAdapterPosition(it)].Fecha_Final

                    Log.d("fecha", fechaF!!)

                    Log.d("Problema", problema!!)
                    if (ext == "0") {
                        ext = "S/N"
                    }

                    val direccion = "$calle $colonia $ext $cp $referencia \n$ciudad, $estado, $pais"
                    val nombre = "$n $ap $am"

                    Toast.makeText(context, "Teléfono: $colonia",
                        Toast.LENGTH_SHORT).show()

                    val b = Bundle()
                    b.putString("Nombre Cliente NoR", nombre)
                    b.putString("Problema", problema)
                    b.putString("Dirección", direccion)
                    b.putString("Fecha", fecha)
                    b.putInt("Contrato", idContrato)
                    b.putString("Fragment", "1")
                    b.putString("Fecha final", fechaF)

                    val f = AgendaFragment()
                    f.arguments = b
                    var fm = requireActivity().supportFragmentManager.beginTransaction().apply {
                        replace(R.id.nav_host_fragment_content_interfaz_kerkly, f).commit()
                    }

                }

                recycler.adapter = MiAdapter

            }

            override fun onFailure(call: Call<List<HistorialNormal?>?>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show();
            }

        })
    }

}