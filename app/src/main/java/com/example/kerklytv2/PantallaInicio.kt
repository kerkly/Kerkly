package com.example.kerklytv2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.kerklytv2.clases.NetworkSpeedChecker
import com.example.kerklytv2.controlador.MainActiityControlador

class PantallaInicio : AppCompatActivity() {
    private lateinit var animation: Animation
    private lateinit var ivLogo: ImageView
    private lateinit var id: String
    private lateinit var controlador: MainActiityControlador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pantalla_inicio)

        // Inicializar las variables
        id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        controlador = MainActiityControlador()

        // Acceder a las vistas mediante View Binding
        ivLogo = findViewById(R.id.img_logo_inicio)

        // Instanciamos un objeto de animación
        animation = AnimationUtils.loadAnimation(this, R.anim.animation)

        //duracion de la animaciom
        animation.duration = 100
        // Establecemos la animación al ImageView
        ivLogo.startAnimation(animation)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                // Este método es llamado cuando la animación inicia
            }

            override fun onAnimationEnd(animation: Animation) {
                // Este método es llamado cuando la animación termina
                controlador.verificarSesion(id, this@PantallaInicio)
                // Considera manejar el resultado de verificarSesion aquí
            }

            override fun onAnimationRepeat(animation: Animation) {
                // Este método es llamado en cada repetición
            }
        })
    }
}
