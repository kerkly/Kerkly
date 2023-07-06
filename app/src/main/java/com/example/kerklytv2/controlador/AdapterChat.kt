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
    lateinit var databaseReference: DatabaseReference

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
        holder.txt_mensaje.text = lista[position].mensaje
        holder.txt_fecha.text = lista[position].hora
        holder.txtMensajeLeido.text = lista[position].mensajeLeido

        // println("entro en el adapter Chat 91 ----> ${lista[position].mensaje}")
        val tipo_usuario = lista[position].tipo_usuario.trim()
        if (tipo_usuario == "Kerkly") {
            holder.txtMensajeLeido.visibility =  View.VISIBLE
            if (lista[position].archivo ==""){
               println("no hay archivo")
            }else{
                if (lista[position].tipoArchivo == "imagen"){
                    println("hay archivo " + lista[position].archivo.toString())
                    holder.layoutArchivo.visibility = View.VISIBLE
                            holder.layoutArchivo.style{
                                this.layoutGravity(Gravity.END)
                            }

                    val photoUrl = Uri.parse(lista[position].archivo)
                    // agregarFotoDelCliente(photoUrl, imageViewfotoCliente)
                    Picasso.get()
                        .load(photoUrl)
                        .resize(800, 800)
                        .into(holder.imageViewArchivo)
                }

                if (lista[position].tipoArchivo == "pdf") {
                    holder.layoutArchivo.visibility = View.VISIBLE
                    holder.layoutArchivo.style {
                        this.layoutGravity(Gravity.END)
                        holder.imageViewArchivo.setImageResource(R.drawable.icono_pdf)
                    }

                }

            }
            holder.layoutMensajeNoLeido.style{
                    this.layoutGravity(Gravity.END)
                }
                holder.layoutMensaje.style {
                    this.backgroundRes(R.drawable.burbuja_char_der)
                    this.layoutGravity(Gravity.END)
                }
                holder.layoutHora.style{
                    this.layoutGravity(Gravity.END)
                }
                if (position > 0) {
                    if (tipo_usuario == lista[position].tipo_usuario.trim()) {
                        holder.layoutMensaje.style {
                            this.layoutMarginTopDp(5)
                        }
                        holder.layoutHora.style{
                            this.layoutMarginTopDp(5)
                        }
                        holder.layoutMensajeNoLeido.style{
                            this.layoutMarginTopDp(5)
                        }
                    } else {
                        holder.layoutMensaje.style {
                            this.layoutMarginTopDp(5)
                        }
                        holder.layoutHora.style{
                            this.layoutMarginTopDp(5)
                        }
                        holder.layoutMensajeNoLeido.style{
                            this.layoutMarginTopDp(5)
                        }
                    }

                }
        }
    if (tipo_usuario == "cliente") {
        holder.txtMensajeLeido.visibility =  View.GONE
                holder.layoutMensajeNoLeido.style{
                    this.layoutGravity(Gravity.START)
                }

        if (lista[position].archivo ==""){
            println("no hay archivo")
        }else{
            if (lista[position].tipoArchivo == "imagen"){
                println("hay archivo " + lista[position].archivo.toString())
                holder.layoutArchivo.visibility = View.VISIBLE
                holder.layoutArchivo.style{
                    this.layoutGravity(Gravity.START)
                }

                val photoUrl = Uri.parse(lista[position].archivo)
                // agregarFotoDelCliente(photoUrl, imageViewfotoCliente)
                Picasso.get()
                    .load(photoUrl)
                    .resize(800, 800)
                    .into(holder.imageViewArchivo)
            }

            if (lista[position].tipoArchivo == "pdf") {
                holder.layoutArchivo.visibility = View.VISIBLE
                holder.layoutArchivo.style {
                    this.layoutGravity(Gravity.START)
                    holder.imageViewArchivo.setImageResource(R.drawable.icono_pdf)
                }

            }
           }

                holder.layoutMensaje.style {
                    this.backgroundRes(R.drawable.burbuja_chat)
                    this.layoutGravity(Gravity.START)
                }
                holder.layoutHora.style{
                    this.layoutGravity(Gravity.START)
                }

                if (position > 0) {
                    if (tipo_usuario == lista[position-1].tipo_usuario.trim()) {
                        holder.layoutMensaje.style {
                            this.layoutMarginTopDp(5)
                        }
                        holder.layoutHora.style{
                            this.layoutMarginTopDp(5)
                        }
                        holder.layoutMensajeNoLeido.style{
                            this.layoutMarginTopDp(5)
                        }
                    } else {
                        holder.layoutHora.style{
                            this.layoutMarginTopDp(5)
                        }
                        holder.layoutMensaje.style {
                            this.layoutMarginTopDp(5)
                        }
                        holder.layoutMensajeNoLeido.style{
                            this.layoutMarginTopDp(5)
                        }

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

