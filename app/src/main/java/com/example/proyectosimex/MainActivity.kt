package com.example.proyectosimex

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import android.content.Intent
import android.widget.Button
import android.widget.EditText

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val textBoxUsuario = findViewById<EditText>(R.id.usuarioTextBox)

        val textBoxPassword = findViewById<EditText>(R.id.contraseñaTextBox)
        val btnIniciarSesion = findViewById<Button>(R.id.btnLogin)
        btnIniciarSesion.setOnClickListener {
            val usuario = textBoxUsuario.text.toString()
            val intent = Intent(this, Dashboard::class.java)
            intent.putExtra("usuario_nombre", usuario)
            startActivity(intent)
        }
    }
} //comentario para guardar la rama