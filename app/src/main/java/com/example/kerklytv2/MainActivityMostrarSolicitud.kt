package com.example.kerklytv2

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kerklytv2.controlador.AdapterPresupuesto
import com.example.kerklytv2.controlador.ClaseAdapterR
import com.example.kerklytv2.controlador.SetProgressDialog
import com.example.kerklytv2.interfaces.PresupuestoInterface
import com.example.kerklytv2.interfaces.PresupuestoNormalInterface
import com.example.kerklytv2.modelo.serial.PresupuestourgentesDatosCliente
import com.example.kerklytv2.url.Url
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface LoadMoreListener {
    fun onLoadMore()
}
class MainActivityMostrarSolicitud : AppCompatActivity(), SearchView.OnQueryTextListener,
LoadMoreListener {
    private lateinit var telefonoKerkly: String
    private lateinit var nombreKerkly: String
    private lateinit var CurpKerkly:String
    private lateinit var correoKerkly: String
    private lateinit var TipoSolicitud:String
    private lateinit var DireccionKerkly:String

    //urgente
    private lateinit var postListUrgente: ArrayList<PresupuestourgentesDatosCliente>
    private lateinit var recyclerview: RecyclerView
    private lateinit var MiAdapterUrgente: ClaseAdapterR

    //normal
    private lateinit var postListNormal: ArrayList<com.example.kerklytv2.modelo.serial.Presupuesto>
    private lateinit var myAdapterNormal: AdapterPresupuesto

    private lateinit var searchView: SearchView

    private lateinit var b: Bundle
    val setProgress= SetProgressDialog()

    private lateinit var imageViewSinDatos: ImageView
    private lateinit var txtSinDatos: TextView

    //paginacion de consultas
    private var currentPage = 1
    private val pageSize = 15 // 10 elementos por página
    private var isLoading = false
    private var shouldLoadMoreData = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_mostrar_solicitud)

        imageViewSinDatos =  findViewById(R.id.img_presupuesto)
        txtSinDatos = findViewById(R.id.txt_presupuesto)
        recyclerview = findViewById(R.id.recycler_presupuesto)
        recyclerview.setHasFixedSize(true)
        recyclerview.layoutManager= LinearLayoutManager(this)
        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(this)

        b = intent.extras!!
        TipoSolicitud = b!!.getString("TipoDeSolicitud").toString()
        telefonoKerkly = b!!.getString("telefonoKerkly").toString()
        nombreKerkly = b!!.getString("nombreCompletoKerkly").toString()
        CurpKerkly = b!!.getString("Curp").toString()
        correoKerkly = b!!.getString("correoKerkly").toString()

        if (TipoSolicitud == "urgente"){
            ObtenerSolictudUrgente()
        }
        if (TipoSolicitud == "normal"){
            ObtenerSolictudNormal()
        }

    }

    private fun ObtenerSolictudNormal() {
        setProgress.setProgressDialog(this)
        val ROOT_URL = Url().URL
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("$ROOT_URL/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val presupuestoGET = retrofit.create(PresupuestoNormalInterface::class.java)
        val call = presupuestoGET.getPost(telefonoKerkly,currentPage,pageSize)
        call?.enqueue(object : Callback<List<com.example.kerklytv2.modelo.serial.Presupuesto?>?> {

            override fun onResponse(
                call: Call<List<com.example.kerklytv2.modelo.serial.Presupuesto?>?>,
                response: Response<List<com.example.kerklytv2.modelo.serial.Presupuesto?>?>
            ) {

                 postListNormal = response.body() as ArrayList<com.example.kerklytv2.modelo.serial.Presupuesto>
                myAdapterNormal = AdapterPresupuesto(postListNormal)
                if(postListNormal.size == 0) {
                    recyclerview.visibility = View.GONE
                    imageViewSinDatos.visibility = View.VISIBLE
                    txtSinDatos.visibility = View.VISIBLE
                    setProgress.dialog!!.dismiss()
                } else {
                    recyclerview.visibility = View.VISIBLE
                    imageViewSinDatos.visibility = View.GONE
                    txtSinDatos.visibility = View.GONE
                    currentPage++
                    myAdapterNormal.loadMoreListener = this@MainActivityMostrarSolicitud
                    myAdapterNormal.setOnClickListener {
                        val latitud = postListNormal[recyclerview.getChildAdapterPosition(it)].latitud
                        val longitud = postListNormal[recyclerview.getChildAdapterPosition(it)].longitud
                        val folio = postListNormal[recyclerview.getChildAdapterPosition(it)].idPresupuesto
                        val colonia = postListNormal[recyclerview.getChildAdapterPosition(it)].Colonia
                        val calle = postListNormal[recyclerview.getChildAdapterPosition(it)].Calle
                        val cp = postListNormal[recyclerview.getChildAdapterPosition(it)].Codigo_Postal
                        val referencia = postListNormal[recyclerview.getChildAdapterPosition(it)].Referencia
                        var ext = postListNormal[recyclerview.getChildAdapterPosition(it)].No_Exterior

                        val n = postListNormal[recyclerview.getChildAdapterPosition(it)].Nombre
                        val ap = postListNormal[recyclerview.getChildAdapterPosition(it)].Apellido_Paterno
                        val am = postListNormal[recyclerview.getChildAdapterPosition(it)].Apellido_Materno

                        val numerocliente = postListNormal[recyclerview.getChildAdapterPosition(it)].telefonoCliente
                        val problema = postListNormal[recyclerview.getChildAdapterPosition(it)].problema
                        val correoCliente = postListNormal[recyclerview.getChildAdapterPosition(it)].Correo
                        val uidCliente  =  postListNormal[recyclerview.getChildAdapterPosition(it)].uidCliente

                        if (ext == "0") {
                            ext = "S/N"
                        }
                        val direccion = "$calle $colonia $ext $cp $referencia"
                        val nombre = "$n $ap $am"

                        //  Toast.makeText(context, "Nombre: $n", Toast.LENGTH_SHORT).show()
                        val i = Intent(this@MainActivityMostrarSolicitud, MapsActivity::class.java)
                        i.putExtra("telefonok", telefonoKerkly)
                        i.putExtra("telefonoCliente", numerocliente)
                        i.putExtra("tipoServicio", "normal")
                        i.putExtra("latitud", latitud)
                        i.putExtra("longitud", longitud)
                        i.putExtra("nombreCompletoCliente", nombre)
                        i.putExtra("nombreCompletoKerkly", nombreKerkly)
                        i.putExtra("problema", problema)
                        i.putExtra("direccion", direccion)
                        i.putExtra("correoCliente", correoCliente)
                        i.putExtra("Folio", folio.toString())
                        i.putExtra("Curp", CurpKerkly)
                        i.putExtra("correoKerly", correoKerkly)
                        i.putExtra("direccionkerkly", direccion)
                        i.putExtra("uidCliente",uidCliente)
                        startActivity(i)

                    }

                    recyclerview.adapter = myAdapterNormal
                    recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                            val totalItemCount = layoutManager.itemCount

                            if (!isLoading && lastVisibleItemPosition == totalItemCount - 1) {
                                isLoading = true
                                onLoadMore()
                                println("Scroll detectado - lastVisibleItemPosition: $lastVisibleItemPosition, totalItemCount: $totalItemCount")
                            }
                        }
                    })
                    setProgress.dialog!!.dismiss()
                }
            }

            override fun onFailure(call: Call<List<com.example.kerklytv2.modelo.serial.Presupuesto?>?>, t: Throwable) {
                Toast.makeText(
                    this@MainActivityMostrarSolicitud,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show()
                Log.d("erro", t.toString())
                setProgress.dialog!!.dismiss()
                imageViewSinDatos.visibility =  View.VISIBLE
            }
        })

    }

    private fun ObtenerSolictudUrgente() {
        setProgress.setProgressDialog(this)
        val ROOT_URL = Url().URL
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().addInterceptor(interceptor).build()

        val retrofit = Retrofit.Builder()
            .baseUrl("$ROOT_URL/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val presupuestoGET = retrofit.create(PresupuestoInterface::class.java)
        val call = presupuestoGET.getPresupuestoClienteRegistrado(telefonoKerkly, currentPage, pageSize)
        call?.enqueue(object : Callback<List<PresupuestourgentesDatosCliente?>?> {

            override fun onResponse(call: Call<List<PresupuestourgentesDatosCliente?>?>, response: Response<List<PresupuestourgentesDatosCliente?>?>) {
                postListUrgente = response.body() as ArrayList<PresupuestourgentesDatosCliente>
                //carsModels = response.body() as ArrayList<presupuestok>
                // Log.d("Lista", postList.toString())
                MiAdapterUrgente = ClaseAdapterR(postListUrgente)
                if(postListUrgente.size == 0) {
                    recyclerview.visibility = View.GONE
                    setProgress.dialog!!.dismiss()
                    imageViewSinDatos.visibility = View.VISIBLE
                    txtSinDatos.visibility = View.VISIBLE
                } else {
                    recyclerview.visibility = View.VISIBLE
                    imageViewSinDatos.visibility = View.GONE
                    txtSinDatos.visibility = View.GONE
                    currentPage++
                    MiAdapterUrgente.loadMoreListener = this@MainActivityMostrarSolicitud
                    MiAdapterUrgente.setOnClickListener {
                        val  folioo = postListUrgente[recyclerview.getChildAdapterPosition(it)].idPresupuesto
                        val latitud = postListUrgente[recyclerview.getChildAdapterPosition(it)].latitud
                        val longitud = postListUrgente[recyclerview.getChildAdapterPosition(it)].longitud
                        val colonia = postListUrgente[recyclerview.getChildAdapterPosition(it)].Colonia
                        val calle = postListUrgente[recyclerview.getChildAdapterPosition(it)].Calle
                        val cp = postListUrgente[recyclerview.getChildAdapterPosition(it)].Codigo_Postal
                        val referencia = postListUrgente[recyclerview.getChildAdapterPosition(it)].Referencia
                        var ext = postListUrgente[recyclerview.getChildAdapterPosition(it)].No_Exterior

                        val n = postListUrgente[recyclerview.getChildAdapterPosition(it)].Nombre
                        val ap = postListUrgente[recyclerview.getChildAdapterPosition(it)].Apellido_Paterno
                        val am = postListUrgente[recyclerview.getChildAdapterPosition(it)].Apellido_Materno
                        val correo = postListUrgente[recyclerview.getChildAdapterPosition(it)].Correo
                        val numeroCliente = postListUrgente[recyclerview.getChildAdapterPosition(it)].telefonoCliente
                        val problema = postListUrgente[recyclerview.getChildAdapterPosition(it)].problema
                        val uidCliente = postListUrgente[recyclerview.getChildAdapterPosition(it)].uidCliente
                        if (ext == "0") {
                            ext = "S/N"
                        }
                        DireccionKerkly = "$calle $colonia $ext $cp $referencia"
                        val Clientenombre = "$n $ap $am"

                        val i = Intent(this@MainActivityMostrarSolicitud, MapsActivity::class.java)
                        i.putExtra("latitud", latitud)
                        i.putExtra("longitud", longitud)
                        i.putExtra("Folio", folioo.toString())
                        i.putExtra("nombreCompletoCliente", Clientenombre)
                        i.putExtra("Dirección", DireccionKerkly)
                        i.putExtra("problema", problema)
                        i.putExtra("telefonoCliente", numeroCliente)
                        i.putExtra("tipoServicio", "urgente")
                        i.putExtra("Curp", CurpKerkly)
                        i.putExtra("telefonok", telefonoKerkly)
                        i.putExtra("correo", correo)
                        i.putExtra("nombreCompletoKerkly", nombreKerkly)
                        i.putExtra("uidCliente",uidCliente)
                        startActivity(i)

                    }
                    recyclerview.adapter = MiAdapterUrgente
                    recyclerview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)
                            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
                            val totalItemCount = layoutManager.itemCount

                            if (!isLoading && lastVisibleItemPosition == totalItemCount - 1) {
                                isLoading = true
                                onLoadMore()
                                println("Scroll detectado - lastVisibleItemPosition: $lastVisibleItemPosition, totalItemCount: $totalItemCount")
                            }
                        }
                    })
                    setProgress.dialog!!.dismiss()
                }
            }
            override fun onFailure(call: Call<List<PresupuestourgentesDatosCliente?>?>, t: Throwable) {
                Toast.makeText(
                    this@MainActivityMostrarSolicitud,
                    "Codigo de respuesta de error: $t",
                    Toast.LENGTH_SHORT
                ).show();
                setProgress.dialog!!.dismiss()
                imageViewSinDatos.visibility =  View.VISIBLE
            }
        })
    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
       return false
    }

    override fun onQueryTextChange(textoIngresado: String?): Boolean {
        if (textoIngresado.isNullOrBlank()){
            if (TipoSolicitud == "urgente"){
                MiAdapterUrgente.showOriginalList()
            }
            if(TipoSolicitud == "normal"){
                myAdapterNormal.showOriginalList()
            }
        }else{
            if (TipoSolicitud == "urgente"){
                MiAdapterUrgente.filter.filter(textoIngresado)
            }
            if (TipoSolicitud == "normal"){
                myAdapterNormal.filter.filter(textoIngresado)
            }
        }
        return true
    }

    override fun onLoadMore() {
        if (TipoSolicitud == "urgente"){
            isLoading = true
            cargarMasDatosUrgentes()
        }
        if (TipoSolicitud == "normal"){
            isLoading =  true
            cargarMasDatosNormal()
        }
    }

    private fun cargarMasDatosNormal() {
        if (!shouldLoadMoreData) {
            // Si no se deben cargar más datos, sal de la función
            return
        }
        setProgress.setProgressDialog(this)

        val ROOT_URL = Url().URL
        val gson = GsonBuilder().setLenient().create()
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl("$ROOT_URL/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val presupuestoGET = retrofit.create(PresupuestoNormalInterface::class.java)
        val call = presupuestoGET.getPost(telefonoKerkly, currentPage, pageSize)

        call?.enqueue(object : retrofit2.Callback<List<com.example.kerklytv2.modelo.serial.Presupuesto?>?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(
                call: Call<List<com.example.kerklytv2.modelo.serial.Presupuesto?>?>,
                response: retrofit2.Response<List<com.example.kerklytv2.modelo.serial.Presupuesto?>?>
            ) {
                val nuevosDatos = response.body() as ArrayList<com.example.kerklytv2.modelo.serial.Presupuesto>

                if (nuevosDatos.isNotEmpty()) {

                    // Agregar los nuevos datos a la lista existente
                    postListNormal.addAll(nuevosDatos)
                    myAdapterNormal.masDatos(nuevosDatos)
                    // Notificar al adaptador que los datos han cambiado
                    myAdapterNormal.notifyDataSetChanged()

                    // Incrementar la página para la próxima carga
                    currentPage++
                    // Log de información para verificar el estado
                    Log.d("MainActivityMostrarSolicitudes", "onResponse - currentPage: $currentPage")
                    Log.d("MainActivityMostrarSolicitudes", "onResponse - postListUrgente size: ${postListNormal.size}")
                } else {
                    // Si no hay más datos, puedes manejarlo según tus necesidades
                    Toast.makeText(this@MainActivityMostrarSolicitud,
                        "No hay más datos",
                        Toast.LENGTH_SHORT
                    ).show()
                    shouldLoadMoreData = false
                }

                isLoading = false // Restablecer el estado de carga
                setProgress.dialog!!.dismiss()
            }

            override fun onFailure(call: Call<List<com.example.kerklytv2.modelo.serial.Presupuesto?>?>, t: Throwable) {
                isLoading = false // Restablecer el estado de carga en caso de fallo
                setProgress.dialog!!.dismiss()
                Log.d("error del retrofit", t.toString())
                Toast.makeText(
                    this@MainActivityMostrarSolicitud,
                    t.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun cargarMasDatosUrgentes() {
        if (!shouldLoadMoreData) {
            // Si no se deben cargar más datos, sal de la función
            return
        }
        setProgress.setProgressDialog(this)

        val ROOT_URL = Url().URL
        val gson = GsonBuilder().setLenient().create()
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BODY
        val client: OkHttpClient = OkHttpClient.Builder().build()
        val retrofit = Retrofit.Builder()
            .baseUrl("$ROOT_URL/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()

        val presupuestoGET = retrofit.create(PresupuestoInterface::class.java)
        val call = presupuestoGET.getPresupuestoClienteRegistrado(telefonoKerkly, currentPage, pageSize)

        call?.enqueue(object : retrofit2.Callback<List<PresupuestourgentesDatosCliente?>?> {
            @SuppressLint("LongLogTag")
            override fun onResponse(
                call: Call<List<PresupuestourgentesDatosCliente?>?>,
                response: retrofit2.Response<List<PresupuestourgentesDatosCliente?>?>
            ) {
                val nuevosDatos = response.body() as ArrayList<PresupuestourgentesDatosCliente>

                if (nuevosDatos.isNotEmpty()) {

                    // Agregar los nuevos datos a la lista existente
                    postListUrgente.addAll(nuevosDatos)
                    MiAdapterUrgente.masDatos(nuevosDatos)
                    // Notificar al adaptador que los datos han cambiado
                    MiAdapterUrgente.notifyDataSetChanged()

                    // Incrementar la página para la próxima carga
                    currentPage++
                    // Log de información para verificar el estado
                    Log.d("MainActivityMostrarSolicitudes", "onResponse - currentPage: $currentPage")
                    Log.d("MainActivityMostrarSolicitudes", "onResponse - postListUrgente size: ${postListUrgente.size}")
                } else {
                    // Si no hay más datos, puedes manejarlo según tus necesidades
                    Toast.makeText(this@MainActivityMostrarSolicitud,
                        "No hay más datos",
                        Toast.LENGTH_SHORT
                    ).show()
                    shouldLoadMoreData = false
                }

                isLoading = false // Restablecer el estado de carga
                setProgress.dialog!!.dismiss()
            }

            override fun onFailure(call: Call<List<PresupuestourgentesDatosCliente?>?>, t: Throwable) {
                isLoading = false // Restablecer el estado de carga en caso de fallo
                setProgress.dialog!!.dismiss()
                Log.d("error del retrofit", t.toString())
                Toast.makeText(
                    this@MainActivityMostrarSolicitud,
                    t.toString(),
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }
}