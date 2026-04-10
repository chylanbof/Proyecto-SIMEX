package com.example.proyectosimex

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout

class Dashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        val usuario = intent.getStringExtra("usuario_nombre") ?: ""

        val textViewBienvenido = findViewById<TextView>(R.id.txtHeaderTitle)
        textViewBienvenido.text = "Bienvenido, $usuario"


        //Boton desplegable notificaciones
        val btnNotificaciones = findViewById<ImageButton>(R.id.btnNotificaciones)
        btnNotificaciones.setOnClickListener {
            mostrarMenu(btnNotificaciones)
        }

    }

    fun mostrarMenu(anchor: View){

        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView =inflater.inflate(R.layout.popup_notificaciones, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true)

        val location = IntArray(2)

        // Obtenemos la posición del botón en la pantalla
        anchor.getLocationInWindow(location)
        val buttonX = location[0]
        val buttonY = location[1]

        // Calculamos la posición deseada
        val posX = buttonX + anchor.width + 10 // 10px de margen
        val posY = buttonY

        // Usamos showAtLocation
        popupWindow.showAtLocation(anchor, android.view.Gravity.NO_GRAVITY, posX, posY)
    }
}