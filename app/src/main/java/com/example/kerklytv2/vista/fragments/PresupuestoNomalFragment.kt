package com.example.kerklytv2.vista.fragments

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.Presupuesto
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.AdapterPresupuesto
import com.example.kerklytv2.interfaces.PresupuestoNormalInterface
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
 * Use the [PresupuestoNomalFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PresupuestoNomalFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var context: Activity
    lateinit var MiAdapter: AdapterPresupuesto
    lateinit var recyclerview: RecyclerView
    private lateinit var numeroTelefono: String
    private var folio = 0
    private lateinit var direccion: String
    private lateinit var nombre: String
    private lateinit var curp: String
    private lateinit var telK: String
    private lateinit var img: ImageView
    private lateinit var txt: TextView

    private lateinit var dialog: Dialog
    private lateinit var progressBar: ProgressBar


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
        val v = inflater.inflate(R.layout.fragment_presupuesto_nomal, container, false)
        context = requireActivity()
        recyclerview = v.findViewById(R.id.recycler_presupuesto_normal)
        img = v.findViewById(R.id.img_presupuesto_urgente)
        txt = v.findViewById(R.id.txt_presupuesto_urgente)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(context)
       // val intent = context.intent
        numeroTelefono = arguments?.getString("numNR").toString()
        Toast.makeText(context, "Teléfono: $numeroTelefono", Toast.LENGTH_SHORT).show()

        curp = arguments?.getString("Curp").toString()
        telK = arguments?.getString("numNR").toString()
        setProgressDialog()
        getJSON()
        return v
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
        val presupuestoGET = retrofit.create(PresupuestoNormalInterface::class.java)
        val call = presupuestoGET.getPost(numeroTelefono)
        call?.enqueue(object : Callback<List<com.example.kerklytv2.modelo.serial.Presupuesto?>?> {

            override fun onResponse(
                call: Call<List<com.example.kerklytv2.modelo.serial.Presupuesto?>?>,
                response: Response<List<com.example.kerklytv2.modelo.serial.Presupuesto?>?>
            ) {

                val postList: ArrayList<com.example.kerklytv2.modelo.serial.Presupuesto> =
                    response.body() as ArrayList<com.example.kerklytv2.modelo.serial.Presupuesto>

               if(postList.size == 0) {
                   recyclerview.visibility = View.GONE
                   dialog.dismiss()
               } else {
                 dialog.dismiss()
                   img.visibility = View.GONE
                   txt.visibility = View.GONE

                   MiAdapter = AdapterPresupuesto(postList)

                   MiAdapter.setOnClickListener {
                       val latitud = postList[recyclerview.getChildAdapterPosition(it)].latitud
                       val longitud = postList[recyclerview.getChildAdapterPosition(it)].longitud
                       folio = postList[recyclerview.getChildAdapterPosition(it)].idPresupuesto
                       val colonia = postList[recyclerview.getChildAdapterPosition(it)].Colonia
                       val calle = postList[recyclerview.getChildAdapterPosition(it)].Calle
                       val cp = postList[recyclerview.getChildAdapterPosition(it)].Codigo_Postal
                       val referencia = postList[recyclerview.getChildAdapterPosition(it)].Referencia
                       var ext = postList[recyclerview.getChildAdapterPosition(it)].No_Exterior

                       val n = postList[recyclerview.getChildAdapterPosition(it)].Nombre
                       val ap = postList[recyclerview.getChildAdapterPosition(it)].Apellido_Paterno
                       val am = postList[recyclerview.getChildAdapterPosition(it)].Apellido_Materno

                       val numero = postList[recyclerview.getChildAdapterPosition(it)].telefonoCliente

                       val problema = postList[recyclerview.getChildAdapterPosition(it)].problema


                       if (ext == "0") {
                           ext = "S/N"
                       }

                       direccion = "$calle $colonia $ext $cp $referencia"
                       nombre = "$n $ap $am"

                     //  Toast.makeText(context, "Nombre: $n", Toast.LENGTH_SHORT).show()

                       val i = Intent(context, Presupuesto::class.java)
                       i.putExtra("latitud", latitud)
                       i.putExtra("longitud", longitud)
                       i.putExtra("Folio", folio)
                       i.putExtra("Nombre", nombre)
                       i.putExtra("Dirección", direccion)
                       i.putExtra("Problema", problema)
                       i.putExtra("Número", numero)
                       i.putExtra("Normal", true)
                       i.putExtra("Curp", curp)
                       i.putExtra("numT", telK)
                       startActivity(i)
                   }

                   recyclerview.adapter = MiAdapter
               }
            }

            override fun onFailure(call: Call<List<com.example.kerklytv2.modelo.serial.Presupuesto?>?>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("erro", t.toString())
            }

        })
    }


    fun setProgressDialog() {
        val llPadding = 30
        val ll = LinearLayout(requireContext())
        ll.orientation = LinearLayout.HORIZONTAL
        ll.setPadding(llPadding, llPadding, llPadding, llPadding)
        ll.gravity = Gravity.CENTER
        var llParam = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        ll.layoutParams = llParam
        progressBar = ProgressBar(requireContext())

        progressBar!!.isIndeterminate = true
        progressBar!!.setPadding(0, 0, llPadding, 0)
        progressBar!!.layoutParams = llParam
        llParam = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        llParam.gravity = Gravity.CENTER
        val tvText = TextView(requireContext())
        tvText.text = "Por favor espere un momento..."
        tvText.setTextColor(Color.parseColor("#000000"))
        tvText.textSize = 20f
        tvText.layoutParams = llParam
        ll.addView(progressBar)
        ll.addView(tvText)
        val builder = AlertDialog.Builder(requireContext())
        builder.setCancelable(false)
        builder.setView(ll)
        dialog = builder.create()
        dialog.show()
        val window: Window? = dialog.window
        if (window != null) {
            val layoutParams = WindowManager.LayoutParams()
            layoutParams.copyFrom(dialog.window!!.attributes)
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT
            dialog.window!!.attributes = layoutParams
        }
    }
}