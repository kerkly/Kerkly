package com.example.kerklytv2

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.request.RequestOptions
import com.example.kerklytv2.Notificacion.llamartopico
import com.example.kerklytv2.controlador.AdapterChat
import com.example.kerklytv2.modelo.Mensaje
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.squareup.picasso.Picasso
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import org.json.JSONException
import org.json.JSONObject
import java.text.DateFormat
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import java.util.*

class MainActivityChats : AppCompatActivity() {
    private lateinit var boton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var imageViewfotoCliente: ImageView
    private lateinit var adapter: AdapterChat
    private lateinit var firebaseDatabase: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var databaseReferenceCliente: DatabaseReference
    private var folio = 0
    private lateinit var b: Bundle
    private lateinit var nombrecliente: String
    private lateinit var fotoCliente:String

    private lateinit var nombreCompletoKerkly: String
    private lateinit var tokenCliente: String
    private val llamartopico = llamartopico()

    private lateinit var nombreKerkly: String
    private lateinit var nombre_txt: TextView
    private lateinit var telefonoCliente:String
    private lateinit var telefonoKerkly: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_chats)

       var firebaseMessaging = FirebaseMessaging.getInstance().subscribeToTopic("EnviarNoti")
        firebaseMessaging.addOnCompleteListener {

                //Toast.makeText(this@MainActivityChats, "Registrado:", Toast.LENGTH_SHORT).show()
        }
        boton = findViewById(R.id.boton_chat)
        editText = findViewById(R.id.editTextChat)
        recyclerView = findViewById(R.id.recycler_chat)
        nombre_txt = findViewById(R.id.txt_nombre_Cliente_chats)
        imageViewfotoCliente = findViewById(R.id.image_cliente)

        b = intent.extras!!

        folio = b.getInt("Folio")
        nombrecliente = b.getString("nombreCompletoCliente").toString()
        nombreCompletoKerkly = b.getString("nombreCompletoKerkly").toString()
       // nombreKerkly = b.getString("nombreKerkly").toString()
        telefonoKerkly = b.getString("telefonoKerkly").toString()
        telefonoCliente = b.getString("telefonoCliente").toString()
        fotoCliente = b.getString("urlFotoCliente").toString()
        tokenCliente = b.getString("tokenCliente")!!




        nombre_txt.text = nombrecliente
        val photoUrl = Uri.parse(fotoCliente)
       // agregarFotoDelCliente(photoUrl, imageViewfotoCliente)
        Picasso.get().load(photoUrl).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                System.out.println("Respuesta 1 ")
                val multi = MultiTransformation<Bitmap>(RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.ALL))
                println("foto Cliente : $fotoCliente")
                Glide.with(this@MainActivityChats).load(photoUrl)
                    .apply(RequestOptions.bitmapTransform(multi))
                    .into(imageViewfotoCliente)
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                TODO("Not yet implemented")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                val multi = MultiTransformation<Bitmap>(RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.ALL))
                println("foto Cliente : $fotoCliente")
                Glide.with(this@MainActivityChats).load(photoUrl)
                    .apply(RequestOptions.bitmapTransform(multi))
                    .into(imageViewfotoCliente)

            }

        })


        firebaseDatabase = FirebaseDatabase.getInstance()
        databaseReference = firebaseDatabase.getReference("UsuariosR").child(telefonoKerkly.toString()).child("chats")
            .child("$telefonoKerkly"+"_"+"$telefonoCliente")
        adapter = AdapterChat(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                setScrollBar()
            }
        })

        databaseReferenceCliente = firebaseDatabase.getReference("UsuariosR").child(telefonoCliente.toString()).child("chats")
            .child("$telefonoCliente"+"_"+"$telefonoKerkly")

        boton.setOnClickListener {
            if (editText.text == null){
                Toast.makeText(this, "Escribe tu mensaje" , Toast.LENGTH_SHORT).show()
            }else{
            //adapter.addMensaje(Mensaje(editText.text.toString(), "00:00"))
            databaseReference.push().setValue(Mensaje(editText.text.toString(), getTime()))
            databaseReferenceCliente.push().setValue(Mensaje(editText.text.toString(), getTime()))
            llamartopico.llamartopico(this,tokenCliente, editText.text.toString(), nombreCompletoKerkly)
            editText.setText("")
            }
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
    }

    private fun setScrollBar() {
        recyclerView.scrollToPosition(adapter.itemCount-1)
    }


    @SuppressLint("SimpleDateFormat")
    private fun getTime(): String {
       // val formatter = SimpleDateFormat("HH:mm")
       // val curDate = Date(System.currentTimeMillis())
        // Obtener la hora actual
        //val str: String = formatter.format(curDate)
        val currentDateTimeString = DateFormat.getDateTimeInstance().format(Date())
        return currentDateTimeString
    }

    fun agregarFotoDelCliente(photoUrl: Uri, imageView: ImageView){
        Picasso.get().load(photoUrl).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                System.out.println("Respuesta 1 ")
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                TODO("Not yet implemented")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                val multi = MultiTransformation<Bitmap>(RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.ALL))

                Glide.with(this@MainActivityChats).load(photoUrl)
                    .apply(RequestOptions.bitmapTransform(multi))
                    .into(imageView)

            }

        })
    }
}