package com.example.proyectosimex

import android.app.AlertDialog
import android.content.Intent
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


        findViewById<TextView>(R.id.tvNombreUsuario).text = usuario


        val item1 = findViewById<View>(R.id.itemTipo)
        item1.findViewById<TextView>(R.id.tvNumero).text = "1"
        item1.findViewById<TextView>(R.id.tvDato).text = "Cliente"


        val item2 = findViewById<View>(R.id.itemEmpresa)
        item2.findViewById<TextView>(R.id.tvNumero).text = "2"
        item2.findViewById<TextView>(R.id.tvDato).text = "Empresa: Tech Import"


        val item3 = findViewById<View>(R.id.itemPuesto)
        item3.findViewById<TextView>(R.id.tvNumero).text = "3"
        item3.findViewById<TextView>(R.id.tvDato).text = "Puesto: Encargada"


        findViewById<TextView>(R.id.tvEditar).setOnClickListener {
        }

        findViewById<ImageButton>(R.id.btnAtras).setOnClickListener {
            finish()
        }

        findViewById<ImageButton>(R.id.btnExit).setOnClickListener {
            AlertDialog.Builder(this).setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de querer cerrar la sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }.setNegativeButton("No", null).show()
        }
    }
    }
