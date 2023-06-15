package com.example.kerklytv2.controlador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.R
import com.example.kerklytv2.modelo.serial.Presupuesto

class AdapterPresupuesto(val datset: ArrayList<Presupuesto>) :
    RecyclerView.Adapter<AdapterPresupuesto.ViewHolder>(), View.OnClickListener {

    private lateinit var listener: View.OnClickListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTipoSolicitud: TextView = view.findViewById(R.id.nombreOficio)
        val txtFolio: TextView = view.findViewById(R.id.tvfolio)

        init {
            // Define click listener for the ViewHolder's View.
        }

        val txtTelefono: TextView = view.findViewById(R.id.tvtelefono)
        val txtnombreCompletoCliente: TextView = view.findViewById(R.id.txtNombreCliente)

        init {
            // Define click listener for the ViewHolder's View.
        }

        val txtFecha: TextView = view.findViewById(R.id.tvfecha)

        init {
            // Define click listener for the ViewHolder's View.
        }

        val txtProblema: TextView = view.findViewById(R.id.tvproblema)

        init {
            // Define click listener for the ViewHolder's View.
        }

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.txtTipoSolicitud.text = datset[position].nombreO
        viewHolder.txtFolio.text = datset[position].idPresupuesto.toString()
        viewHolder.txtTelefono.text = datset[position].telefonoCliente
        viewHolder.txtProblema.text = datset[position].problema
        viewHolder.txtFecha.text = datset[position].fechaP
        val nombre = datset[position].Nombre
        val apeP = datset[position].Apellido_Paterno
        val apeM = datset[position].Apellido_Materno
        viewHolder.txtnombreCompletoCliente.text = "$nombre $apeP $apeM"
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.presupuesto_recycler, viewGroup, false)

        view.setOnClickListener(this)

        return ViewHolder(view)
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