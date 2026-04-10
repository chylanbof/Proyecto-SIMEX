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
import com.example.proyectosimex.Clases.Usuario

class UsuariosFragment : Fragment(R.layout.fragment_usuarios) {
    private lateinit var adapter: UsuarioAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Configuracion de recycle view con el id rvUsuarios del xml
        val rv = view.findViewById<RecyclerView>(R.id.rvUsuarios)
        rv.layoutManager = LinearLayoutManager(requireContext())

        //Inicializa con una lista vacia
        adapter = UsuarioAdapter(emptyList()) { usuario ->
            // Buscamos la posición del usuario en la lista
            val posicion = adapter.listaUsuario.indexOf(usuario)

            // Obtenemos la View de esa tarjeta específica a través del RecyclerView
            val viewTarjeta = rv.findViewHolderForAdapterPosition(posicion)?.itemView ?: rv

            mostrarPopUp(viewTarjeta, usuario)
        }

        rv.adapter = adapter

        //Llamar a la api de .NET
        obtenerDatos()
    }

    private fun obtenerDatos(){
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val listaUsuarios = RetrofitClient.instancia.obtenerUsuariosRol3()

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

    // Función para crear el PopUp
    private fun mostrarPopUp(view: View, usuario: Usuario) {
        val popup = android.widget.PopupMenu(requireContext(), view)
        popup.menu.add("Crear oferta para ${usuario.nom}")
        popup.menu.add("Administrar ofertas")

        popup.setOnMenuItemClickListener { item ->
            // Guardamos el resultado de la lógica en una variable
            val gestionado = when (item.title) {
                "Crear oferta para ${usuario.nom}" -> {
                    abrirFragmentOfertas(usuario)
                    true
                }
                "Administrar ofertas" -> {
                    abrirAdministrarOfertas(usuario)
                    true
                }
                else -> false
            }
            gestionado
        }
        popup.show()
    }

    private fun abrirFragmentOfertas(usuario: Usuario) {
        val fragmentOfertas = CrearOfertasFragment()

        // PASAR DATOS
        val bundle = Bundle()
        bundle.putString("nombreUsuario", usuario.nom)
        bundle.putInt("idUsuario", usuario.id ?: 0)
        fragmentOfertas.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, fragmentOfertas) // Usando tu ID real
            .addToBackStack(null)
            .commit()

    }

    private fun abrirAdministrarOfertas(usuario: Usuario){
        val fragmemtAdministrarOfertas = AdministrarOfertasFragment()

        parentFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, fragmemtAdministrarOfertas)
            .addToBackStack(null)
            .commit()
    }

}