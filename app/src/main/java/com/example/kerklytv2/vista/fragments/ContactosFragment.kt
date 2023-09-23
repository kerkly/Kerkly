package com.example.kerklytv2.vista.fragments

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.MainActivityChats
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.SetProgressDialog

import com.example.kerklytv2.controlador.adapterUsuarios
import com.example.kerklytv2.modelo.usuarios
import com.example.kerklytv2.url.Instancias
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
    private val setProgressDialog = SetProgressDialog()
    private lateinit var instancias: Instancias
    private lateinit var uidKerkly: String

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

        b= requireArguments()
        instancias = Instancias()
        telefonokerkly = b.getString("telefonoKerkly").toString()
        nombreK = b.getString("nombreKerkly").toString()
        correoK = b.getString("correoKerkly").toString()
        uidKerkly = b.getString("uidKerkly").toString()

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


        val databaseReferenceLista  = instancias.referenciaListaDeUsuariosKerkly(uidKerkly)
        databaseReferenceLista.addChildEventListener(object : ChildEventListener{
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                //println("mis contactos"+snapshot.child("telefono").value)
               mostrarUsuarios(snapshot)
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                MiAdapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
               MiAdapter.notifyDataSetChanged()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                MiAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                MiAdapter.notifyDataSetChanged()
            }

        })

        return v
    }

    private fun mostrarUsuarios(snapshot: DataSnapshot) {
        val uid =snapshot.child("uid").value
        array = arrayListOf(snapshot.value.toString())
      //  firebase_databaseUsu = FirebaseDatabase.getInstance()
       // databaseUsu = firebase_databaseUsu.getReference("UsuariosR").child(uid.toString()).child("MisDatos")
        databaseUsu =instancias.referenciaInformacionDelCliente(uid.toString())
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
                                val tokebCliente = MiAdapter.lista[position].token
                                val uidCliente = MiAdapter.lista[position].uid

                                //Toast.makeText(requireContext(),"$telefono",Toast.LENGTH_SHORT).show()
                                val intent = Intent(requireContext(), MainActivityChats::class.java)
                                b!!.putString("nombreCompletoCliente", nombre)
                                b!!.putString("correoCliente", correo)
                                b!!.putString("telefonoCliente",telefonoCliente)
                                b!!.putString("telefonoKerkly", telefonokerkly)
                                b!!.putString("urlFotoCliente", urlfoto)
                                b!!.putString("tokenCliente", tokebCliente)
                                b!!.putString("uidCliente", uidCliente)
                                b!!.putString("uidKerkly", uidKerkly)

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

                    }
                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {
                        TODO("Not yet implemented")
                    }
                })
            }
            override fun onCancelled(error: DatabaseError) {
                println(error)
            }
        })

    }
    private fun setScrollBar() {
        recyclerView.scrollToPosition(MiAdapter.itemCount-1)
    }
}