package com.example.proyectosimex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectosimex.fragments.UsuariosFragment


// activity que controla los fragment de UsuarioFragment, DetalleOfertaFragment,
// CrearOfertasFragment, AdministrarOfertasFragment
class AgenteComercial : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_agente_comercial)
        val usuario = intent.getStringExtra("usuario_nombre") ?: ""
        val usuarioId = intent.getIntExtra("usuario_id", -1)
        val usuarioCognoms = intent.getStringExtra("usuario_cognoms") ?: ""
        val usuarioEmpresa = intent.getStringExtra("usuario_empresa") ?: ""
        val usuarioTelefon = intent.getStringExtra("usuario_telefon") ?: ""

        // Botón perfil
        val header = findViewById<View>(R.id.LayoutAgenteComercial)
        header.findViewById<androidx.appcompat.widget.AppCompatButton>(R.id.perfil)
            .setOnClickListener {
                val intent = Intent(this, Perfil::class.java)
                intent.putExtra("usuario_nombre", usuario)
                intent.putExtra("usuario_id", usuarioId)
                intent.putExtra("usuario_cognoms", usuarioCognoms)
                intent.putExtra("usuario_empresa", usuarioEmpresa)
                intent.putExtra("usuario_telefon", usuarioTelefon)
                startActivity(intent)
            }
        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.FragmentContainer, UsuariosFragment())
                .commit()

        }
    }

    fun actualizarTitulosHeader(nuevoTitulo: String){

        //buscamos el textView dentro del layout incluido
        val txtHeader = findViewById<TextView>(R.id.txtHeaderTitle)
        txtHeader?.text = nuevoTitulo
    }



}