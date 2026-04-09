package com.example.proyectosimex.Fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.Adapters.UsuarioAdapter
import com.example.proyectosimex.R
import com.example.proyectosimex.RetrofitClient
import kotlinx.coroutines.launch

class UsuariosFragment : Fragment(R.layout.fragment_usuarios) {
    private lateinit var adapter: UsuarioAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuracion de recycle view con el id rvUsuarios del xml
        val rv = view.findViewById<RecyclerView>(R.id.rvUsuarios)
        rv.layoutManager = LinearLayoutManager(requireContext())

        //Inicializa con una lista vacia
        adapter = UsuarioAdapter(emptyList())
        rv.adapter = adapter

        //Llamar a la api de .NET
        obtenerDatos()
    }

    private fun obtenerDatos(){
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val listaUsuarios = RetrofitClient.instancia.obtenerUsuarios()

                if (listaUsuarios.isNotEmpty()){
                    adapter.updateData(listaUsuarios)
                }else{
                    Toast.makeText(requireContext(), "No se encontraron usuarios", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception){
                Log.e("API_ERROR", "Mensaje: ${e.message}")
            }
        }
    }

}