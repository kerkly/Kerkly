package com.example.kerklytv2.ui.slideshow

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.kerklytv2.R
import com.example.kerklytv2.databinding.FragmentSlideshowBinding
import com.example.kerklytv2.vista.MainActivityMostrarTrabajosPendientes
import com.google.android.material.button.MaterialButton

class SlideshowFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel
    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!

    private lateinit var btn_urgencia: MaterialButton
    private lateinit var btn_servicio: MaterialButton

    private lateinit var telefonoKerkly:String
    private lateinit var nombreKerkly:String
    private lateinit var curp:String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)

        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        btn_urgencia = root.findViewById(R.id.btn_urgencia);
        btn_servicio = root.findViewById(R.id.btn_servicioNormal);
        telefonoKerkly = arguments?.getString("telefonoKerkly").toString()
        nombreKerkly = arguments?.getString("Curp").toString()
        curp = arguments?.getString("nombrekerkly").toString()


        btn_urgencia.setOnClickListener {
            setTrabajos()
        }

        btn_servicio.setOnClickListener {
            setServicio()
        }
        return root
    }

    private fun setTrabajos() {
        val intent =  Intent(requireContext(),MainActivityMostrarTrabajosPendientes::class.java)
        intent.putExtra("telefonoKerkly", telefonoKerkly)
        intent.putExtra("Curp", curp)
        intent.putExtra("nombrekerkly",nombreKerkly)
        intent.putExtra("tipoServicio","urgente")
        startActivity(intent)
    }


    private fun setServicio() {
        val intent =  Intent(requireContext(),MainActivityMostrarTrabajosPendientes::class.java)
        intent.putExtra("telefonoKerkly", telefonoKerkly)
        intent.putExtra("Curp", curp)
        intent.putExtra("nombrekerkly",nombreKerkly)
        intent.putExtra("tipoServicio","normal")
        startActivity(intent)



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}