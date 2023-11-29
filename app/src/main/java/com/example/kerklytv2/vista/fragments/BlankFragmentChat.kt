package com.example.kerklytv2.vista.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.isInvisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.AdapterChat
import com.example.kerklytv2.modelo.Mensaje
import com.example.kerklytv2.modelo.listaDeUsuarios
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BlankFragmentChat.newInstance] factory method to
 * create an instance of this fragment.
 */
class BlankFragmentChat : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var boton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var adapter: AdapterChat
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var folio = 0
    private lateinit var b: Bundle
    private lateinit var nombrecliente: String
    private lateinit var nombreKerkly: String
    private lateinit var nombre_txt: TextView
    private lateinit var telefonoCliente:String
    private lateinit var telefonoKerkly: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_blank_chat, container, false)

        boton = v.findViewById(R.id.boton_chat)
        editText = v.findViewById(R.id.editTextChat)
        recyclerView = v.findViewById(R.id.recycler_chat)
        nombre_txt = v.findViewById(R.id.txt_nombre_kerkly_chats)

        b = requireArguments()

         folio = b.getInt("folio")
         nombrecliente = b.getString("nombreCliente").toString()
        nombreKerkly = b.getString("nombreKerkly").toString()
        telefonoKerkly = b.getString("numeroKerkly").toString()
        telefonoCliente = b.getString("numeroCliente").toString()

         nombre_txt.text = nombreKerkly


         firebaseDatabase = FirebaseDatabase.getInstance()
        //refencia para agregar lista de usuarios para el kerkly
        val firebaseDatabaseKerkly: FirebaseDatabase
        val databaseReferenceKerkly: DatabaseReference
        firebaseDatabaseKerkly = FirebaseDatabase.getInstance()
        databaseReferenceKerkly = firebaseDatabaseKerkly.getReference("UsuariosR")
            .child(telefonoKerkly).child("Lista de Usuarios")

        databaseReferenceKerkly.push().setValue(listaDeUsuarios(telefonoCliente))
        // referencia para agregar lista de usuarios para el kerkly
        val firebaseDatabaseCliente: FirebaseDatabase
        val databaseReferenceCliente: DatabaseReference
        firebaseDatabaseCliente = FirebaseDatabase.getInstance()
        databaseReferenceCliente = firebaseDatabaseCliente.getReference("UsuariosR")
            .child(telefonoCliente).child("Lista de Usuarios")
        databaseReferenceCliente.push().setValue(listaDeUsuarios(telefonoKerkly))
         databaseReference = firebaseDatabase.getReference("UsuariosR")
             .child(telefonoKerkly).child("chats").child("$telefonoKerkly"+"_"+"$telefonoCliente")

         adapter = AdapterChat(requireContext())

         recyclerView.layoutManager = LinearLayoutManager(context)

         recyclerView.adapter = adapter

         adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
             override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                 super.onItemRangeInserted(positionStart, itemCount)
                 setScrollBar()
             }
         })

         boton.setOnClickListener {
             //adapter.addMensaje(Mensaje(editText.text.toString(), "00:00"))
             databaseReference.push().setValue(Mensaje(editText.text.toString(), getTime(),"","",""))
             editText.setText("")
         }

         databaseReference.addChildEventListener(object : ChildEventListener {
             override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                 val m = snapshot.getValue(Mensaje::class.java)
                 adapter.addMensaje(m!!)
             }

             override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                 TODO("Not yet implemented")
             }

             override fun onChildRemoved(snapshot: DataSnapshot) {
                 TODO("Not yet implemented")
             }

             override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                 TODO("Not yet implemented")
             }

             override fun onCancelled(error: DatabaseError) {
                 TODO("Not yet implemented")
             }

         })


        return v
    }

    private fun setScrollBar() {
        recyclerView.scrollToPosition(adapter.itemCount-1)
    }


    @SuppressLint("SimpleDateFormat")
    private fun getTime(): String {
        val formatter = SimpleDateFormat("HH:mm")
        val curDate = Date(System.currentTimeMillis())
        // Obtener la hora actual
        val str: String = formatter.format(curDate)

        return str
    }

}