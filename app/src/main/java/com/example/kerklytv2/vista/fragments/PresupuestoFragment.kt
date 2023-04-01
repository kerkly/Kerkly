package com.example.kerklytv2.vista.fragments

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.AdapterView.OnItemClickListener
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.MainActivitySeguimientoDelServicio
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.ClaseAdapterR
import com.example.kerklytv2.interfaces.PresupuestoInterface
import com.example.kerklytv2.interfaces.obtenerTodosLosOficios
import com.example.kerklytv2.modelo.serial.OficioKerkly

import com.example.kerklytv2.modelo.serial.PresupuestoDatosClienteRegistrado
import com.example.kerklytv2.modelo.serial.todosLosOficios
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

    lateinit var postList: ArrayList<PresupuestoDatosClienteRegistrado>
    lateinit var autoCompleteTxt: AutoCompleteTextView
    lateinit  var adapterItems: ArrayAdapter<String>
    lateinit var items: String
    lateinit var nombrekerkly: String
    lateinit var ofi: MutableList<String>
    lateinit var postList2: ArrayList<OficioKerkly>


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
        nombrekerkly = arguments?.getString("nombrekerkly").toString()

        postList2 = arguments?.getSerializable("arrayOfcios") as ArrayList<OficioKerkly>

       ofi = mutableListOf()
        for (i in 0 until postList2.size) {
            val oficio = postList2[i].nombreOficio
            ofi.add(oficio)
        }

        autoCompleteTxt = view.findViewById(R.id.filtro);
        adapterItems = ArrayAdapter<String>(context, R.layout.list_item, ofi)
        autoCompleteTxt.setAdapter(adapterItems)
        autoCompleteTxt.onItemClickListener = OnItemClickListener { parent, view, position, id -> items = parent.getItemAtPosition(position).toString()
                Toast.makeText(context, "oficio: $items", Toast.LENGTH_SHORT).show()
                //  getJSON(items.toString())
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

               getJSON()

            }



        //primero se obtendra todos los oficios que tiene el kerkly
/*
      val ROOT_URL = Url().URL
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()
        val retrofit = Retrofit.Builder()
            .baseUrl("$ROOT_URL/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val oficios = retrofit.create(obtenerTodosLosOficios::class.java)
        val call2 = oficios.getPost(numeroTelefono)
        call2?.enqueue(object : Callback<List<todosLosOficios?>?>{
            override fun onResponse(call: Call<List<todosLosOficios?>?>, response: Response<List<todosLosOficios?>?>) {
                val postList: ArrayList<todosLosOficios> =
                    response.body() as ArrayList<todosLosOficios>


                ofi = mutableListOf()
                for (i in 0 until postList.size) {
                    val oficio = postList[i].nombreO
                    System.out.println("oficios obtenidos " + oficio)
                    ofi.add(oficio.toString())
                }


                //  System.out.println("tamaño del array " + items1!!.get(1))
                autoCompleteTxt = view.findViewById(R.id.filtro);
                adapterItems = ArrayAdapter<String>(context, R.layout.list_item, ofi)
                autoCompleteTxt.setAdapter(adapterItems)
                autoCompleteTxt.onItemClickListener =
                    OnItemClickListener { parent, view, position, id ->
                        items = parent.getItemAtPosition(position).toString()
                        Toast.makeText(context, "oficio: $items", Toast.LENGTH_SHORT).show()
                        //  getJSON(items.toString())
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

                        getJSON()

                    }

            }
          override fun onFailure(call: Call<List<todosLosOficios?>?>, t: Throwable) {
                Toast.makeText(context, "Codigo de respuesta de error: $t", Toast.LENGTH_SHORT).show();
            }


        })*/

        return view
    }




    fun  getJSON() {
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
        val call = presupuestoGET.getPresupuestoClienteRegistrado(numeroTelefono, items)
        call?.enqueue(object : Callback<List<PresupuestoDatosClienteRegistrado?>?> {

            override fun onResponse(call: Call<List<PresupuestoDatosClienteRegistrado?>?>, response: Response<List<PresupuestoDatosClienteRegistrado?>?>) {

                postList = response.body() as ArrayList<PresupuestoDatosClienteRegistrado>

                //carsModels = response.body() as ArrayList<presupuestok>
               // Log.d("Lista", postList.toString())
                MiAdapter = ClaseAdapterR(postList)

                if(postList.size == 0) {
                    recyclerview.visibility = View.GONE
                } else {
                    img.visibility = View.GONE
                    txt.visibility = View.GONE

                    MiAdapter.setOnClickListener {
                        folio = postList[recyclerview.getChildAdapterPosition(it)].idPresupuestoNoRegistrado
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


                        if (ext == "0") {
                            ext = "S/N"
                        }

                        direccion = "$calle $colonia $ext $cp $referencia"
                        nombre = "$n $ap $am"

                        //Toast.makeText(context, "Teléfono: $numeroCliente", Toast.LENGTH_SHORT).show()

                        //cambios de diseño
                       // val i = Intent(context, Presupuesto::class.java)
                  /*      val i = Intent(context, MapsActivity::class.java)
                        i.putExtra("latitud", latitud)
                        i.putExtra("longitud", longitud)
                        i.putExtra("Folio", folio)
                        i.putExtra("Nombre", nombre)
                        i.putExtra("Dirección", direccion)
                        i.putExtra("Problema", problema)
                        i.putExtra("Número", numero)
                        i.putExtra("Normal", false)
                        i.putExtra("Curp", curp)
                        i.putExtra("numT", telK)
                        i.putExtra("correo", correo)
                        i.putExtra("nombrekerkly", nombrekerkly)
                        startActivity(i)*/
                       val i = Intent(context, MainActivitySeguimientoDelServicio::class.java)
                        i.putExtra("latitud", latitud)
                        i.putExtra("longitud", longitud)
                        i.putExtra("Folio", folio)
                        i.putExtra("Nombre", nombre)
                        i.putExtra("Dirección", direccion)
                        i.putExtra("Problema", problema)
                        i.putExtra("numeroCliente", numeroCliente)
                        i.putExtra("Normal", false)
                        i.putExtra("Curp", curp)
                        i.putExtra("numeroKerkly", telK)
                        i.putExtra("correo", correo)
                        i.putExtra("nombrekerkly", nombrekerkly)
                        startActivity(i)

                    }

                    recyclerview.adapter = MiAdapter
                }

            }

            override fun onFailure(call: Call<List<PresupuestoDatosClienteRegistrado?>?>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show();
            }

        })
    }

}
