package com.example.kerklytv2.controlador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.R
import com.example.kerklytv2.modelo.serial.PresupuestourgentesDatosCliente

class ClaseAdapterR(val datset: ArrayList<PresupuestourgentesDatosCliente>) :
    RecyclerView.Adapter<ClaseAdapterR.ViewHolder>(), View.OnClickListener {

    private lateinit var listener: View.OnClickListener

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtTipoDeServicio: TextView = view.findViewById(R.id.nombreOficio)
        val txtFolio: TextView = view.findViewById(R.id.tvfolio)

        val txtTelefono: TextView = view.findViewById(R.id.tvtelefono)
        val txtnombreCompletoCliente: TextView = view.findViewById(R.id.txtNombreCliente)

        val txtFecha: TextView = view.findViewById(R.id.tvfecha)

        val txtProblema: TextView = view.findViewById(R.id.tvproblema)

        val idOficio: TextView = view.findViewById(R.id.txtNombreCliente)

    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ClaseAdapterR.ViewHolder, position: Int) {
        viewHolder.txtTipoDeServicio.text = datset[position].nombreOficios.toString()
        viewHolder.txtFolio.text = datset[position].idPresupuesto.toString()

            viewHolder.txtTelefono.text = datset[position].telefonoCliente.toString()

        viewHolder.txtnombreCompletoCliente.text =datset[position].Nombre +" " +datset[position].Apellido_Paterno+ " "+ datset[position].Apellido_Materno
        viewHolder.txtFecha.text = datset[position].fechaPresupuesto.toString()
        viewHolder.txtProblema.text = datset[position].problema.toString()


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