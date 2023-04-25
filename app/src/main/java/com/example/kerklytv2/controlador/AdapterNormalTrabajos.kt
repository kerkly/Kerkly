package com.example.kerklytv2.controlador

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.R
import com.example.kerklytv2.modelo.serial.TrabajoNormal
import com.example.kerklytv2.vista.fragments.ContactosFragment


class AdapterNormalTrabajos(val datset: ArrayList<TrabajoNormal>, val activity: FragmentActivity):
    RecyclerView.Adapter<AdapterNormalTrabajos.ViewHolder>(), View.OnClickListener {

    private lateinit var listener: View.OnClickListener


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFolio: TextView = view.findViewById(R.id.txt_folio_folio)

        init {
            // Define click listener for the ViewHolder's View.
        }

        val nombre: TextView = view.findViewById(R.id.txt_folio_nombre)

        init {
            // Define click listener for the ViewHolder's View.
        }

        val correo: TextView = view.findViewById(R.id.txt_folio_correo)

        init {}

        var txt_folio_problema: TextView = view.findViewById(R.id.txt_folio_problema)

        var txt_fecha_urgente: TextView = view.findViewById(R.id.txt_fecha_urgente)

        var chat_img: ImageView = view.findViewById(R.id.chat_img)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_trabajos_urgentes, parent, false)

        view.setOnClickListener(this)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtFolio.text = datset[position].idPresupuesto.toString()
        holder.nombre.text = datset[position].Nombre
        holder.correo.text = datset[position].Correo
        holder.txt_folio_problema.text = datset[position].problema
        holder.txt_fecha_urgente.text = datset[position].fechaP


        holder.chat_img.setOnClickListener {
            val f = ContactosFragment()
            val b = Bundle()
            val nombre = "${datset[position].Nombre} ${datset[position].Apellido_Paterno} ${datset[position].Apellido_Materno}"
            val folio = datset[position].idPresupuesto

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