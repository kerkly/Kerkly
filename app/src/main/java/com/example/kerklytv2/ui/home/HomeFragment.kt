package com.example.kerklytv2.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.kerklytv2.MainActivityMostrarSolicitud
import com.example.kerklytv2.R
import com.example.kerklytv2.databinding.FragmentHomeBinding
import com.example.kerklytv2.vista.fragments.BlankFragmentServiciosClientesNR
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var boton_urg: MaterialButton
    private lateinit var boton_normal: MaterialButton
    private lateinit var btonClienteNR: MaterialButton

    private lateinit var telefonoKerkly: String
    private lateinit var nombre_completo: String
    lateinit var photoUrl: String
    lateinit var name: String
    lateinit var correoKerkly: String
    lateinit var Curp: String
    private lateinit var direccionKerly: String

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root

        boton_normal = root.findViewById(R.id.boton_normalPresupuesto)
        boton_urg = root.findViewById(R.id.boton_urgenciaPresupuesto)
        btonClienteNR = root.findViewById(R.id.boton_serviciosClientesNR)

        telefonoKerkly = arguments?.getString("telefonoKerkly").toString()
        photoUrl = arguments?.getString("urlFotoKerkly").toString()
        nombre_completo = arguments?.getString("nombreCompletoKerkly").toString()
        Curp = arguments?.getString("Curp").toString()
        correoKerkly = arguments?.getString("correoKerkly").toString()
        direccionKerly = arguments?.getString("direccionkerkly").toString()

        println("tel $telefonoKerkly ")
        println("foto $photoUrl ")
        println("nombr $nombre_completo ")
        println("curp $Curp ")
        println("correo $correoKerkly ")
        println("direcc $direccionKerly ")

        btonClienteNR.setOnClickListener {
            setServiciosClienteNR()
        }

        boton_urg.setOnClickListener {
            setPresupuestoUrgente()
        }

        boton_normal.setOnClickListener {
            setPresupuestoNormal()
        }

        return root
    }

    private fun setServiciosClienteNR() {
        /*val b = Bundle()
        b.putString("telefonokerkly", telefonoKerkly)
        b.putString("nombreCompletoKerkly", nombre_completo)
        b.putString("Curp", Curp)
        b.putString("correoKerly", correoKerkly)
        val fragment = BlankFragmentServiciosClientesNR()
        b.putString("direccionKerly", direccionKerly)
        fragment.arguments = b
        var frag =  requireActivity().supportFragmentManager.beginTransaction().apply {
            replace(R.id.nav_host_fragment_content_interfaz_kerkly, fragment).commit()*/
        showMensaje("Pendiente .... (:")
        }

    private fun setPresupuestoNormal() {
        val intent = Intent(requireContext(),MainActivityMostrarSolicitud::class.java)
        intent.putExtra("telefonoKerkly", telefonoKerkly)
        intent.putExtra("nombreCompletoKerkly", nombre_completo)
        intent.putExtra("Curp", Curp)
        intent.putExtra("correoKerkly", correoKerkly)
        intent.putExtra("TipoDeSolicitud","normal")
        startActivity(intent)
    }

    private fun setPresupuestoUrgente() {
        val intent = Intent(requireContext(), MainActivityMostrarSolicitud::class.java)
        intent.putExtra("telefonoKerkly", telefonoKerkly)
        intent.putExtra("nombreCompletoKerkly", nombre_completo)
        intent.putExtra("Curp", Curp)
        intent.putExtra("correoKerkly", correoKerkly)
        intent.putExtra("TipoDeSolicitud","urgente")
        startActivity(intent)
    }

    private fun showMensaje(mensaje:String){
        Toast.makeText(requireContext(),mensaje,Toast.LENGTH_SHORT).show()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}