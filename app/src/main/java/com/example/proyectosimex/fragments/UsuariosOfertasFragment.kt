package com.example.proyectosimex.Fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.Adapters.OfertaUsuariosAdapter
import com.example.proyectosimex.AgenteComercial
import com.example.proyectosimex.Clases.Oferta
import com.example.proyectosimex.OfertasUsuarios
import com.example.proyectosimex.R
import com.example.proyectosimex.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.Response
import kotlin.collections.emptyList

// Fragment donde se muestran las ofertas que han sido creadas por el agente comercial
// y tiene que aceptar o no el usuario


// Finalizado (Solo flata recibir el usuario del login)
class UsuariosOfertasFragment : Fragment(R.layout.fragment_ofertas_cliente) {

    private lateinit var adapter: OfertaUsuariosAdapter
    private var clientId: Int = 3

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // recuperamos el id del cliente que se ha logueado para poder mostrar sus ofertas
        /*val prefs = requireActivity().getSharedPreferences("MiApprefs", android.content.Context.MODE_PRIVATE)
        clientId = prefs.getInt("clientId", 0)*/

        //Llamamos a la funcion para cambiar el nombre del header
        (activity as? OfertasUsuarios)?.actualizarTitulosHeader("Ofertas del Cliente")

        val rv = view.findViewById<RecyclerView>(R.id.rvOfertasUsuarios)
        rv.layoutManager = LinearLayoutManager(requireContext())

        // inicializamos el adpater con una lista vacia
        adapter = OfertaUsuariosAdapter(emptyList(), mostrarEstado = false){oferta ->
            abrirDetalleOferta(oferta)
        }
        rv.adapter = adapter

        parentFragmentManager.setFragmentResultListener("request_refresh", viewLifecycleOwner) { _, bundle ->
            val debeRefrescar = bundle.getBoolean("refresh")
            if (debeRefrescar) {
                obtenerOfertasServidor() // Volvemos a llamar a la API
            }
        }

        obtenerOfertasServidor()
    }

    private fun obtenerOfertasServidor() {
        lifecycleScope.launch {
            try {
                // Cargamos datos
                val incos = RetrofitClient.instancia.getIncoterms().associate { it.id to it.nom!! }
                val puertos = RetrofitClient.instancia.getPorts().associate { it.id to it.nom!! }
                val aeros = RetrofitClient.instancia.getAeroports().associate { it.id to it.nom!! }


                // cargamos las ofertas
                val response = RetrofitClient.instancia.getOfertasByCliente(clientId)

                if (response.isSuccessful) {
                    val todasLasOfertas = response.body() ?: emptyList()

                    // FILTRAR: Solo nos quedamos con las que tienen estatOfertaId == 1 (Pendiente)
                    val soloPendientes = todasLasOfertas.filter { it.estatOfertaId == 1 }

                    // Actualizar el adapter solo con las pendientes
                    adapter.updateData(soloPendientes, incos, puertos, aeros)

                    // Opcional: Mostrar un mensaje si no hay nada pendiente
                    if (soloPendientes.isEmpty()) {
                        Toast.makeText(requireContext(), "No tienes ofertas pendientes", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                android.util.Log.e("API_ERROR", "Error: ${e.message}")
                Toast.makeText(requireContext(), "Fallo de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirDetalleOferta(oferta: Oferta){
        val fragmentDetalle = OfertaDetalladaUsuarioFragment()
        val bundle = Bundle()
        bundle.putInt("idOferta", oferta.id)
        //pasar todo el objeto si Oferta es Parcelable
        fragmentDetalle.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.FramgmentContainerOfertasCliente, fragmentDetalle)
            .addToBackStack(null)
            .commit()

    }
}