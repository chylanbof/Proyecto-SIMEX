package com.example.proyectosimex


import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectosimex.Fragments.UsuariosOfertasFragment

class OfertasUsuarios : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ofertas_clientes)

        if (savedInstanceState == null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.FramgmentContainerOfertasCliente,
                UsuariosOfertasFragment())
                .commit()
        }

    }

    fun actualizarTitulosHeader(nuevoTitulo: String){
        //buscamos el textView dentro del layout incluido
        val txtHeader = findViewById<TextView>(R.id.txtHeaderTitle)
        txtHeader?.text = nuevoTitulo
    }

}