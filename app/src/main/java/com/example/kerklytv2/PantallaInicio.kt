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
        var context = this
        NetworkSpeedChecker(this)
        controlador = MainActiityControlador()
        id = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        controlador.verificarSesion(id, this)

        ivLogo = findViewById(R.id.img_logo_inicio)
        //Instanciamos un objeto
        animation = AnimationUtils.loadAnimation(this, R.anim.animation)

        //Establecemos la animacion al image view
        ivLogo.startAnimation(animation)

        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                //Este metodo es llamado cuando la animacion inicia
                //Aqui podriamos realizar algunas comprobaciones iniciales
            }


            override fun onAnimationEnd(animation: Animation) {
                controlador.verificarSesion(id, context)
                /*val i = Intent(applicationContext, MainActivity::class.java)
                startActivity(i)
                finish()*/
            }

            override fun onAnimationRepeat(animation: Animation) {
                //Este metodo es llamado en cada repeticion
            }
        })
    }
}