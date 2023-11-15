package com.example.kerklytv2.vista.fragments

import android.app.Activity
import android.content.Intent
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
import com.example.kerklytv2.MainActivity_Seguimiento
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.AdapterNormalTrabajos
import com.example.kerklytv2.interfaces.TrabajoNormalnterface
import com.example.kerklytv2.modelo.serial.TrabajoNormal
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
 * Use the [ServicioNormalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ServicioNormalFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var context: Activity
    private lateinit var recycler: RecyclerView
    lateinit var MiAdapter: AdapterNormalTrabajos
    private lateinit var numeroTelefono: String
    private lateinit var b: Bundle
   // private lateinit var img: ImageView
    private var folio = 0
    private lateinit var curp: String
    private lateinit var img: ImageView
    private lateinit var txt: TextView
    lateinit var nombrekerkly:String


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
        val v =  inflater.inflate(R.layout.fragment_servicio_normal, container, false)
        context = requireActivity()
        recycler = v.findViewById(R.id.recycler_trabajoNormal)
      //  img = v.findViewById(R.id.chat_img)
        img = v.findViewById(R.id.img_servicio_normal)
        txt = v.findViewById(R.id.txt_servicio_normal)
        recycler.setHasFixedSize(true)
        recycler.layoutManager = LinearLayoutManager(context)
        b = requireArguments()
        numeroTelefono = b.getString("numNR").toString()
        curp = arguments?.getString("Curp").toString()
        nombrekerkly = arguments?.getString("nombrekerkly").toString()
        getJson()

      /*  img.setOnClickListener {
            Toast.makeText(activity,
                           "Mi folio es $folio",
                            Toast.LENGTH_SHORT).show()
        }*/
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
        val presupuestoGET = retrofit.create(TrabajoNormalnterface::class.java)
        val call = presupuestoGET.getPost(numeroTelefono)
        call?.enqueue(object : Callback<List<TrabajoNormal?>?> {

            override fun onResponse(
                call: Call<List<TrabajoNormal?>?>,
                response: Response<List<TrabajoNormal?>?>
            ) {

                val postList: ArrayList<TrabajoNormal> = response.body() as
                        ArrayList<TrabajoNormal>

                if (postList.size == 0) {
                    recycler.visibility = View.GONE
                } else {
                    img.visibility = View.GONE
                    txt.visibility = View.GONE

                    MiAdapter = AdapterNormalTrabajos(postList, requireActivity())
                    MiAdapter.setOnClickListener {
                        folio = postList[recycler.getChildAdapterPosition(it)].idPresupuesto
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

                       /* Toast.makeText(context, "Teléfono: $colonia",
                            Toast.LENGTH_SHORT).show()*/

                      /*  val b = Bundle()
                        b.putString("Nombre Cliente NoR", nombre)
                        b.putString("Problema", problema)
                        b.putString("Dirección", direccion)
                        b.putString("Fragment", "0")
                        b.putString("Curp", curp)
                        b.putString("idPresupuestoNoRegistrado", idpresupuesto.toString())

                        val f = AgendaFragment()

                        b.putBoolean("Historial", false)
                        b.putString("folio", folio.toString())
                        f.arguments = b
                        var fm = requireActivity().supportFragmentManager.beginTransaction().apply {
                            replace(R.id.nav_host_fragment_content_interfaz_kerkly, f).commit()
                        }*/
                        val intent = Intent(requireContext(), MainActivity_Seguimiento::class.java)
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
                    recycler.adapter = MiAdapter
                }
            }

            override fun onFailure(call: Call<List<TrabajoNormal?>?>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show();
            }

        })
    }


}