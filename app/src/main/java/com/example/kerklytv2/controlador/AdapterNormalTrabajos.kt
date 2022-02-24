package com.example.kerklytv2.controlador

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.R
import com.example.kerklytv2.modelo.TrabajoNormal
import com.example.kerklytv2.vista.fragments.MensajesFragment

class AdapterNormalTrabajos(val datset: ArrayList<TrabajoNormal>, val activity: FragmentActivity):
    RecyclerView.Adapter<AdapterNormalTrabajos.ViewHolder>(), View.OnClickListener {

    private lateinit var listener: View.OnClickListener


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFolio: TextView = view.findViewById(R.id.txt_folio_urgente)

        init {
            // Define click listener for the ViewHolder's View.
        }

        val txtTelefono: TextView = view.findViewById(R.id.txt_fecha_urgente)

        init {
            // Define click listener for the ViewHolder's View.
        }

        val img = view.findViewById<ImageView>(R.id.chat_img)

        init {}

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_trabajos_urgentes, parent, false)

        view.setOnClickListener(this)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtFolio.text = datset[position].idContrato.toString()
        holder.txtTelefono.text = datset[position].Fecha_Inicio


        holder.img.setOnClickListener {
            val f = MensajesFragment()
            val b = Bundle()
            val nombre = "${datset[position].Nombre} ${datset[position].Apellido_Paterno}"
            val folio = datset[position].idContrato

            b.putString("Nombre", nombre)
            b.putInt("Contrato", folio)
            f.arguments = b
            var fm = activity.supportFragmentManager.beginTransaction().apply {
                replace(R.id.nav_host_fragment_content_interfaz_kerkly, f).commit()
            }
        }
    }

    override fun getItemCount(): Int {
        return datset.size
    }

    fun setOnClickListener(l: View.OnClickListener) {
        this.listener = l

    }

    override fun onClick(v: View?) {
        listener.onClick(v)
    }
}