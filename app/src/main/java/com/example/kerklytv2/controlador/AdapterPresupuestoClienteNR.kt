package com.example.kerklytv2.controlador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.R
import com.example.kerklytv2.modelo.serial.Presupuesto
import com.example.kerklytv2.modelo.serial.PresupuestoDatosClienteNoRegistrado

class AdapterPresupuestoClienteNR(val datset: ArrayList<PresupuestoDatosClienteNoRegistrado>) :
    RecyclerView.Adapter<AdapterPresupuestoClienteNR.ViewHolder>(), View.OnClickListener {

    private lateinit var listener: View.OnClickListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTipoDeSevicio: TextView = view.findViewById(R.id.nombreOficioNR)
        val txtFolio: TextView = view.findViewById(R.id.txtFolioNR)
        val txtTelefono: TextView = view.findViewById(R.id.txttelefono)
        val txtFecha: TextView = view.findViewById(R.id.tvfecha)
        val txtProblema: TextView = view.findViewById(R.id.tvproblema)
        val txtNombre: TextView = view.findViewById(R.id.txtNombreClienteNR)

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val nombreC =  datset[position].nombre_noR +" "+datset[position].apellidoP_noR +" "+  datset[position].apellidoM_noR
        viewHolder.txtTipoDeSevicio.text = datset[position].nombreO
        viewHolder.txtFolio.text = datset[position].idPresupuestoNoRegistrado.toString()
        viewHolder.txtTelefono.text = datset[position].telefono_NoR
        viewHolder.txtProblema.text = datset[position].problema
        viewHolder.txtFecha.text = datset[position].fechaPresupuesto
        viewHolder.txtNombre.text = nombreC
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.presupuesto_recycler_nr, viewGroup, false)

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