package com.example.proyectosimex.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.proyectosimex.R
import android.view.View
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.OfertaUsuariosAdapter
import com.example.proyectosimex.AgenteComercial
import com.example.proyectosimex.clases.Oferta
import com.example.proyectosimex.api.RetrofitClient
import kotlinx.coroutines.launch

//Apartado de agente comercial, cuando seleccione a un usuarios mostrara las ofertas
// que tiene ese usuarios, aqui el agente comercial podra darle click y nos enviara a
// detalleOfertaFragment, donde el agente comercial podra cambiar el estado de los pasos

// Finalizado
class AdministrarOfertasFragment : Fragment(R.layout.fragment_ofertas_para_administrar){

    private var clientId: Int = 0
    private lateinit var adapter: OfertaUsuariosAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AgenteComercial)?.configurarBotonAtras(true) {
            parentFragmentManager.beginTransaction()
                .replace(R.id.FragmentContainer, UsuariosFragment())
                .commit()
        }

        // Recuperar el ID del cliente enviado desde UsuariosFragment
        clientId = arguments?.getInt("clientId") ?: 0
        val nombreCli = arguments?.getString("nombreUsuario") ?: "Cliente"

        //Llamamos a la funcion para cambiar el nombre del header
        (activity as? AgenteComercial)?.actualizarTitulosHeader("Ofertas $nombreCli")

        //Configuramos el recycleView
        val rv = view.findViewById<RecyclerView>(R.id.rvOfertasUsuarios)
        rv.layoutManager = LinearLayoutManager(requireContext())

        adapter = OfertaUsuariosAdapter(emptyList(), mostrarEstado = true){oferta ->
            abrirDetalleSeguimiento(oferta)
        }

        rv.adapter = adapter

        //Cargar datos
        obtenerOfertasDelCliente()


    }

    private fun obtenerOfertasDelCliente(){
        lifecycleScope.launch {
            try {
                // Cargamos catálogos para traducir nombres
                val incos = RetrofitClient.api.getIncoterms().associate { it.id to (it.nom ?: "N/A") }
                val puertos = RetrofitClient.api.getPorts().associate { it.id to (it.nom ?: "Puerto") }
                val aeros = RetrofitClient.api.getAeroports().associate { it.id to (it.nom ?: "Aero") }

                // Llamada a la API
                val response = RetrofitClient.api.getOfertasByCliente(clientId)

                if (response.isSuccessful) {
                    val lista = response.body() ?: emptyList()
                    // Aquí el Agente ve TODO (pendientes, aceptadas, etc.)
                    adapter.updateData(lista, incos, puertos, aeros)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirDetalleSeguimiento(oferta: Oferta){
        //Fragment donde el agente cambiara los estados del envio
        val fragmentDetalle = AdministrarDetallesOfertaFragment()
        val bundle = Bundle()
        bundle.putInt("idOferta", oferta.id)
        fragmentDetalle.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.FragmentContainer, fragmentDetalle)
            .addToBackStack(null)
            .commit()

    }
}