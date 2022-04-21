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
import com.example.kerklytv2.controlador.AdapterHistorialUrg
import com.example.kerklytv2.interfaces.ObtenerHIstorialUrgenteInterface
import com.example.kerklytv2.modelo.serial.HistorialUrgencia
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
 * Use the [HistorialUrgenteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistorialUrgenteFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null

    private lateinit var context: Activity
    private lateinit var recycler: RecyclerView
    lateinit var MiAdapter: AdapterHistorialUrg
    private lateinit var numeroTelefono: String
    private var param2: String? = null

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
        val v =  inflater.inflate(R.layout.fragment_historial_urgente, container, false)
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
        val presupuestoGET = retrofit.create(ObtenerHIstorialUrgenteInterface::class.java)
        val call = presupuestoGET.getPost(numeroTelefono)
        call?.enqueue(object : Callback<List<HistorialUrgencia?>?> {

            override fun onResponse(
                call: Call<List<HistorialUrgencia?>?>,
                response: Response<List<HistorialUrgencia?>?>
            ) {

                val postList: ArrayList<HistorialUrgencia> = response.body() as
                        ArrayList<HistorialUrgencia>

                Log.d("Lista", postList.toString())
                MiAdapter = AdapterHistorialUrg(postList)

                MiAdapter.setOnClickListener {
                    val folio = postList[recycler.getChildAdapterPosition(it)].idPresupuestoNoRegistrado
                    val colonia = postList[recycler.getChildAdapterPosition(it)].Colonia
                    val calle = postList[recycler.getChildAdapterPosition(it)].Calle
                    val cp = postList[recycler.getChildAdapterPosition(it)].Codigo_Postal
                    val referencia = postList[recycler.getChildAdapterPosition(it)].Referencia
                    var ext = postList[recycler.getChildAdapterPosition(it)].No_Exterior

                    val n = postList[recycler.getChildAdapterPosition(it)].nombre_noR
                    val ap = postList[recycler.getChildAdapterPosition(it)].apellidoP_noR
                    val am = postList[recycler.getChildAdapterPosition(it)].apellidoM_noR

                    val numero = postList[recycler.getChildAdapterPosition(it)].telefono_NoR

                    val problema = postList[recycler.getChildAdapterPosition(it)].problema
                    val ciudad = postList[recycler.getChildAdapterPosition(it)].Ciudad
                    val estado = postList[recycler.getChildAdapterPosition(it)].Estado
                    val pais = postList[recycler.getChildAdapterPosition(it)].Pais
                    val idContrato = postList[recycler.getChildAdapterPosition(it)].idContraNoRegistrado

                    val fecha = postList[recycler.getChildAdapterPosition(it)].Fecha_Inicio_NoRegistrado
                    val fechaF = postList[recycler.getChildAdapterPosition(it)].Fecha_Final_NoRegistrado

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

            override fun onFailure(call: Call<List<HistorialUrgencia?>?>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show();
            }

        })
    }

}