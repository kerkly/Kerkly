package com.example.kerklytv2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.kerklytv2.ui.vista.fragments.BlankFragmentChat
import com.example.kerklytv2.ui.vista.fragments.MapsFragment



class MainActivitySeguimientoDelServicio : AppCompatActivity(){
lateinit var manager: FragmentManager
lateinit var transaction: FragmentTransaction
lateinit var mapa: MapsFragment
lateinit var chat: BlankFragmentChat
private lateinit var b: Bundle
    lateinit var numeroKerkly: String
    lateinit var numeroCliente: String
    lateinit var latitud2: String
    lateinit var longitud2: String
    lateinit var nombreCliente: String
    lateinit var nombrekerkly: String
    lateinit var folio: String
    lateinit var context: Context
    lateinit var linearLayout: LinearLayout
    private lateinit var urlFotoCliente: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_seguimiento_del_servicio)

        b = intent.extras!!
        //recibimos las coordenas
        //telefono = b.getString("Telefono").toString()
        latitud2 = b.getString("latitud").toString()
        longitud2 = b.getString("longitud").toString()
        nombreCliente = b.getString("Nombre").toString()
        nombrekerkly = b.getString("nombrekerkly").toString()
        numeroCliente = b.getString("numeroCliente").toString()
        numeroKerkly = b.getString("numeroKerkly").toString()
        folio = b.getString("Folio").toString()


        //  System.out.println("latitud : $latitud2 $longitud2 $nombreCliente latitud del marcador")
        context = this


         mapa = MapsFragment()
         chat = BlankFragmentChat()

         manager = supportFragmentManager
        transaction = manager.beginTransaction()
        val args = Bundle()
        transaction.replace(R.id.ContenedorFragmentsTodos, mapa)
        transaction.addToBackStack(null)
        args.putString("latitud", latitud2)
        args.putString("longitud", longitud2)
        args.putString("Nombre", nombreCliente)
        args.putString("nombrekerkly", nombrekerkly)
        args.putString("numeroKerkly", numeroKerkly)
        args.putString("numeroCliente", numeroCliente)
        mapa.arguments = args
        transaction.commit()

     linearLayout= findViewById(R.id.linearLayoutSeguimiento)
    }

    fun eventoS(view: View) {

        mapa = MapsFragment()
        chat = BlankFragmentChat()
        manager = supportFragmentManager
        transaction = manager.beginTransaction()
         when(view.getId()){
             R.id.imageViewACeptar1 ->{
                 transaction.replace(R.id.ContenedorFragmentsTodos, mapa)
                 transaction.addToBackStack(null)
                 val args = Bundle()
                args.putString("latitud", latitud2)
                 args.putString("longitud", longitud2)
                  args.putString("Nombre", nombreCliente)
                  args.putString("nombrekerkly", nombrekerkly)
                 mapa.arguments = args
                 linearLayout.setVisibility(View.VISIBLE)

             }
             R.id.imageViewChat1 -> {
                 transaction.replace(R.id.ContenedorFragmentsTodos, chat)
                 transaction.addToBackStack(null)
                 var args = Bundle()
                 args.putString("telefonoKerkly", numeroKerkly)
                 args.putString("telefonoCliente", numeroCliente)
                 args.putString("folio", folio)
                 args.putString("nombreCompletoCliente", nombreCliente)
                 args.putString("nombreKerkly", nombrekerkly)
                 args.putString("urlFotoCliente", urlFotoCliente)

                chat.arguments = args
                 linearLayout.setVisibility(View.INVISIBLE);
             }
             else -> { // Opción default
                 System.out.println("entrooo en default")
             }

             }
        transaction.commit()
         }


 /*   override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if(keyCode== event?.keyCode){
           val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setMessage("¿Desea Salir?")
                .setPositiveButton("si",  new DialogInterface.OnClickListener(){

                })
        }
        return super.onKeyDown(keyCode, event)
    }*/

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            linearLayout.setVisibility(View.VISIBLE);

           /* val builder = AlertDialog.Builder(this)
            builder.setMessage("¿Desea salir de Stroopers?")
                .setPositiveButton("Si") { dialog, id ->
                    val intent = Intent(Intent.ACTION_MAIN)
                    intent.addCategory(Intent.CATEGORY_HOME)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                }
                .setNegativeButton(
                    "Cancelar"
                ) { dialog, id -> dialog.dismiss() }
            builder.show()*/
        }
        return super.onKeyDown(keyCode, event)
    }

}