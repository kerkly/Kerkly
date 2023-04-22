package com.example.kerklytv2.vista.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.kerklytv2.R
import com.example.kerklytv2.modelo.serial.OficioKerkly
//import com.example.kerklytv2.modelo.serial.OficioKerkly
import com.google.android.material.button.MaterialButton

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PresupuestosPreviewFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PresupuestosPreviewFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var boton_urg: MaterialButton
    private lateinit var boton_normal: MaterialButton
    private var b: Bundle? = null
    lateinit var postList: ArrayList<OficioKerkly>
    private lateinit var telefonoKerkly: String
    private lateinit var nombre_completo: String
    private lateinit var nombreKerkly: String
    lateinit var photoUrl: String
    lateinit var name: String
    lateinit var correoKerkly: String


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
        val v =  inflater.inflate(R.layout.fragment_presupuestos_preview, container, false)

        boton_normal = v.findViewById(R.id.boton_normalPresupuesto)
        boton_urg = v.findViewById(R.id.boton_urgenciaPresupuesto)

        boton_urg.setOnClickListener {
            setPresupuestoUrgente()
        }

        boton_normal.setOnClickListener {
            setPresupuestoNormal()
        }

        b = arguments
        postList = arguments?.getSerializable("arrayOfcios") as ArrayList<OficioKerkly>
        telefonoKerkly = arguments?.getString("telefonoKerkly").toString()
        photoUrl = arguments?.getString("urlFotoKerkly").toString()
       // nombreKerkly = arguments?.getString("nombreKerkly").toString()
        nombre_completo = arguments?.getString("nombreCompletoKerkly").toString()

        return v
    }

    private fun setPresupuestoUrgente() {
        val args = Bundle()
        val f = PresupuestoFragment()
        args.getString("telefonoKerkly", telefonoKerkly)
       // args.getString("nombreKerkly", nombreKerkly)
        args.putSerializable("arrayOfcios", postList)
        args.putString("nombreCompletoKerkly", nombre_completo)
        f.arguments = args
        f.arguments = b
        var fm = requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
        }
    }

    private fun setPresupuestoNormal() {
        val b = Bundle()
        b.putString("numNR", telefonoKerkly)
        val f = PresupuestoNomalFragment()
        f.arguments = b
        var fm = requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly,f).commit()
        }

    }

}