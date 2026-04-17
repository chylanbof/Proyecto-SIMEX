package com.example.proyectosimex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectosimex.Fragments.UsuariosFragment

class AgenteComercial : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agente_comercial)

        // 1. Recuperamos los datos del Agente que inició sesión (desde el Login)
        val agenteId = intent.getIntExtra("usuario_id", -1)
        val agenteNombre = intent.getStringExtra("usuario_nombre") ?: ""
        val agenteCognoms = intent.getStringExtra("usuario_cognoms") ?: ""
        val agenteEmpresa = intent.getStringExtra("usuario_empresa") ?: ""
        val agenteTelefon = intent.getStringExtra("usuario_telefon") ?: ""

        // 2. Configuración del Botón de Perfil (Header)
        val btnPerfil = findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.perfil)
        btnPerfil.setOnClickListener {
            val intentPerfil = Intent(this, Perfil::class.java)
            intentPerfil.putExtra("usuario_id", agenteId)
            intentPerfil.putExtra("usuario_nombre", agenteNombre)
            intentPerfil.putExtra("usuario_cognoms", agenteCognoms)
            intentPerfil.putExtra("usuario_empresa", agenteEmpresa)
            intentPerfil.putExtra("usuario_telefon", agenteTelefon)
            startActivity(intentPerfil)
        }

        // 3. Cargar el Fragment inicial (Usuarios) solo la primera vez
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.FragmentContainer, UsuariosFragment())
                .commit()
        }
    }

    /**
     * Permite a los fragmentos cambiar el título del Header común
     */
    fun actualizarTitulosHeader(nuevoTitulo: String) {
        val txtHeader = findViewById<TextView>(R.id.txtHeaderTitle)
        txtHeader?.text = nuevoTitulo
    }

    /**
     * Controla la visibilidad y acción del botón atrás del Header.
     * Se llama desde el onViewCreated de cada Fragment.
     */
    fun configurarBotonAtras(visible: Boolean, accion: (() -> Unit)? = null) {
        val btnAtras = findViewById<ImageButton>(R.id.btnVolverAtras)

        // Visibilidad
        btnAtras?.visibility = if (visible) View.VISIBLE else View.GONE

        // Acción
        btnAtras?.setOnClickListener {
            if (accion != null) {
                accion() // Acción personalizada (ej: volver a un fragment específico)
            } else {
                // Acción estándar: volver al fragment anterior en la pila
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }
}