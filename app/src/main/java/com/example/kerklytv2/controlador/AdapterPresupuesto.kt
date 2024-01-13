package com.example.kerklytv2.controlador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.LoadMoreListener
import com.example.kerklytv2.R
import com.example.kerklytv2.modelo.serial.Presupuesto
import com.example.kerklytv2.modelo.serial.PresupuestourgentesDatosCliente
import java.util.Locale

class AdapterPresupuesto(val datset: ArrayList<Presupuesto>) :
    RecyclerView.Adapter<AdapterPresupuesto.ViewHolder>(), View.OnClickListener, Filterable {

    private lateinit var listener: View.OnClickListener
    lateinit var loadMoreListener: LoadMoreListener
    private var isLoading = false
    private var datasetFilteredOriginal: MutableList<Presupuesto> = ArrayList(datset)
    var datasetFiltered = datset

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

        // Verificar si estamos cerca del final y se debe cargar más
        if (loadMoreListener != null && position == datset.size - 1 && !isLoading) {
            isLoading = true
            loadMoreListener.onLoadMore()
            println("Scroll detectado - position: $position, datset.size: ${datset.size}")
        }
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

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraints: CharSequence?): FilterResults {
                val filteredList = ArrayList<Presupuesto>()

                if (!constraints.isNullOrBlank()) {
                    val filterPattern = constraints.toString().toLowerCase(Locale.getDefault()).trim()

                    for (item in datasetFilteredOriginal) {
                        val idContratoString = item.idPresupuesto.toString().toLowerCase(Locale.getDefault())

                        if (idContratoString.contains(filterPattern)) {
                            // Agrega elementos que contienen el número buscado
                            println("item agregado ${item.idPresupuesto}")
                            filteredList.add(item)
                        }
                    }
                } else {
                    filteredList.addAll(datset)
                }

                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraints: CharSequence?, results: FilterResults?) {
                if (results?.values != null) {
                    val filteredList = results.values as List<Presupuesto>

                    if (filteredList.isEmpty()) {
                        println("No se encontraron coincidencias")
                        isLoading = true
                        loadMoreListener.onLoadMore()
                    } else {
                        datasetFiltered.clear()
                        // Filtrar las coincidencias que contienen el número buscado y agregarlas al inicio
                        val matchingItems = filteredList.filter { item ->
                            val idContratoString =
                                item.idPresupuesto.toString().toLowerCase(Locale.getDefault())
                            idContratoString.contains(
                                constraints.toString().toLowerCase(Locale.getDefault()).trim()
                            )
                        }

                        // Agregar las coincidencias al inicio de la lista filtrada
                        datasetFiltered.addAll(matchingItems)
                        // Agregar el resto de los elementos al final de la lista filtrada
                        val nonMatchingItems = filteredList.filterNot { matchingItems.contains(it) }
                        datasetFiltered.addAll(nonMatchingItems)
                    }
                }else{
                    println("No hay resultados de filtrado")
                }
                notifyDataSetChanged()
            }
        }
    }

    fun showOriginalList() {
        //println("adapter borrado tamaño de ${datasetFilteredOriginal.size}")
        // Limpiar la lista filtrada y agregar todos los elementos originales
        datasetFiltered.clear()
        datasetFiltered.addAll(datasetFilteredOriginal)
        notifyDataSetChanged()
    }

    fun masDatos(masDatos: ArrayList<Presupuesto>){
        datasetFilteredOriginal.addAll(masDatos)
    }
}