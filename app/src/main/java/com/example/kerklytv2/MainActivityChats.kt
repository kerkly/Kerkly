package com.example.kerklytv2

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Looper
import android.os.ParcelFileDescriptor
import android.provider.OpenableColumns
import android.provider.Settings
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.request.RequestOptions
import com.example.kerklytv2.MisServicios.LocationService
import com.example.kerklytv2.Notificacion.llamartopico
import com.example.kerklytv2.controlador.AdapterChat
import com.example.kerklytv2.modelo.Mensaje
import com.example.kerklytv2.modelo.MensajeCopia
import com.example.kerklytv2.ui.home.HomeFragment
import com.example.kerklytv2.url.Instancias
import com.example.kerklytv2.vista.InterfazKerkly
import com.github.barteksc.pdfviewer.PDFView
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.squareup.picasso.Picasso
import jp.wasabeef.glide.transformations.RoundedCornersTransformation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.DateFormat
import java.util.*


class MainActivityChats : AppCompatActivity() {
    private lateinit var boton: ImageButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var editText: EditText
    private lateinit var imageViewfotoCliente: ImageView
    private lateinit var adapter: AdapterChat
    private var folio = 0
    private lateinit var b: Bundle
    private lateinit var nombrecliente: String
    private lateinit var fotoCliente:String

    private lateinit var nombreCompletoKerkly: String
    private lateinit var tokenCliente: String
    private val llamartopico = llamartopico()

    private lateinit var nombre_txt: TextView
    private lateinit var telefonoCliente:String
    private lateinit var telefonoKerkly: String
     var childEventListener: ChildEventListener? = null
    var childEventListener2: ChildEventListener? = null
    private lateinit var botonArchivos: ImageButton
    private val REQUEST_CODE_FILE = 1
    private lateinit var progressBar: ProgressBar
    private lateinit var uriArchivo: Uri
   private lateinit var imagenCompleta: ImageView
    private lateinit var PantallaCompletaPdf: PDFView
    private lateinit var pdfRenderer: PdfRenderer
    lateinit var frameLayout: FrameLayout
    private var  REQUEST_CODE = 0
    lateinit var directoMaps:String
    private lateinit var buttonGPs: Button
    private lateinit var instancias: Instancias
    private lateinit var uidCliente:String
    private lateinit var uidKerkly:String
    private val locationServiceIntent: Intent by lazy {
        Intent(this, LocationService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
      /*  requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)*/
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
        imagenCompleta = findViewById(R.id.fullscreenImageView)
        PantallaCompletaPdf = findViewById(R.id.pdfView)
        frameLayout = findViewById(R.id.fragemntlayoutChats)
        buttonGPs =  findViewById(R.id.botonDetenerService)
       // viewPager = findViewById(R.id.viewPager)

        b = intent.extras!!
        folio = b.getInt("Folio")
        nombrecliente = b.getString("nombreCompletoCliente").toString()
        nombreCompletoKerkly = b.getString("nombreCompletoKerkly").toString()
       // nombreKerkly = b.getString("nombreKerkly").toString()
        telefonoKerkly = b.getString("telefonoKerkly").toString()
        telefonoCliente = b.getString("telefonoCliente").toString()
        fotoCliente = b.getString("urlFotoCliente").toString()
        tokenCliente = b.getString("tokenCliente")!!
        directoMaps = b.getString("directoMaps").toString()
        uidCliente = b.getString("uidCliente").toString()
        uidKerkly = b.getString("uidKerkly").toString()

       // showMensaje("cliente $uidCliente kerkly $uidKerkly")
       // println("cliente $uidCliente kerkly $uidKerkly")

        nombre_txt.text = nombrecliente
        val photoUrl = Uri.parse(fotoCliente)
       // agregarFotoDelCliente(photoUrl, imageViewfotoCliente)
        Picasso.get().load(photoUrl).into(object : com.squareup.picasso.Target {
            override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                val multi = MultiTransformation<Bitmap>(RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.ALL))
                //println("foto Cliente : $fotoCliente")
                Glide.with(this@MainActivityChats).load(photoUrl)
                    .apply(RequestOptions.bitmapTransform(multi))
                    .into(imageViewfotoCliente)
            }

            override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                TODO("Not yet implemented")
            }

            override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                val multi = MultiTransformation<Bitmap>(RoundedCornersTransformation(128, 0, RoundedCornersTransformation.CornerType.ALL))
                Glide.with(this@MainActivityChats).load(photoUrl)
                    .apply(RequestOptions.bitmapTransform(multi))
                    .into(imageViewfotoCliente)
            }
        })
        instancias = Instancias()
       // databaseReference = firebaseDatabase.getReference("UsuariosR").child(telefonoKerkly.toString()).child("chats").child("$telefonoKerkly"+"_"+"$telefonoCliente")
       val databaseReference = instancias.chatsKerkly(uidKerkly, uidCliente)
        adapter = AdapterChat(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                super.onItemRangeInserted(positionStart, itemCount)
                setScrollBar()
            }
        })

        //databaseReferenceCliente = firebaseDatabase.getReference("UsuariosR").child(telefonoCliente).child("chats").child("$telefonoCliente"+"_"+"$telefonoKerkly")
