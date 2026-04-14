package com.example.proyectosimex.Fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.Adapters.OfertaUsuariosAdapter
import com.example.proyectosimex.AgenteComercial
import com.example.proyectosimex.Clases.Oferta
import com.example.proyectosimex.OfertasUsuarios
import com.example.proyectosimex.R

// Fragment donde se muestran las ofertas que han sido creadas por el agente comercial
// y tiene que aceptar o no el usuario
class UsuariosOfertasFragment : Fragment(R.layout.fragment_ofertas_cliente) {

    private lateinit var adapter: OfertaUsuariosAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Llamamos a la funcion para cambiar el nombre del header
        (activity as? OfertasUsuarios)?.actualizarTitulosHeader("Ofertas del Cliente")

        val rv = view.findViewById<RecyclerView>(R.id.rvOfertasUsuarios)
        rv.layoutManager = LinearLayoutManager(requireContext())

        /*// Datos de prueba (Fake)
        val listaPrueba = listOf(
            Oferta(1, "Barcelona", "Shanghai", 35.0),
            Oferta(2, "Madrid", "New York", 35.2)
        )

        adapter = OfertaUsuariosAdapter(listaPrueba) { oferta ->
            abrirDetalleOferta(oferta)
        }
        rv.adapter = adapter*/
    }

    /*private fun abrirDetalleOferta(oferta: Oferta){
        val fragmentDetalle = OfertaDetalladaUsuarioFragment()

        // Pasamos el ID de la oferta mediante un Bundle
        val bundle = Bundle()
        bundle.putInt("idOferta", oferta.id)
        // Podrías pasar más datos si no quieres volver a llamar a la API
        bundle.putString("origen", oferta.origen)
        bundle.putString("destino", oferta.destino)

        fragmentDetalle.arguments = bundle

        // Realizamos la transacción al fragmento de detalle
        parentFragmentManager.beginTransaction()
            .replace(R.id.FramgmentContainerOfertasCliente, fragmentDetalle)
            .addToBackStack(null) // Para que el cliente pueda volver a la lista con el botón "Atrás"
            .commit()

    }*/
}