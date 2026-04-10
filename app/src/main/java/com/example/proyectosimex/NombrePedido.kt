package com.example.proyectosimex

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class NombrePedido : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nombre_pedido)

        val numeroOferta = intent.getStringExtra("numeroOferta") ?: ""
        val nombrePedido = intent.getStringExtra("nombrePedido") ?: "Pedido"

        findViewById<TextView>(R.id.tvNumeroOferta).text = numeroOferta


        val header = findViewById<View>(R.id.layoutDeArriba)
        header.findViewById<ImageView>(R.id.imgBackgroundHeader).setImageResource(R.drawable.nombrepedido)

        header.findViewById<TextView>(R.id.txtHeaderTitle).text = nombrePedido

        findViewById<ImageButton>(R.id.btnAtras).setOnClickListener {
            finish()
        }
    }
}