package com.example.kerklytv2.controlador

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.paris.extensions.backgroundRes
import com.airbnb.paris.extensions.layoutGravity
import com.airbnb.paris.extensions.layoutMarginTopDp
import com.airbnb.paris.extensions.style
import com.example.kerklytv2.R
import com.example.kerklytv2.modelo.Mensaje
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class AdapterChat(c: Context): RecyclerView.Adapter<AdapterChat.ViewHolder>() {

    private var lista = ArrayList<Mensaje>()
    var context = c
    lateinit var databaseReference: DatabaseReference

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txt_mensaje = view.findViewById<TextView>(R.id.txt_mensaje_chat)
        val txt_fecha = view.findViewById<TextView>(R.id.txt_fechaMensaje_chat)
        var layoutMensaje = view.findViewById<LinearLayout>(R.id.layout_mensaje_card)
        var layoutHora = view.findViewById<LinearLayout>(R.id.layout_Hora_card)
        var txtMensajeLeido = view.findViewById<TextView>(R.id.txt_MensajeLeido)
       var layoutMensajeNoLeido = view.findViewById<LinearLayout>(R.id.layoutmensajeVisto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_view_mensaje, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.txt_mensaje.text = lista[position].mensaje
        holder.txt_fecha.text = lista[position].hora
        holder.txtMensajeLeido.text = lista[position].mensajeLeido
           // println("entro en el adapter Chat 91 ----> ${lista[position].mensaje}")



        val tipo_usuario = lista[position].tipo_usuario.trim()

        if (tipo_usuario == "Kerkly") {
            holder.txtMensajeLeido.visibility =  View.VISIBLE
            holder.layoutMensajeNoLeido.style({
                    this.layoutGravity(Gravity.END)
                })
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
                holder.layoutMensajeNoLeido.style({
                    this.layoutGravity(Gravity.START)
                })

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