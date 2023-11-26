package com.example.kerklytv2.ui.vista.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.kerklytv2.R
import com.google.android.material.button.MaterialButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HistorialFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HistorialFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var btn_urgencia: MaterialButton
    private lateinit var btn_servicio: MaterialButton
    private var b: Bundle? = null

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
        val v = inflater.inflate(R.layout.fragment_historial, container, false)

        btn_urgencia = v.findViewById(R.id.btn_urgenciaHistorial)
        btn_servicio = v.findViewById(R.id.btn_servicioNormalHistorial)

        btn_urgencia.setOnClickListener {
            setHistorialUrgente()
        }

        btn_servicio.setOnClickListener {
            setHistorial()
        }

        b = arguments

        return v
    }

    private fun setHistorialUrgente() {
        val f = HistorialUrgenteFragment()
        f.arguments = b
        var fm = requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
        }

    }

    private fun setHistorial() {
        val f = HistorialNormalFragment()
        f.arguments = b
        var fm = requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
        }

    }

}