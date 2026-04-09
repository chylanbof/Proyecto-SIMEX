package com.example.proyectosimex

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectosimex.Fragments.UsuariosFragment

class AgenteComercial : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_agente_comercial)

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.FragmentContainer, UsuariosFragment())
                .commit()
        }
    }

}