package com.example.kerklytv2.ui.gallery

import android.content.Intent
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.MainActivityChats
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.adapterUsuarios
import com.example.kerklytv2.databinding.FragmentGalleryBinding
import com.example.kerklytv2.modelo.usuarios
import com.example.kerklytv2.url.Instancias
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class GalleryFragment : Fragment() {

    private lateinit var galleryViewModel: GalleryViewModel
    private var _binding: FragmentGalleryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var instancias: Instancias
    private lateinit var telefonokerkly: String
    private lateinit var nombreKerkly: String
    private lateinit var correoK: String
    private lateinit var uidKerkly: String
    private lateinit var tokenKerkly:String
    private lateinit var fotoKerkly:String

    private lateinit var arrayListDatos: ArrayList<usuarios>
    private lateinit var array: ArrayList<String>
    private lateinit var MiAdapter: adapterUsuarios
    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseUsu: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        galleryViewModel = ViewModelProvider(this).get(GalleryViewModel::class.java)
        _binding = FragmentGalleryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView =  root.findViewById(R.id.recycler_Usuarios)
        instancias = Instancias()
        telefonokerkly = arguments?.getString("telefonoKerkly").toString()
        nombreKerkly = arguments?.getString("nombreCompletoKerkly").toString()
        correoK = arguments?.getString("correoKerkly").toString()
        uidKerkly = arguments?.getString("uidKerkly").toString()
        tokenKerkly = arguments?.getString("tokenKerkly").toString()
        fotoKerkly =  arguments?.getString("urlFotoKerkly").toString()

        println("tel: $telefonokerkly")
        println("nom: $nombreKerkly")
        println("correo: $correoK")
        println("uidk: $uidKerkly")
        println("token: $tokenKerkly")
        println("foto: $fotoKerkly")

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
        databaseReferenceLista.addChildEventListener(object : ChildEventListener {
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
        return root
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
                                val telefonoCliente = MiAdapter.lista[position].telefono
                                val urlfoto = MiAdapter.lista[position].foto
                                val tokebCliente = MiAdapter.lista[position].token
                                val uidCliente = MiAdapter.lista[position].uid

                                Toast.makeText(requireContext(),"$uidCliente",Toast.LENGTH_SHORT).show()
                                val intent = Intent(requireContext(), MainActivityChats::class.java)
                                intent.putExtra("nombreCompletoCliente", nombre)
                                intent.putExtra("nombreCompletoKerkly", nombreKerkly)
                                intent.putExtra("telefonoCliente",telefonoCliente)
                                intent.putExtra("telefonoKerkly", telefonokerkly)
                                intent.putExtra("urlFotoCliente", urlfoto)
                                intent.putExtra("fotoKerkly", fotoKerkly)
                                intent.putExtra("tokenCliente", tokebCliente)
                                intent.putExtra("uidCliente", uidCliente)
                                intent.putExtra("uidKerkly", uidKerkly)
                                intent.putExtra("tokenKerkly",tokenKerkly)

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}