val databaseReferenceCliente = instancias.chatsCliente(uidKerkly, uidCliente)
        boton.setOnClickListener {
            if (editText.text.toString() == ""){
                Toast.makeText(this, "Escribe tu mensaje" , Toast.LENGTH_SHORT).show()
            }else{
           //adapter.addMensaje(Mensaje(editText.text.toString(), "00:00"))
            databaseReference.push().setValue(Mensaje(editText.text.toString(), getTime(),"","",""))
            databaseReferenceCliente.push().setValue(Mensaje(editText.text.toString(), getTime(),"","",""))
            llamartopico.llamartopico(this,tokenCliente, editText.text.toString(), nombreCompletoKerkly)
            editText.setText("")
            }
        }

        childEventListener =  databaseReference.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val m = snapshot.getValue(Mensaje::class.java)
                adapter.addMensaje(m!!)
                setScrollBar()
             //   Toast.makeText(applicationContext, "nuevo mensaje ", Toast.LENGTH_SHORT).show()
                if (m!!.tipo_usuario == "cliente"){
                   mensajesVistokerkly(snapshot.key!!)
                }
                val mGestureDetector = GestureDetector(applicationContext,
                    object : GestureDetector.SimpleOnGestureListener() {
                        override fun onSingleTapUp(e: MotionEvent): Boolean {
                            return true
                        }
                    })
                recyclerView.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener{
                    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                        try {
                            val child = recyclerView.findChildViewUnder(e.x, e.y)
                            if (child != null && mGestureDetector.onTouchEvent(e)) {
                                val position = recyclerView.getChildAdapterPosition(child)
                                //Toast.makeText(applicationContext, "click en ${adapter.lista[position].mensaje}",Toast.LENGTH_SHORT).show()
                                if (adapter.lista[position].tipo_usuario == "Kerkly"){
                                    if (adapter.lista[position].tipoArchivo == "imagen"){
                                        imagenCompleta.visibility = View.VISIBLE
                                        val url = Uri.parse(adapter.lista[position].archivo)
                                        // println("url imagen " +adapter.lista[position].archivo)
                                        Glide.with(applicationContext)
                                            .load(url)
                                            .into(imagenCompleta)
                                    }
                                    //Toast.makeText(applicationContext, "es mensaje de kerkly ${adapter.lista[position].mensaje}",Toast.LENGTH_SHORT).show()
                                }
                                if (adapter.lista[position].tipo_usuario == "cliente"){
                                        if (adapter.lista[position].tipoArchivo == "pdf"){
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                                                if (!Environment.isExternalStorageManager()) {
                                                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                                                    startActivity(intent)
                                                }else{
                                                    showOptionsDialogPDF(position,adapter.lista[position].mensaje, adapter.lista[position].archivo)
                                                }
                                            }else{
                                                showOptionsDialogPDF(position,adapter.lista[position].mensaje, adapter.lista[position].archivo)
                                            }
                                        }
                                        if (adapter.lista[position].tipoArchivo == "imagen"){
                                            showOptionsDialog(position, adapter.lista[position].mensaje, adapter.lista[position].archivo)
                                        }


                                }
                                return true
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                        return false
                    }

                    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {
                        showMensaje(e.toString())
                    }

                    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {

                    }

                })

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val m = snapshot.getValue(Mensaje::class.java)
                adapter.addMensajeClear()
                adapter.addMensaje(m!!)
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
               println("---> entro onChildRemoved")
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                println("---> entro onChildMoved")
            }

            override fun onCancelled(error: DatabaseError) {
                println("---> entro onCancelled")
            }

        })

        childEventListener2 = databaseReferenceCliente.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                val m = snapshot.getValue(MensajeCopia::class.java)
                if (m!!.tipo_usuario == "cliente"){
                    println("id ${snapshot.key}")
                    println("${snapshot.child("tipo_usuario")}")

                    mensajesVistoCliente(snapshot.key!!)
                }else{
                    println("no es cliente")
                    println("id ${snapshot.key}")
                    println("${snapshot.child("tipo_usuario")}")
                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
               println("actualizado")
                adapter.notifyDataSetChanged()
            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                adapter.notifyDataSetChanged()
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                adapter.notifyDataSetChanged()
            }

        })

        progressBar= findViewById(R.id.progressBar)
        botonArchivos = findViewById(R.id.imageButtonEnviarArchivo)
        botonArchivos.setOnClickListener {
            seleccionarArchivoPDF();

        }
        if (directoMaps == "Maps"){
            Seguimineto()
        }

        buttonGPs.setOnClickListener {
            stopLocationService()
        }
    }
    fun showMensaje(mensaje:String){
        Toast.makeText(this,mensaje,Toast.LENGTH_SHORT).show()
    }
    private fun Seguimineto(){
        val options = arrayOf("Si", "No")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("¿Deseas Compartir la ubicacion en tiempo real con el cliente?")
        builder.setItems(options) { dialog: DialogInterface, which: Int ->
            when (which) {
                0 -> {
                    println("DFsd")
                  startService(locationServiceIntent)

                    dialog.dismiss()
                }
                1 -> {
                    println("DFsd")
                    dialog.dismiss()
                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog: DialogInterface, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }


    private fun stopLocationService() {
            stopService(locationServiceIntent)
      //  fusedLocationClient.removeLocationUpdates(locationCallback)
            buttonGPs.isEnabled = false // Opcional: Deshabilita el botón después de detener el servicio
    }

    private fun showOptionsDialog(position: Int, archivo: String, urlImagen: String) {
        val options = arrayOf("Ver imagen", "Descargar imagen")

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Opciones")
        builder.setItems(options) { dialog: DialogInterface, which: Int ->
            when (which) {
                0 -> {

                    frameLayout.setBackgroundResource(android.R.color.transparent)
                    // Acción para "Ver imagen"
                     imagenCompleta.visibility = View.VISIBLE
                                      val url = Uri.parse(urlImagen)
                                    //  println("url imagen " +adapter.lista[position].archivo)
                                      Glide.with(applicationContext)
                                          .load(url)
                                          .into(imagenCompleta)

                }
                1 -> {
                    // Acción para "Descargar imagen"
                      progressBar.visibility = View.VISIBLE
                                        //descargar la imagen
                                        val storage = FirebaseStorage.getInstance()
                                        val storageRef = storage.reference
                                        // Reemplaza "nombre_del_archivo.jpg" con el nombre del archivo de imagen que deseas descargar
                                        val imageRef = storageRef.child("UsuariosR").child(telefonoCliente).child("chats").child("$telefonoCliente"+"_"+"$telefonoKerkly").child(archivo)
                                        val localFile = File.createTempFile("$archivo", "jpg")
                                        val ruta = getRuta(archivo)
                                        val uploadTask = storageRef.getFile(ruta!!)
                                        // Registra un Listener para obtener la URL del archivo una vez cargado
                                        uploadTask.addOnProgressListener {taskSnapshot ->
                                            // Calcula el progreso en porcentaje
                                            val progress = 100.0 * taskSnapshot!!.bytesTransferred / taskSnapshot!!.totalByteCount
                                            // Actualiza la barra de progreso
                                            progressBar.progress = progress.toInt()
                                        }
                                        imageRef.getFile(localFile)
                                            .addOnSuccessListener {
                                                // La imagen se descargó exitosamente
                                                // Puedes guardar la imagen en una ubicación específica utilizando el siguiente código

                                                val destinationFile = File(ruta!!.toURI())
                                                localFile.copyTo(destinationFile, overwrite = true)
                                                Toast.makeText(applicationContext, "Imagen Descargada",Toast.LENGTH_SHORT).show()
                                                // La imagen se ha guardado en la ubicación especificada
                                                progressBar.visibility = View.GONE

                                            }
                                            .addOnFailureListener { exception ->
                                                // Ocurrió un error al descargar la imagen
                                                // Maneja el error de acuerdo a tus necesidades
                                                Toast.makeText(applicationContext, "Ocurrió un error al descargar la imagen",Toast.LENGTH_SHORT).show()
                                                progressBar.visibility = View.GONE
                                            }
                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog: DialogInterface, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, realizar las acciones necesarias aquí
                // Toast.makeText(this,"Permiso concedido",Toast.LENGTH_SHORT).show()
                // println("permiso $REQUEST_CODE")
            } else {
                // Permiso denegado, manejar la situación de permiso denegado
                //  Toast.makeText(this,"Permiso denegado",Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showOptionsDialogPDF(position: Int, Nombrearchivo: String, urlPdf: String) {
        val options = arrayOf("Ver PDF", "Descargar PDF")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Opciones")
        builder.setItems(options) { dialog: DialogInterface, which: Int ->
            when (which) {
                0 -> {
//                    // Acción para "Ver PDF"
//                    PantallaCompletaPdf.visibility = View.VISIBLE
//                    val pdfUrl = Uri.parse(urlPdf)
//                    PantallaCompletaPdf.fromUri(pdfUrl)
//                        .load()

                    PantallaCompletaPdf.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                    //descargar pdf
                   // val storage = FirebaseStorage.getInstance()
                 //   val storageRef = storage.reference
                    // Reemplaza "nombre_del_archivo.pdf" con el nombre del archivo PDF que deseas descargar
                    //val pdfRef = storageRef.child("UsuariosR").child(telefonoCliente).child("chats").child("$telefonoCliente"+"_"+"$telefonoKerkly").child(Nombrearchivo)
                    val pdfRef = instancias.StorageReferenceKerkly(uidKerkly, uidCliente, Nombrearchivo)
                    val localFile = File.createTempFile("$Nombrearchivo", "pdf")
                    val ruta = getRuta(Nombrearchivo)
                    val uploadTask = pdfRef.getFile(ruta!!)
                    // Registra un Listener para obtener la URL del archivo una vez cargado
                    uploadTask.addOnProgressListener {taskSnapshot ->
                        // Calcula el progreso en porcentaje
                        val progress = 100.0 * taskSnapshot!!.bytesTransferred / taskSnapshot!!.totalByteCount
                        // Actualiza la barra de progreso
                        progressBar.progress = progress.toInt()
                    }
                    pdfRef.getFile(localFile)
                        .addOnSuccessListener {
                            // El archivo PDF se descargó exitosamente, puedes realizar las operaciones necesarias aquí
                            // localFile contiene la ubicación del archivo descargado en el dispositivo
                            val destinationFile = File(ruta!!.toURI())
                            localFile.copyTo(destinationFile, overwrite = true)
                            Toast.makeText(applicationContext, "Archivo Descargado",Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                            PantallaCompletaPdf.fromFile(File(destinationFile.toURI()))
                                .defaultPage(0)
                                .enableSwipe(true)
                                .swipeHorizontal(false)
                                .load()
                         /*   GlobalScope.launch(Dispatchers.IO) {
                                val parcelFileDescriptor: ParcelFileDescriptor =
                                    ParcelFileDescriptor.open(destinationFile, ParcelFileDescriptor.MODE_READ_ONLY)
                                pdfRenderer = PdfRenderer(parcelFileDescriptor)

                              //  showPdfPage(0) // Muestra la primera página del PDF


                            }*/
                           // showPdf(destinationFile)
                        }
                        .addOnFailureListener { exception ->
                            // Ocurrió un error al descargar el archivo PDF
                            // Maneja el error de acuerdo a tus necesidades
                            Toast.makeText(applicationContext, "Ocurrió un error al descargar el archivo PDF",Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }


                }
                1 -> {
                    // Acción para "Descargar PDF"
                    //imagenCompleta.visibility = View.VISIBLE
                    progressBar.visibility = View.VISIBLE
                    //descargar pdf
                   // val storage = FirebaseStorage.getInstance()
                  //  val storageRef = storage.reference
                    // Reemplaza "nombre_del_archivo.pdf" con el nombre del archivo PDF que deseas descargar
                    //val pdfRef = storageRef.child("UsuariosR").child(telefonoCliente).child("chats").child("$telefonoCliente"+"_"+"$telefonoKerkly").child(Nombrearchivo)
                    val pdfRef = instancias.StorageReferenceKerkly(uidKerkly, uidCliente,Nombrearchivo)
                    val localFile = File.createTempFile("$Nombrearchivo", "pdf")
                    val ruta = getRuta(Nombrearchivo)
                    val uploadTask = pdfRef.getFile(ruta!!)
                    // Registra un Listener para obtener la URL del archivo una vez cargado
                    uploadTask.addOnProgressListener {taskSnapshot ->
                        // Calcula el progreso en porcentaje
                        val progress = 100.0 * taskSnapshot!!.bytesTransferred / taskSnapshot!!.totalByteCount
                        // Actualiza la barra de progreso
                        progressBar.progress = progress.toInt()
                    }
                    pdfRef.getFile(localFile)
                        .addOnSuccessListener {
                            // El archivo PDF se descargó exitosamente, puedes realizar las operaciones necesarias aquí
                            // localFile contiene la ubicación del archivo descargado en el dispositivo
                            val destinationFile = File(ruta!!.toURI())
                            localFile.copyTo(destinationFile, overwrite = true)
                            Toast.makeText(applicationContext, "Archivo Descargado",Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }
                        .addOnFailureListener { exception ->
                            // Ocurrió un error al descargar el archivo PDF
                            // Maneja el error de acuerdo a tus necesidades
                            Toast.makeText(applicationContext, "Ocurrió un error al descargar el archivo PDF",Toast.LENGTH_SHORT).show()
                            progressBar.visibility = View.GONE
                        }

                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog: DialogInterface, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

    private fun showPdf(pdfFile: File) {
        lifecycleScope.launch {
            withContext(Dispatchers.Main) {
                // Crea un objeto PdfRenderer utilizando un ParcelFileDescriptor para el archivo PDF
                val fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)
                val pdfRenderer = PdfRenderer(fileDescriptor)

                // Muestra la primera página del PDF
                showPage(pdfRenderer, pageNumber = 0)

                // Cierra el objeto PdfRenderer cuando hayas terminado de usarlo
                pdfRenderer.close()
                fileDescriptor.close()
            }
        }
        }

    private suspend fun showPdfPage(pageIndex: Int) {
        withContext(Dispatchers.IO) {
            val page: PdfRenderer.Page = pdfRenderer.openPage(pageIndex)

            val bitmap: Bitmap = Bitmap.createBitmap(
                page.width,
                page.height,
                Bitmap.Config.ARGB_8888
            )

            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            withContext(Dispatchers.Main) {
                imagenCompleta.setImageBitmap(bitmap)
            }

            page.close()
        }
    }


        private fun showPage(pdfRenderer: PdfRenderer, pageNumber: Int) {
        // Abre la página en el índice especificado
        val pdfPage = pdfRenderer.openPage(pageNumber)
        // Crea un bitmap para mostrar la página
        val bitmap = Bitmap.createBitmap(pdfPage.width, pdfPage.height, Bitmap.Config.ARGB_8888)

        // Renderiza la página en el bitmap
        pdfPage.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

        // Muestra el bitmap en el ImageView
        imagenCompleta.setImageBitmap(bitmap)

        // Cierra la página cuando hayas terminado de usarla
        pdfPage.close()
    }

    fun getRuta(NOMBRE_DIRECTORIO: String): File? {
        // El fichero sera almacenado en un directorio dentro del directorio
        // Descargas
        var ruta: File? = null
        if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()) {
            //  println("nombre archivo 325 " +NOMBRE_DIRECTORIO)
            ruta = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), NOMBRE_DIRECTORIO)
            if (ruta.exists()) {
                // El directorio de descargas existe
                println("El directorio de descargas existe")
            } else {
                // El directorio de descargas no existe
                println("El directorio de descargas no existe")
                ruta.mkdirs()
            }
            /*   if (ruta != null) {
                    if (!ruta.mkdirs()) {
                        if (!ruta.exists()) {
                            return null
                        }
                    }
                }*/
        }
        return ruta
    }

    private fun seleccionarArchivoPDF() {
        val options = arrayOf("Enviar Imagen", "Enviar PDF")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Opciones")
        builder.setItems(options) { dialog: DialogInterface, which: Int ->
            when (which) {
                0 -> {
                    progressBar.visibility = View.VISIBLE
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "image/*"
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    startActivityForResult(intent, REQUEST_CODE_FILE)
                }
                1 -> {
                    progressBar.visibility = View.VISIBLE
                    val intent = Intent(Intent.ACTION_GET_CONTENT)
                    intent.type = "application/pdf"
                    intent.addCategory(Intent.CATEGORY_OPENABLE)
                    startActivityForResult(intent, REQUEST_CODE_FILE)

                }
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog: DialogInterface, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILE && resultCode == RESULT_OK && data != null) {
            progressBar.visibility = View.VISIBLE
             uriArchivo = data.data!!

            uriArchivo?.let { uri ->
                val fileType: String? = contentResolver.getType(uri)
                fileType?.let {
                    // Aquí tienes el tipo de archivo seleccionado
                    // Puedes realizar acciones adicionales en función del tipo de archivo
                    System.out.println("uri $fileType")
                    if (fileType  == "application/pdf"){
                        println("seleciono un pdf")
                        // Realiza las operaciones necesarias con el archivo PDF seleccionado
                        EnviarArchivo(uriArchivo, "pdf")
                    }else{
                        EnviarArchivo(uriArchivo, "imagen")
                        println("otro archivo")
                    }
                }
            }

        }
    }

    private fun EnviarArchivo(uriArchivo: Uri, tipoArchivo: String){
        val nombreArchivo: String = obtenerNombreArchivo(uriArchivo)
        // Obtén una referencia al storage de Firebase
        val storageRef = instancias.storageRef
        // Crea un nombre de archivo único para evitar conflictos
       // val fileRef = storageRef.child("UsuariosR").child(telefonoKerkly.toString()).child("chats").child("$telefonoKerkly"+"_"+"$telefonoCliente").child(filename)
        val fileRef = instancias.StorageReferenceKerkly(uidKerkly, uidCliente,nombreArchivo)

        if (tipoArchivo == "pdf"){
            println("nombre del archivo " + nombreArchivo)
            // Carga el archivo PDF en Firebase Storage
            val uploadTask = fileRef.putFile(uriArchivo)
            // Registra un Listener para obtener la URL del archivo una vez cargado
            uploadTask.addOnProgressListener {taskSnapshot ->
                // Calcula el progreso en porcentaje
                val progress = 100.0 * taskSnapshot!!.bytesTransferred / taskSnapshot!!.totalByteCount
                // Actualiza la barra de progreso
                progressBar.progress = progress.toInt()
            }
            uploadTask.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                // Obtiene la URL de descarga del archivo
                fileRef.downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        // Guarda la URL del archivo en Firebase Realtime Database
                        //val databaseRef = FirebaseDatabase.getInstance().reference
                         val fileUrl = uri.toString()
                        val databaseReference = instancias.chatsKerkly(uidKerkly, uidCliente)
                        databaseReference.push().setValue(Mensaje(nombreArchivo, getTime(),"",fileUrl,tipoArchivo))
                        val databaseReferenceCliente = instancias.chatsCliente(uidKerkly, uidCliente)
                        databaseReferenceCliente.push().setValue(Mensaje(nombreArchivo, getTime(),"",fileUrl,tipoArchivo))

                            storageRef.child("$tipoArchivo").child(fileUrl)
                        llamartopico.llamartopico(this,tokenCliente, nombreArchivo, nombreCompletoKerkly)
                        Toast.makeText(applicationContext, "archivo enviado", Toast.LENGTH_SHORT).show()
                        progressBar.visibility =View.GONE
                    }
            }
        }else{
            // Carga el archivo PDF en Firebase Storage
            val uploadTask = fileRef.putFile(uriArchivo)
            // Registra un Listener para obtener la URL del archivo una vez cargado
            uploadTask.addOnProgressListener {taskSnapshot ->
                // Calcula el progreso en porcentaje
                val progress = 100.0 * taskSnapshot!!.bytesTransferred / taskSnapshot!!.totalByteCount
                // Actualiza la barra de progreso
                progressBar.progress = progress.toInt()
            }
            uploadTask.addOnSuccessListener { taskSnapshot: UploadTask.TaskSnapshot? ->
                // Obtiene la URL de descarga del archivo
                fileRef.downloadUrl
                    .addOnSuccessListener { uri: Uri ->
                        // Guarda la URL del archivo en Firebase Realtime Database
                        //val databaseRef = FirebaseDatabase.getInstance().reference
                        val fileUrl = uri.toString()
                        val databaseReference = instancias.chatsKerkly(uidKerkly, uidCliente)
                        databaseReference.push().setValue(Mensaje(nombreArchivo, getTime(),"",fileUrl,tipoArchivo))
                        val databaseReferenceCliente = instancias.chatsCliente(uidKerkly, uidCliente)
                        databaseReferenceCliente.push().setValue(Mensaje(nombreArchivo, getTime(),"",fileUrl,tipoArchivo))
                        storageRef.child("$tipoArchivo").child(fileUrl)
                        llamartopico.llamartopico(this,tokenCliente, nombreArchivo, nombreCompletoKerkly)
                        Toast.makeText(applicationContext, "archivo enviado", Toast.LENGTH_SHORT).show()
                        progressBar.visibility =View.GONE
                    }
            }
        }
    }

    @SuppressLint("Range")
    private fun obtenerNombreArchivo(uri: Uri): String {
        var nombre: String? = null
        if (uri.scheme.equals("content")) {
            contentResolver.query(uri, null, null, null, null).use { cursor ->
                if (cursor != null && cursor.moveToFirst()) {
                    nombre = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                }
            }
        }
        if (nombre == null) {
            nombre = uri.path
            val index = nombre!!.lastIndexOf("/")
            if (index != -1) {
                nombre = nombre!!.substring(index + 1)
            }
        }
        return nombre!!
    }


    private fun mensajesVistoCliente(key: String) {
       // firebaseDatabase = FirebaseDatabase.getInstance()
      //  databaseReferenceMensajeCliente = firebaseDatabase.getReference("UsuariosR").child(telefonoCliente.toString()).child("chats") .child("$telefonoCliente"+"_"+"$telefonoKerkly").child(key)
        val databaseReferenceMensajeCliente = instancias.chatsCliente(uidKerkly, uidCliente).child(key)
        val map = mapOf("mensajeLeido" to "Visto")
        databaseReferenceMensajeCliente.updateChildren(map)
    }

    fun mensajesVistokerkly(id: String){
       // firebaseDatabase = FirebaseDatabase.getInstance()
        //databaseReferenceMensajekerkly = firebaseDatabase.getReference("UsuariosR").child(telefonoKerkly.toString()).child("chats").child("$telefonoKerkly"+"_"+"$telefonoCliente").child(id)
        val databaseReferenceMensajekerkly =  instancias.chatsKerkly(uidKerkly, uidCliente).child(id)
        val map = mapOf("mensajeLeido" to "Visto")
         databaseReferenceMensajekerkly.updateChildren(map)
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
    override fun onBackPressed() {
        if (directoMaps == "Maps"){
            val intent = Intent(this, InterfazKerkly::class.java)
            intent.putExtra("numT", telefonoKerkly)
            startActivity(intent)
            finish()
        }

        if ( imagenCompleta.visibility == View.VISIBLE){
            imagenCompleta.visibility = View.GONE
            imagenCompleta?.setImageDrawable(null)

        }else{
            if (PantallaCompletaPdf.visibility == View.VISIBLE){
                PantallaCompletaPdf.visibility  =View.GONE

            }else
      finish()
        if (childEventListener == null || childEventListener2 == null){
          //  Toast.makeText(applicationContext, "null el childEventListener", Toast.LENGTH_SHORT).show()
        }else {
         //  Toast.makeText(applicationContext, "childEventListener detnido ", Toast.LENGTH_SHORT).show()
            val databaseReference = instancias.chatsKerkly(uidKerkly, uidCliente)
            val databaseReferenceCliente = instancias.chatsCliente(uidKerkly, uidCliente)
            databaseReference.removeEventListener(childEventListener!!);
            databaseReferenceCliente.removeEventListener(childEventListener2!!);
        }
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        if (childEventListener!= null) {
            val databaseReference = instancias.chatsKerkly(uidKerkly, uidCliente)
            val databaseReferenceCliente = instancias.chatsCliente(uidKerkly, uidCliente)
            databaseReference.removeEventListener(childEventListener!!);
            databaseReferenceCliente.removeEventListener(childEventListener2!!);
        }
       // stopService(locationServiceIntent)
    }
}


