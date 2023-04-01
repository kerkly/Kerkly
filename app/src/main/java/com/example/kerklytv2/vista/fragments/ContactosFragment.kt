package com.example.kerklytv2.vista.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.MainActivityChats
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.adapterUsuarios
import com.example.kerklytv2.modelo.usuarios
import com.google.firebase.database.*
import java.util.*


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MensajesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ContactosFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var correoK: String
    private lateinit var nombreK: String
    private lateinit var urlFotoK: String

    private lateinit var array: ArrayList<String>
    private lateinit var arrayListDatos: ArrayList<usuarios>

    private lateinit var b: Bundle
    private lateinit var recyclerView: RecyclerView
    private lateinit var MiAdapter: adapterUsuarios
    private lateinit var firebase_databaseUsu: FirebaseDatabase
    private lateinit var databaseUsu: DatabaseReference

    private lateinit var telefonokerkly: String
    private lateinit var telefonoCliente: String
    private lateinit var fotoUrlCliente: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_contactos, container, false)
        recyclerView = v.findViewById(R.id.recycler_Usuarios)

        print("entro en contactos")
        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("message")

        myRef.setValue("Hello, cliente!")

        b= requireArguments()

        telefonokerkly = b.getString("telefonoKerkly").toString()
        nombreK = b.getString("nombreKerkly").toString()
        urlFotoK =b.getString("urlFotoKerkly").toString()
        correoK = b.getString("correoKerkly").toString()

        arrayListDatos = ArrayList()
        array = ArrayList<String>()

        MiAdapter = adapterUsuarios(requireContext())
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = MiAdapter

        MiAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver(){
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                setScrollBar()
            }
        })

        //Obtenemos la lista de contactos
        var firebaseDatabaseLista: FirebaseDatabase
        var databaseReferenceLista: DatabaseReference
        firebaseDatabaseLista = FirebaseDatabase.getInstance()
        databaseReferenceLista = firebaseDatabaseLista.getReference("UsuariosR")
            .child(telefonokerkly).child("Lista de Usuarios")

        databaseReferenceLista.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                mostrarUsuarios(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                TODO("Not yet implemented")
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
               MiAdapter.notifyDataSetChanged()
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

    private fun mostrarUsuarios(snapshot: DataSnapshot) {
        println(" tel: " + snapshot.child("telefono").value)
        array = arrayListOf(snapshot.child("telefono").value.toString())
        firebase_databaseUsu = FirebaseDatabase.getInstance()
        databaseUsu = firebase_databaseUsu.getReference("UsuariosR")
            .child(snapshot.child("telefono").value.toString())
            .child("MisDatos")

        databaseUsu.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val u2 = snapshot.getValue(usuarios::class.java)
                MiAdapter.agregarUsuario(u2!!)

                val mGestureDetector = GestureDetector(
                    requireContext(),
                    object : GestureDetector.SimpleOnGestureListener() {
                        override fun onSingleTapUp(e: MotionEvent): Boolean {
                            return true
                        }
                    })
                recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        try {
                            val child = recyclerView.findChildViewUnder(e.x, e.y)
                            if (child != null && mGestureDetector.onTouchEvent(e)) {
                                val position = recyclerView.getChildAdapterPosition(child)
                                val correo = MiAdapter.lista[position].correo
                                val nombre = MiAdapter.lista[position].nombre
                                 telefonoCliente = MiAdapter.lista[position].telefono
                                val urlfoto = MiAdapter.lista[position].foto
                                //Toast.makeText(requireContext(),"$telefono",Toast.LENGTH_SHORT).show()
                                val intent = Intent(requireContext(), MainActivityChats::class.java)
                                b!!.putString("nombreCompletoCliente", nombre)
                                b!!.putString("correoCliente", correo)
                                b!!.putString("telefonoCliente",telefonoCliente)
                                b!!.putString("telefonoKerkly", telefonokerkly)
                                b!!.putString("urlFotoCliente", urlfoto)
                                intent.putExtras(b!!)
                                startActivity(intent)
                                return true
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return false
                    }

                    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                        TODO("Not yet implemented")
                    }

                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                        TODO("Not yet implemented")
                    }

                })


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }



    private fun setScrollBar() {
        recyclerView.scrollToPosition(MiAdapter.itemCount-1)
    }



}