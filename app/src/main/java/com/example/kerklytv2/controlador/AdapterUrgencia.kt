package com.example.kerklytv2.controlador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.R
import com.example.kerklytv2.modelo.serial.TrabajoUrgencia

class AdapterUrgencia(val datset: ArrayList<TrabajoUrgencia>):
                    RecyclerView.Adapter<AdapterUrgencia.ViewHolder>(), View.OnClickListener {

    private lateinit var listener: View.OnClickListener


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFolio: TextView = view.findViewById(R.id.txt_folio_folio)

        init {
            // Define click listener for the ViewHolder's View.
        }

        val txtTelefono: TextView = view.findViewById(R.id.txt_folio_problema)

        init {
            // Define click listener for the ViewHolder's View.
        }


        val txtnombre: TextView = view.findViewById(R.id.txt_folio_nombre)

        val txt_folio_correo: TextView = view.findViewById(R.id.txt_folio_correo)

        val txt_folio_problema: TextView = view.findViewById(R.id.txt_folio_problema)
        val txtOficio: TextView= view.findViewById(R.id.txt_folio_oficio)

        val txt_fecha_urgente: TextView = view.findViewById(R.id.txt_fecha_urgente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_trabajos_urgentes, parent, false)

        view.setOnClickListener(this)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtFolio.text = datset[position].idPresupuesto.toString()

        val nombreCompleto = datset[position].Nombre +" "+ datset[position].Apellido_Paterno +" "+ datset[position].Apellido_Materno
        holder.txtnombre.text = nombreCompleto
        holder.txt_folio_correo.text = datset[position].Correo
        holder.txt_folio_problema.text = datset[position].problema
        holder.txtOficio.text = datset[position].nombreOficio
        holder.txt_fecha_urgente.text = datset[position].fechaP
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