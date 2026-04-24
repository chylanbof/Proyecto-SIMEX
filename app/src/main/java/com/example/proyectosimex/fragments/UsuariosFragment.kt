package com.example.proyectosimex.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.adapters.UsuarioAdapter
import com.example.proyectosimex.AgenteComercial
import com.example.proyectosimex.R
import com.example.proyectosimex.api.RetrofitClient
import kotlinx.coroutines.launch
import com.example.proyectosimex.clases.Usuario

// ligado al agente comercial, carga los usuarios de la API y el agente comercial podra
// elegir si quiere crear una nueva oferta para ese usuario o administrar una oferta ya creada


// Finalizado.
class UsuariosFragment : Fragment(R.layout.fragment_usuarios) {
    private lateinit var adapter: UsuarioAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AgenteComercial)?.actualizarTitulosHeader("Agente Comercial")

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
                val listaUsuarios = RetrofitClient.api.obtenerUsuariosRol3()

                if (listaUsuarios.isNotEmpty()){
                    adapter.updateData(listaUsuarios)
                }else{
                    Toast.makeText(requireContext(), "No se encontraron usuarios", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception){
                Log.e("API_ERROR", "Mensaje: ${e.message}")
            }

            /*// solo puesta para hacer pruebas borrar despues
            // FORZAMOS una lista de prueba manual
            val listaFake = listOf(
                Usuario(id = 1, nom = "Cliente de Prueba", empresa = "Empresa Test", rol = "Cliente"),
                Usuario(id = 2, nom = "Juan Pérez", empresa = "Logística S.A.", rol = "Cliente")
            )

            // Se la pasamos al adapter directamente
            adapter.updateData(listaFake)*/
        }
    }

    // Función para crear el PopUp y elegir a que fragmento va a ir
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

    //Abre el fragmen de ofertas del usuario
    private fun abrirFragmentOfertas(usuario: Usuario) {

        Log.d("DEBUG_SIMEX", "Click en usuario: ${usuario.nom} con ID original: ${usuario.id}")
        val fragmentOfertas = CrearOfertasFragment()

        // PASAR DATOS
        val bundle = Bundle()
        bundle.putString("nombreUsuario", usuario.nom)
        bundle.putInt("clientId", usuario.id ?: 0)
        fragmentOfertas.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, fragmentOfertas) // Usando tu ID real
            .addToBackStack(null)
            .commit()

    }
    // abre el fragmen donde se administra el usuario
    private fun abrirAdministrarOfertas(usuario: Usuario){
        val fragmentAdministrar = AdministrarOfertasFragment()
        val bundle = Bundle()
        bundle.putInt("clientId", usuario.id ?: 0) // Pasamos el ID del usuario seleccionado
        bundle.putString("nombreUsuario", usuario.nom)
        fragmentAdministrar.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, fragmentAdministrar)
            .addToBackStack(null)
            .commit()
    }

}