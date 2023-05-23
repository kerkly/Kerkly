package com.example.kerklytv2.vista


import android.annotation.SuppressLint
import android.os.Bundle
import android.provider.Settings
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.kerklytv2.R
import com.example.kerklytv2.controlador.MainActiityControlador
import com.example.kerklytv2.modelo.Kerkly
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase


class MainActivity : AppCompatActivity() {
    private lateinit var editUsuario: TextInputEditText
    private lateinit var editContra: TextInputEditText
    private lateinit var layoutUsuario: TextInputLayout
    private lateinit var layoutContra: TextInputLayout
    private lateinit var kerkly: Kerkly
    private lateinit var controlador: MainActiityControlador
    private lateinit var id: String

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        controlador = MainActiityControlador()
        id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        //controlador.verificarSesion(id, this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editUsuario = findViewById(R.id.input_user)
        editContra = findViewById(R.id.input_password)
        layoutUsuario = findViewById(R.id.textnputUser)
        layoutContra = findViewById(R.id.textnputPassword)
        kerkly = Kerkly()


    }


    fun entrar(view: View) {
        val t = editUsuario.text.toString()
        if(t.equals("")) {
            layoutUsuario.error = getText(R.string.campoRequerico)
        } else {
            layoutUsuario.error = null
        }

        val c = editContra.text.toString()
        if (c.equals("")) {
            layoutContra.error = getText(R.string.campoRequerico)
        } else {
            layoutContra.error = null
        }

        if (!t.equals("") && !c.equals("")) {
            if (t.length != 10) {
                layoutUsuario.error = getText(R.string.telefonoNoValido)
            } else {
                layoutUsuario.error = null

                kerkly.setTelefono(t)
                kerkly.setContrasenia(c)

                controlador = MainActiityControlador()
                controlador.verificarUsuario(kerkly, this)

            }
        }
        // val intent = Intent(this, InterfazKerkly::class.java)
        //k startActivity(intent)
    }
}