package com.example.proyectosimex.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.proyectosimex.R
import android.view.View
import android.widget.TextView

class CrearOfertasFragment : Fragment(R.layout.fragment_crear_ofertas){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Recuperar los datos del "Bundle" (lo que enviamos desde UsuariosFragment)
        val nombreCliente = arguments?.getString("nombreUsuario") ?: "Cliente"
        val idCliente = arguments?.getInt("idUsuario") ?: 0

        //Referenciar algún TextView de tu layout para personalizar la vista
        val tvTitulo = view.findViewById<TextView>(R.id.tvTituloOferta)
        tvTitulo.text = "Nueva Oferta para: $nombreCliente"


    }
}