package com.example.kerklytv2.vista.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.Presupuesto
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.ClaseAdapterR
import com.example.kerklytv2.interfaces.PresupuestoInterface
import com.example.kerklytv2.modelo.PresupuestoDatos
import com.example.kerklytv2.url.Url
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import okhttp3.OkHttpClient

import okhttp3.logging.HttpLoggingInterceptor

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PresupuestoFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PresupuestoFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var context: Activity
    lateinit var MiAdapter: ClaseAdapterR
    lateinit var recyclerview: RecyclerView
    private lateinit var numeroTelefono: String
    private var folio = 0
    private lateinit var direccion: String
    private lateinit var nombre: String

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
        var view = inflater.inflate(R.layout.fragment_presupuesto, container, false)
        context = requireActivity()
        recyclerview = view.findViewById(R.id.recycler_presupuesto)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager= LinearLayoutManager(context)
        val intent = context.intent
        numeroTelefono = arguments?.getString("numNR").toString()
        getJSON()
        return view
    }


    fun getJSON() {
        val ROOT_URL = Url().URL
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("$ROOT_URL/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val presupuestoGET = retrofit.create(PresupuestoInterface::class.java)
        val call = presupuestoGET.getPost(numeroTelefono)
        call?.enqueue(object : Callback<List<PresupuestoDatos?>?> {

            override fun onResponse(
                call: Call<List<PresupuestoDatos?>?>,
                response: Response<List<PresupuestoDatos?>?>
            ) {

                val postList: ArrayList<PresupuestoDatos> = response.body() as ArrayList<PresupuestoDatos>

                //carsModels = response.body() as ArrayList<presupuestok>
                Log.d("Lista", postList.toString())
                MiAdapter = ClaseAdapterR(postList)

                MiAdapter.setOnClickListener {
                    folio = postList[recyclerview.getChildAdapterPosition(it)].idPresupuestoNoRegistrado
                    val colonia = postList[recyclerview.getChildAdapterPosition(it)].Colonia
                    val calle = postList[recyclerview.getChildAdapterPosition(it)].Calle
                    val cp = postList[recyclerview.getChildAdapterPosition(it)].Codigo_Postal
                    val referencia = postList[recyclerview.getChildAdapterPosition(it)].Referencia
                    var ext = postList[recyclerview.getChildAdapterPosition(it)].No_Exterior

                    val n = postList[recyclerview.getChildAdapterPosition(it)].nombre_noR
                    val ap = postList[recyclerview.getChildAdapterPosition(it)].apellidoP_noR
                    val am = postList[recyclerview.getChildAdapterPosition(it)].apellidoM_noR

                    val numero = postList[recyclerview.getChildAdapterPosition(it)].idNoRTelefono

                    val problema = postList[recyclerview.getChildAdapterPosition(it)].problema


                    if (ext == "0") {
                        ext = "S/N"
                    }

                    direccion = "$calle $colonia $ext $cp $referencia"
                    nombre = "$n $ap $am"

                    Toast.makeText(context, "Teléfono: $colonia",
                        Toast.LENGTH_SHORT).show()

                    val i = Intent(context, Presupuesto::class.java)
                    i.putExtra("Folio", folio)
                    i.putExtra("Nombre", nombre)
                    i.putExtra("Dirección", direccion)
                    i.putExtra("Problema", problema)
                    i.putExtra("Número", numero)
                    startActivity(i)
                }

                recyclerview.adapter = MiAdapter
            }

            override fun onFailure(call: Call<List<PresupuestoDatos?>?>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show();
            }

        })
    }
}