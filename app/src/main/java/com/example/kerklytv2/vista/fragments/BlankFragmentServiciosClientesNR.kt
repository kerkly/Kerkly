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
import com.example.kerklytv2.MapsActivity
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.AdapterPresupuesto
import com.example.kerklytv2.controlador.AdapterPresupuestoNR
import com.example.kerklytv2.controlador.SetProgressDialog
import com.example.kerklytv2.interfaces.PresupuestoInterface
import com.example.kerklytv2.interfaces.PresupuestoNormalInterface
import com.example.kerklytv2.modelo.serial.Presupuesto
import com.example.kerklytv2.modelo.serial.PresupuestoDatosClienteNoRegistrado
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
 * Use the [BlankFragmentServiciosClientesNR.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragmentServiciosClientesNR : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var context: Activity
    lateinit var MiAdapter: AdapterPresupuestoNR
    lateinit var recyclerview: RecyclerView
    private lateinit var telefonokerkly: String
    lateinit var nombreKerkly: String
    private var folio = 0
    private lateinit var direccion: String
    private lateinit var nombre: String
    private lateinit var img: ImageView
    private lateinit var txt: TextView
    private  val setProgressDialog = SetProgressDialog()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val v = inflater.inflate(R.layout.fragment_blank_servicios_clientes_n_r, container, false)
        context = requireActivity()
        recyclerview = v.findViewById(R.id.recycler_presupuesto_ClienteNR)
        img = v.findViewById(R.id.img_presupuesto_ClienteNR)
        txt = v.findViewById(R.id.txt_presupuesto_ClienteNR)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager = LinearLayoutManager(context)
        // val intent = context.intent
        telefonokerkly = arguments?.getString("telefonokerkly").toString()
        nombreKerkly = arguments?.getString("nombreCompletoKerkly")!!
        //Toast.makeText(context, "Teléfono: $telefonokerkly", Toast.LENGTH_SHORT).show()
        setProgressDialog.setProgressDialog(requireContext())
        obtenerSolivitudes()
        return v
    }

    private fun obtenerSolivitudes() {
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
        val call = presupuestoGET.getPresupuestoCliente_NO_Registrado(telefonokerkly)
        call?.enqueue(object : Callback<List<PresupuestoDatosClienteNoRegistrado?>?> {

            override fun onResponse(
                call: Call<List<PresupuestoDatosClienteNoRegistrado?>?>,
                response: Response<List<PresupuestoDatosClienteNoRegistrado?>?>
            ) {

                val postList: ArrayList<PresupuestoDatosClienteNoRegistrado> = response.body() as ArrayList<PresupuestoDatosClienteNoRegistrado>

                if(postList.size == 0) {
                    recyclerview.visibility = View.GONE
                    setProgressDialog.dialog!!.dismiss()
                } else {
                    setProgressDialog.dialog!!.dismiss()
                    img.visibility = View.GONE
                    txt.visibility = View.GONE

                    MiAdapter = AdapterPresupuestoNR(postList)

                    MiAdapter.setOnClickListener {
                        val latitud = postList[recyclerview.getChildAdapterPosition(it)].latitud
                        val longitud = postList[recyclerview.getChildAdapterPosition(it)].longitud
                        folio = postList[recyclerview.getChildAdapterPosition(it)].idPresupuestoNoRegistrado
                        val colonia = postList[recyclerview.getChildAdapterPosition(it)].Colonia
                        val calle = postList[recyclerview.getChildAdapterPosition(it)].Calle
                        val cp = postList[recyclerview.getChildAdapterPosition(it)].Codigo_Postal
                        val referencia = postList[recyclerview.getChildAdapterPosition(it)].Referencia
                        var ext = postList[recyclerview.getChildAdapterPosition(it)].No_Exterior
                        val n = postList[recyclerview.getChildAdapterPosition(it)].nombre_noR
                        val ap = postList[recyclerview.getChildAdapterPosition(it)].apellidoP_noR
                        val am = postList[recyclerview.getChildAdapterPosition(it)].apellidoM_noR
                        val numerocliente = postList[recyclerview.getChildAdapterPosition(it)].telefono_NoR
                        val problema = postList[recyclerview.getChildAdapterPosition(it)].problema

                        if (ext == "0") {
                            ext = "S/N"
                        }

                        direccion = "$calle $colonia $ext $cp $referencia"
                        nombre = "$n $ap $am"

                        //  Toast.makeText(context, "Nombre: $n", Toast.LENGTH_SHORT).show()
                        val i = Intent(requireContext(), MapsActivity::class.java)
                        i.putExtra("telefonok", telefonokerkly)
                        i.putExtra("telefonoCliente", numerocliente)
                        i.putExtra("tipoServicio", "clienteNoRegistrado")
                        i.putExtra("latitud", latitud)
                        i.putExtra("longitud", longitud)
                        i.putExtra("nombreCompletoCliente", nombre)
                        i.putExtra("nombreCompletoKerkly", nombreKerkly)
                        i.putExtra("problema", problema)
                        i.putExtra("direccion", direccion)
                        //i.putExtra("correoCliente", correoCliente)
                        i.putExtra("Folio", folio)
                        startActivity(i)

                    }

                    recyclerview.adapter = MiAdapter
                }
            }

            override fun onFailure(call: Call<List<PresupuestoDatosClienteNoRegistrado?>?>, t: Throwable) {
                Toast.makeText(
                    context,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("erro", t.toString())
            }

        })
        setProgressDialog.dialog!!.dismiss()
    }


    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment BlankFragmentServiciosClientesNR.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragmentServiciosClientesNR().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}