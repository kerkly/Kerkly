package com.example.kerklytv2.ui.vista


import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.Toast
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

        val ButtonactualizarAPP = findViewById<Button>(R.id.buttonActualizarAPP)
        ButtonactualizarAPP.setOnClickListener {
            // URL de Google Drive que deseas abrir
            val urlGoogleDrive = "https://drive.google.com/file/d/1WuyI1UFpvgaIGtrIcXHvQNu3rW3Zaa5O/view?usp=drive_link"
            // Crear un Intent con la acción ACTION_VIEW y la URL
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(urlGoogleDrive))

            // Verificar si hay una aplicación para manejar la acción VIEW (navegador)
            if (intent.resolveActivity(packageManager) != null) {
                // Abrir la URL en el navegador
                startActivity(intent)
            } else {
                // Manejar el caso en el que no haya una aplicación para manejar la acción VIEW
                Toast.makeText(this, "No se encontró una aplicación para abrir la URL", Toast.LENGTH_SHORT).show()
            }
        }
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

    }
}