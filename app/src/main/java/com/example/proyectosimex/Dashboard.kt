package com.example.proyectosimex

import android.os.Bundle
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

    }
}