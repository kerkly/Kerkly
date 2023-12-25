package com.example.kerklytv2.controlador

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.R
import com.example.kerklytv2.modelo.Mensaje

class AdapterChat(c: Context) : RecyclerView.Adapter<AdapterChat.ViewHolder>() {

    var lista = ArrayList<Mensaje>()
    var context = c

    companion object {
        const val VIEW_TYPE_TEXTO = 1
        const val VIEW_TYPE_IMAGEN = 2
        const val VIEW_TYPE_PDF = 3
    }

    override fun getItemViewType(position: Int): Int {
        val mensaje = lista[position]
        return when {
            mensaje.tipoArchivo == "imagen" -> VIEW_TYPE_IMAGEN
            mensaje.tipoArchivo == "pdf" -> VIEW_TYPE_PDF
            else -> VIEW_TYPE_TEXTO
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_mensaje: TextView = view.findViewById(R.id.txt_mensaje_chat)
        val txt_fecha: TextView = view.findViewById(R.id.txt_fechaMensaje_chat)
        val layoutMensaje: LinearLayout = view.findViewById(R.id.layout_mensaje_card)
        val layoutHora: LinearLayout = view.findViewById(R.id.layout_Hora_card)
        val txtMensajeLeido: TextView = view.findViewById(R.id.txt_MensajeLeido)
        val layoutMensajeNoLeido: LinearLayout = view.findViewById(R.id.layoutmensajeVisto)
        val layoutArchivo: LinearLayout = view.findViewById(R.id.layoutArchivo)
        val imageViewArchivo: ImageView = view.findViewById(R.id.imageViewArchivo)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_mensaje, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mensaje = lista[position]

        holder.txt_mensaje.text = mensaje.mensaje
        holder.txt_fecha.text = mensaje.hora
        holder.txtMensajeLeido.text = mensaje.mensajeLeido

        val tipoUsuario = mensaje.tipo_usuario.trim()
        val esKerkly = tipoUsuario == "Kerkly"

        with(holder.layoutMensaje) {
            setStyleAndVisibility(if (esKerkly) Gravity.END else Gravity.START, 5)
            this.setBackgroundResource(if (esKerkly) R.drawable.burbuja_char_der else R.drawable.burbuja_chat)
        }

        with(holder.layoutHora) {
            setStyleAndVisibility(if (esKerkly) Gravity.END else Gravity.START, 0)
        }

        with(holder.layoutMensajeNoLeido) {
            setStyleAndVisibility(if (esKerkly) Gravity.END else Gravity.START, 0)
        }

        with(holder.txtMensajeLeido) {
            setStyleAndVisibility(if (esKerkly) Gravity.END else Gravity.START, 0)
        }

        with(holder.layoutArchivo) {
            setVisibility(mensaje.archivo.isNotEmpty())
            setStyleAndVisibility(if (esKerkly) Gravity.END else Gravity.START, 0)
        }

        holder.imageViewArchivo.setImageResource(
            when (mensaje.tipoArchivo) {
                "imagen" -> R.drawable.descargaimagen
                "pdf" -> R.drawable.icono_pdf
                else -> R.drawable.descargaimagen
            }
        )

    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun addMensaje(m: Mensaje) {
        lista.add(m)
        notifyItemInserted(lista.size)
    }

    fun addMensajeClear() {
        val tam = lista.size
        if (tam > 0) {
            lista.removeAt(tam - 1)
            notifyItemRemoved(tam - 1)
        }
    }

    private fun View.setStyleAndVisibility(gravity: Int, margin: Int) {
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        params.gravity = gravity
        params.topMargin = margin // Ajusta el margen superior aquí
        this.layoutParams = params
    }



    private fun View.setVisibility(isVisible: Boolean) {
        this.visibility = if (isVisible) View.VISIBLE else View.GONE
    }
}

