package com.example.proyectosimex

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class Notificaciones : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_notificaciones)

        //Variable para llamar al boton externo
        val btnNotificaciones = findViewById<Button>(R.id.btnShared)
        btnNotificaciones.text = "Perfil"

    }
}