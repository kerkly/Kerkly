package com.example.kerklytv2.vista.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.MapsActivity

import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.ClaseAdapterR
import com.example.kerklytv2.controlador.SetProgressDialog
import com.example.kerklytv2.interfaces.PresupuestoInterface
import com.example.kerklytv2.modelo.serial.OficioKerkly

import com.example.kerklytv2.modelo.serial.PresupuestourgentesDatosCliente
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
    private lateinit var curp: String
    private lateinit var telK: String
    private lateinit var img: ImageView
    private lateinit var txt: TextView

    lateinit var postList: ArrayList<PresupuestourgentesDatosCliente>
   // lateinit var autoCompleteTxt: AutoCompleteTextView
    lateinit  var adapterItems: ArrayAdapter<String>
    lateinit var items: String
    lateinit var nombreCompletoKerkly: String
//    lateinit var ofi: MutableList<String>
   // lateinit var postList2: ArrayList<OficioKerkly>
 //   private val setProgressDialog = SetProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        var view = inflater.inflate(R.layout.fragment_presupuesto, container, false)
        context = requireActivity()
        recyclerview = view.findViewById(R.id.recycler_presupuesto)
        img = view.findViewById(R.id.img_presupuesto_normal)
        txt = view.findViewById(R.id.txt_presupuesto_normal)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager= LinearLayoutManager(context)
        val intent = context.intent
        numeroTelefono = arguments?.getString("numNR").toString()

        curp = arguments?.getString("Curp").toString()
        telK = arguments?.getString("numNR").toString()
        nombreCompletoKerkly = arguments?.getString("nombreCompletoKerkly")!!
        //nombrekerkly = arguments?.getString("nombrekerkly").toString()

//        postList2 = arguments?.getSerializable("arrayOfcios") as ArrayList<OficioKerkly>
        //setProgressDialog.setProgressDialog(requireContext())
        //getJSON()
     /*  ofi = mutableListOf()
        for (i in 0 until postList2.size) {
            val oficio = postList2[i].nombreOficio
            ofi.add(oficio)
        }*/
     //   autoCompleteTxt = view.findViewById(R.id.filtro);
      // adapterItems = ArrayAdapter<String>(context, R.layout.list_item, ofi)
       // autoCompleteTxt.setAdapter(adapterItems)
     /*   autoCompleteTxt.onItemClickListener = OnItemClickListener { parent, view, position, id -> items = parent.getItemAtPosition(position).toString()
           setProgressDialog.setProgressDialog(requireContext())

            getJSON()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    fragmentManager?.beginTransaction()?.detach(this@PresupuestoFragment)
                        ?.commitNow();
                    fragmentManager?.beginTransaction()?.attach(this@PresupuestoFragment)
                        ?.commitNow();
                } else {
                    fragmentManager?.beginTransaction()?.detach(this@PresupuestoFragment)
                        ?.attach(this@PresupuestoFragment)
                        ?.commit();
                }

            }*/


        return view
    }

    /*fun  getJSON() {
      //  System.out.println("metodo json $numeroTelefono")
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
        val call = presupuestoGET.getPresupuestoClienteRegistrado(numeroTelefono)
        call?.enqueue(object : Callback<List<PresupuestourgentesDatosCliente?>?> {

            override fun onResponse(call: Call<List<PresupuestourgentesDatosCliente?>?>, response: Response<List<PresupuestourgentesDatosCliente?>?>) {
                postList = response.body() as ArrayList<PresupuestourgentesDatosCliente>
                //carsModels = response.body() as ArrayList<presupuestok>
               // Log.d("Lista", postList.toString())
                MiAdapter = ClaseAdapterR(postList)
                if(postList.size == 0) {
                    recyclerview.visibility = View.GONE
               //     setProgressDialog.dialog!!.dismiss()
                } else {
                    img.visibility = View.GONE
                    txt.visibility = View.GONE

                    MiAdapter.setOnClickListener {
                      val  folioo = postList[recyclerview.getChildAdapterPosition(it)].idPresupuesto
                       val latitud = postList[recyclerview.getChildAdapterPosition(it)].latitud
                        val longitud = postList[recyclerview.getChildAdapterPosition(it)].longitud
                        val colonia = postList[recyclerview.getChildAdapterPosition(it)].Colonia
                        val calle = postList[recyclerview.getChildAdapterPosition(it)].Calle
                        val cp = postList[recyclerview.getChildAdapterPosition(it)].Codigo_Postal
                        val referencia = postList[recyclerview.getChildAdapterPosition(it)].Referencia
                        var ext = postList[recyclerview.getChildAdapterPosition(it)].No_Exterior

                        val n = postList[recyclerview.getChildAdapterPosition(it)].Nombre
                        val ap = postList[recyclerview.getChildAdapterPosition(it)].Apellido_Paterno
                        val am = postList[recyclerview.getChildAdapterPosition(it)].Apellido_Materno
                        val correo = postList[recyclerview.getChildAdapterPosition(it)].Correo
                        val numeroCliente = postList[recyclerview.getChildAdapterPosition(it)].telefonoCliente
                        val problema = postList[recyclerview.getChildAdapterPosition(it)].problema
                        val uidCliente = postList[recyclerview.getChildAdapterPosition(it)].uidCliente
                        if (ext == "0") {
                            ext = "S/N"
                        }
                        direccion = "$calle $colonia $ext $cp $referencia"
                        val Clientenombre = "$n $ap $am"
                        println("Teléfono:------> $numeroCliente, folio $folioo")

                       val i = Intent(context, MapsActivity::class.java)
                        i.putExtra("latitud", latitud)
                        i.putExtra("longitud", longitud)
                        i.putExtra("Folio", folioo.toString())
                        i.putExtra("nombreCompletoCliente", Clientenombre)
                        i.putExtra("Dirección", direccion)
                        i.putExtra("problema", problema)
                        i.putExtra("telefonoCliente", numeroCliente)
                        i.putExtra("tipoServicio", "urgente")
                        i.putExtra("Curp", curp)
                        i.putExtra("telefonok", telK)
                        i.putExtra("correo", correo)
                        i.putExtra("nombreCompletoKerkly", nombreCompletoKerkly)
                        i.putExtra("uidCliente",uidCliente)
                        startActivity(i)

                    }
                    recyclerview.adapter = MiAdapter
                  //  setProgressDialog.dialog!!.dismiss()
                }
            }
            override fun onFailure(call: Call<List<PresupuestourgentesDatosCliente?>?>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show();
            }
        })
    }*/
}
