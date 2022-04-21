package com.example.kerklytv2.controlador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.R
import com.example.kerklytv2.modelo.serial.PresupuestoDatos

class ClaseAdapterR(val datset: ArrayList<PresupuestoDatos>) :
    RecyclerView.Adapter<ClaseAdapterR.ViewHolder>(), View.OnClickListener {

    private lateinit var listener: View.OnClickListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtFolio: TextView = view.findViewById(R.id.tvfolio)

        init {
            // Define click listener for the ViewHolder's View.
        }

        val txtTelefono: TextView = view.findViewById(R.id.tvtelefono)

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
    override fun onBindViewHolder(viewHolder: ClaseAdapterR.ViewHolder, position: Int) {
        viewHolder.txtFolio.text = datset[position].idPresupuestoNoRegistrado.toString()
        viewHolder.txtTelefono.text = datset[position].idNoRTelefono.toString()
        viewHolder.txtProblema.text = datset[position].problema.toString()
        viewHolder.txtFecha.text = datset[position].fechaPresupuesto.toString()
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