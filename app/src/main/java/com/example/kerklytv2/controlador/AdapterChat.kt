package com.example.kerklytv2.controlador

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.paris.extensions.backgroundRes
import com.airbnb.paris.extensions.layoutGravity
import com.airbnb.paris.extensions.layoutMarginTopDp
import com.airbnb.paris.extensions.style
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.kerklytv2.R
import com.example.kerklytv2.modelo.Mensaje
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.picasso.Picasso
import jp.wasabeef.glide.transformations.RoundedCornersTransformation

class AdapterChat(c: Context): RecyclerView.Adapter<AdapterChat.ViewHolder>() {

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
        val txt_mensaje = view.findViewById<TextView>(R.id.txt_mensaje_chat)
        val txt_fecha = view.findViewById<TextView>(R.id.txt_fechaMensaje_chat)
        var layoutMensaje = view.findViewById<LinearLayout>(R.id.layout_mensaje_card)
        var layoutHora = view.findViewById<LinearLayout>(R.id.layout_Hora_card)
        var txtMensajeLeido = view.findViewById<TextView>(R.id.txt_MensajeLeido)
        var layoutMensajeNoLeido = view.findViewById<LinearLayout>(R.id.layoutmensajeVisto)
        var layoutArchivo =  view.findViewById<LinearLayout>(R.id.layoutArchivo)
        var imageViewArchivo = view.findViewById<ImageView>(R.id.imageViewArchivo)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.card_view_mensaje, parent, false)
        return ViewHolder(view)
    }
    @SuppressLint("SuspiciousIndentation")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val mensaje = lista[position]
        holder.txt_mensaje.text = mensaje.mensaje
        holder.txt_fecha.text = mensaje.hora
        holder.txtMensajeLeido.text = mensaje.mensajeLeido

        var tipo_usuario = mensaje.tipo_usuario.trim()
        if (tipo_usuario == "Kerkly") {
            holder.txtMensajeLeido.visibility = View.GONE

            if (mensaje.archivo == "") {
                holder.layoutArchivo.visibility = View.GONE
            } else {
                holder.layoutArchivo.visibility = View.VISIBLE
                holder.imageViewArchivo.setImageResource(when (mensaje.tipoArchivo) {
                    "imagen" -> R.drawable.descargaimagen
                    "pdf" -> R.drawable.icono_pdf
                    else -> R.drawable.descargaimagen
                })
            }

            holder.layoutArchivo.style {
                this.layoutGravity(Gravity.START)
            }
            holder.layoutMensaje.style {
                this.backgroundRes(R.drawable.burbuja_chat)
                this.layoutGravity(Gravity.START)
            }
            holder.layoutHora.style {
                this.layoutGravity(Gravity.START)
            }

            if (position > 0 && tipo_usuario == lista[position - 1].tipo_usuario.trim()) {
                holder.layoutMensaje.style {
                    this.layoutMarginTopDp(5)
                }
                holder.layoutHora.style {
                    this.layoutMarginTopDp(5)
                }
                holder.layoutMensajeNoLeido.style {
                    this.layoutMarginTopDp(5)
                }
            } else {
                holder.layoutMensaje.style {
                    this.layoutMarginTopDp(5)
                }
                holder.layoutHora.style {
                    this.layoutMarginTopDp(5)
                }
                holder.layoutMensajeNoLeido.style {
                    this.layoutMarginTopDp(5)
                }
            }
        }
    if (tipo_usuario == "cliente") {
        holder.txtMensajeLeido.visibility = View.VISIBLE

        if (mensaje.archivo == "") {
            holder.layoutArchivo.visibility = View.GONE
        } else {
            holder.layoutArchivo.visibility = View.VISIBLE
            holder.imageViewArchivo.setImageResource(when (mensaje.tipoArchivo) {
                "imagen" -> R.drawable.descargaimagen
                "pdf" -> R.drawable.icono_pdf
                else -> R.drawable.descargaimagen
            })
        }

        holder.layoutArchivo.style {
            this.layoutGravity(Gravity.END)
        }
        holder.layoutMensajeNoLeido.style {
            this.layoutGravity(Gravity.END)
        }
        holder.layoutMensaje.style {
            this.backgroundRes(R.drawable.burbuja_chat)
            this.layoutGravity(Gravity.END)
        }
        holder.layoutHora.style {
            this.layoutGravity(Gravity.END)
        }

        if (position > 0 && tipo_usuario == lista[position - 1].tipo_usuario.trim()) {
            holder.layoutMensaje.style {
                this.layoutMarginTopDp(5)
            }
            holder.layoutHora.style {
                this.layoutMarginTopDp(5)
            }
            holder.layoutMensajeNoLeido.style {
                this.layoutMarginTopDp(5)
            }
        } else {
            holder.layoutMensaje.style {
                this.layoutMarginTopDp(5)
            }
            holder.layoutHora.style {
                this.layoutMarginTopDp(5)
            }
            holder.layoutMensajeNoLeido.style {
                this.layoutMarginTopDp(5)
            }
        }

    }

    }

    override fun getItemCount(): Int {
        return lista.size
    }

    fun addMensaje(m: Mensaje) {
        lista.add(m)
        notifyItemInserted(lista.size)
    }

    fun addMensajeClear() {
        var tam = lista.size
        lista.remove(lista.get(tam-1))
        notifyItemInserted(lista.size)
    }

}

