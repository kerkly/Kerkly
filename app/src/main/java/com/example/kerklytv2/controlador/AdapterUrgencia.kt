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
        val txtFolio: TextView = view.findViewById(R.id.txt_folio_urgente)

        init {
            // Define click listener for the ViewHolder's View.
        }

        val txtTelefono: TextView = view.findViewById(R.id.txt_fecha_urgente)

        init {
            // Define click listener for the ViewHolder's View.
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_trabajos_urgentes, parent, false)

        view.setOnClickListener(this)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txtFolio.text = datset[position].idContraNoRegistrado.toString()
        holder.txtTelefono.text = datset[position].Fecha_Inicio_NoRegistrado
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