package com.example.kerklytv2.controlador

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.lifecycle.findViewTreeViewModelStoreOwner
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.LoadMoreListener
import com.example.kerklytv2.R
import com.example.kerklytv2.modelo.serial.PresupuestourgentesDatosCliente
import java.text.SimpleDateFormat
import java.util.Locale

class ClaseAdapterR(val datset: ArrayList<PresupuestourgentesDatosCliente>) :
    RecyclerView.Adapter<ClaseAdapterR.ViewHolder>(), View.OnClickListener, Filterable {

    private lateinit var listener: View.OnClickListener
    lateinit var loadMoreListener: LoadMoreListener
    private var isLoading = false
    private var datasetFilteredOriginal: MutableList<PresupuestourgentesDatosCliente> = ArrayList(datset)
    var datasetFiltered = datset

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
        val fechaOriginal =  formatearFecha(datset[position].fechaPresupuesto.toString())
        viewHolder.txtFecha.text = fechaOriginal
        viewHolder.txtProblema.text = datset[position].problema.toString()


        // Verificar si estamos cerca del final y se debe cargar más
        if (loadMoreListener != null && position == datset.size - 1 && !isLoading) {
            isLoading = true
            loadMoreListener.onLoadMore()
            println("Scroll detectado - position: $position, datset.size: ${datset.size}")
        }
    }

    fun formatearFecha(fechaOriginal: String): String {
        try {
            // Formato de la fecha y hora devuelto por el servidor
            val formatoOriginal = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

            // Parsear la fecha y hora original
            val fechaParseada = formatoOriginal.parse(fechaOriginal)

            // Nuevo formato deseado
            val nuevoFormato = SimpleDateFormat("h:mm a 'del' EEEE d 'de' MMMM yyyy", Locale.getDefault())

            // Formatear la fecha y hora parseada en el nuevo formato
            return nuevoFormato.format(fechaParseada!!)
        } catch (e: Exception) {
            e.printStackTrace()
            return fechaOriginal // En caso de error, devolver la fecha original sin formato
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
        return datasetFiltered.size
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
                val filteredList = ArrayList<PresupuestourgentesDatosCliente>()

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
                    val filteredList = results.values as List<PresupuestourgentesDatosCliente>

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
        println("adapter borrado tamaño de ${datasetFilteredOriginal.size}")
        // Limpiar la lista filtrada y agregar todos los elementos originales
        datasetFiltered.clear()
        datasetFiltered.addAll(datasetFilteredOriginal)
        notifyDataSetChanged()
    }

    fun masDatos(masDatos: ArrayList<PresupuestourgentesDatosCliente>){
        datasetFilteredOriginal.addAll(masDatos)
    }
}