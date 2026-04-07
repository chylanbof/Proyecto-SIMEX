package com.example.proyectosimex

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Perfil : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val usuario = intent.getStringExtra("usuario_nombre") ?: ""

        // Nombre
        findViewById<TextView>(R.id.tvNombreUsuario).text = usuario

        // Item 1
        val item1 = findViewById<View>(R.id.itemTipo)
        item1.findViewById<TextView>(R.id.tvNumero).text = "1"
        item1.findViewById<TextView>(R.id.tvDato).text = "Cliente"

        // Item 2
        val item2 = findViewById<View>(R.id.itemEmpresa)
        item2.findViewById<TextView>(R.id.tvNumero).text = "2"
        item2.findViewById<TextView>(R.id.tvDato).text = "Empresa: Tech Import"

        // Item 3
        val item3 = findViewById<View>(R.id.itemPuesto)
        item3.findViewById<TextView>(R.id.tvNumero).text = "3"
        item3.findViewById<TextView>(R.id.tvDato).text = "Puesto: Encargada"


        findViewById<TextView>(R.id.tvEditar).setOnClickListener {
        }
    }
}