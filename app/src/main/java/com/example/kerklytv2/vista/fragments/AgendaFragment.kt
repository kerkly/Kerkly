package com.example.kerklytv2.vista.fragments

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.TimePricker
import com.example.kerklytv2.interfaces.LoginInterface
import com.example.kerklytv2.interfaces.TerminarContratoInterface
import com.example.kerklytv2.url.Url
import com.example.kerklytv2.vista.InterfazKerkly
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import retrofit.Callback
import retrofit.RestAdapter
import retrofit.RetrofitError
import retrofit.client.Response
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AgendaFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AgendaFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var edit_cliente: TextInputEditText
    private lateinit var edit_problema: TextInputEditText
    private lateinit var edit_direccion: TextInputEditText
    private lateinit var edit_fechaFinal: TextInputEditText
    private lateinit var edit_horaFinal: TextInputEditText
    private lateinit var edit_fechaInicio: TextInputEditText
    private lateinit var edit_horInicio: TextInputEditText
    private lateinit var boton: MaterialButton
    private lateinit var fechaFinal: String
    private lateinit var fechaS: String
    private lateinit var horaS: String
    private var contrato = 0
    private lateinit var layoutHora: TextInputLayout
    private lateinit var layoutFecha: TextInputLayout

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
        val v =  inflater.inflate(R.layout.fragment_agenda, container, false)

        edit_cliente = v.findViewById(R.id.edit_clienteProblm)
        edit_direccion = v.findViewById(R.id.edi_direccionProblem)
        edit_problema = v.findViewById(R.id.edit_problProblem)
        edit_fechaFinal = v.findViewById(R.id.edit_fechafinalProblem)
        edit_horaFinal = v.findViewById(R.id.edit_horafinalProblem)
        edit_fechaInicio = v.findViewById(R.id.edit_fechaInicioProblem)
        edit_horInicio = v.findViewById(R.id.edit_horaInicioProblem)
        boton = v.findViewById(R.id.btn_agendaGuardar)
        layoutFecha = v.findViewById(R.id.layout_fechaFinal)
        layoutHora = v.findViewById(R.id.layout_horaFinal)

        val aux = arguments?.get("Fragment")

        edit_fechaFinal.setOnClickListener {
            val fecha = DatePrickFragment {year, month, day -> mostrar(year, month, day)}
            fecha.show(requireActivity().supportFragmentManager, "DatePicker")
        }

        edit_horaFinal.setOnClickListener {
            val hora = TimePricker{hora, min -> mostrarHora(hora,min)}
            hora.show(requireActivity().supportFragmentManager, "TimePicker")
        }

        boton.setOnClickListener {

            if (edit_fechaFinal.text!!.isNotEmpty() && edit_horaFinal.text!!.isNotEmpty()) {
                fechaFinal = "$fechaS $horaS"
                Log.d("boton", "deberia jalar")
                terminar(contrato, fechaFinal)
                layoutFecha.error = null
                layoutHora.error = null
            } else {
                layoutFecha.error = getString(R.string.campoRequerico)
                layoutHora.error = getString(R.string.campoRequerico)
            }
        }


        edit_cliente.setText(arguments?.getString("Nombre Cliente NoR").toString())
        edit_direccion.setText(arguments?.getString("Dirección").toString())
        edit_problema.setText(arguments?.getString("Problema").toString())
        contrato = arguments?.getInt("Contrato")!!

        var fecha = arguments?.getString("Fecha")
        val hora = fecha?.substring(11,16)
        fecha = fecha?.substring(0,10)

        edit_horInicio.setText(hora)
        edit_fechaInicio.setText(fecha)

        if (aux == "1") {
            boton.visibility = View.GONE
            var fechaFinal = arguments?.getString("Fecha final")
            val horaF = fechaFinal?.substring(11,16)
            fechaFinal = fechaFinal?.substring(0,10)

            edit_fechaFinal.setText(fechaFinal)
            edit_fechaFinal.isClickable = false
            edit_fechaFinal.isEnabled = false

            edit_horaFinal.setText(horaF)
            edit_horaFinal.isClickable = false
            edit_horaFinal.isEnabled = false
        }

        return v
    }


    @SuppressLint("SetTextI18n")
    private fun mostrarHora(hora: Int, min: Int) {
        horaS = "$hora:$min"
        edit_horaFinal.setText(horaS)
        horaS = "$horaS:00"
    }

    @SuppressLint("SetTextI18n")
    private fun mostrar(year: Int, month: Int, day: Int) {
        fechaS = "$year-$month-$day"
        edit_fechaFinal.setText(fechaS)
    }

    class DatePrickFragment(val listener: (year: Int, month: Int, day: Int) -> Unit): DialogFragment(),
        DatePickerDialog.OnDateSetListener {


        override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

            val c = Calendar.getInstance()
            var y = c.get(Calendar.YEAR)
            var m = c.get(Calendar.MONTH)
            var d = c.get(Calendar.DAY_OF_MONTH)

            return DatePickerDialog(requireActivity(), this, y, m, d)
        }

        override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
            listener(year, month+1, dayOfMonth)
        }

    }

    private fun terminar(id: Int, fecha: String) {
        val ROOT_URL = Url().URL
        val adapter = RestAdapter.Builder()
            .setEndpoint(ROOT_URL)
            .build()

        val api: TerminarContratoInterface = adapter.create(TerminarContratoInterface::class.java)
        api.terminarContrato(id,fecha,
            object : Callback<Response?> {
                override fun success(t: Response?, response: Response?) {
                    var entrada: BufferedReader? =  null
                    var Respuesta = ""
                    try {
                        entrada = BufferedReader(InputStreamReader(t?.body?.`in`()))
                        Respuesta = entrada.readLine()
                    }catch (e: Exception){
                        e.printStackTrace()
                    }
                    val resp1 = "1"
                    if (resp1.equals(Respuesta)){
                        Toast.makeText(requireContext(), "Servicio Finalizado", Toast.LENGTH_SHORT).show()
                        edit_horaFinal.isEnabled = false
                        edit_fechaFinal.isEnabled = false
                        boton.isEnabled = false
                    }else{
                        Toast.makeText(requireContext(), Respuesta, Toast.LENGTH_SHORT).show()
                    }
                }

                override fun failure(error: RetrofitError?) {
                    Toast.makeText(context, "error $error", Toast.LENGTH_SHORT).show()
                }

            }
        )
    }

}