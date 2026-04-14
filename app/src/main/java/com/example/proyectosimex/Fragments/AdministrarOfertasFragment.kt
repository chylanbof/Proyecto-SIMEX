package com.example.proyectosimex.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.proyectosimex.R
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.Adapters.OfertaAdapter
import com.example.proyectosimex.AgenteComercial
import com.example.proyectosimex.Clases.Oferta

//Apartado de agente comercial, cuando seleccione a un usuarios mostrara las ofertas
// que tiene ese usuarios, aqui el agente comercial podra darle click y nos enviara a
// detalleOfertaFragment, donde el agente comercial podra cambiar el estado de los pasos
//
class AdministrarOfertasFragment : Fragment(R.layout.fragment_administrar_ofertas){

    private lateinit var adapter: OfertaAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Llamamos a la funcion para cambiar el nombre del header
        (activity as? AgenteComercial)?.actualizarTitulosHeader("Administrar Ofertas")

        val rv = view.findViewById<RecyclerView>(R.id.rvOfertasAdministrar)
        rv.layoutManager = LinearLayoutManager(requireContext())

       /* //solo datos de prueba para poder ver el front como quedara
        val listaFake = listOf(
            Oferta(101,"Barcelona, ES", "Marseille,FR"),
            Oferta(102,"Valencia, ES", "Genova,IT"),
            Oferta(103,"Bilbao, ES", "Southampton,UK")
        )

        adapter = OfertaAdapter(listaFake){ oferta ->
            val fragment = DetalleOfertaFragment()
            val bundle = Bundle()
            bundle.putInt("idOferta", oferta.id)
            fragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.FragmentContainer,fragment)
                .addToBackStack(null)
                .commit()

        }
        rv.adapter = adapter*/

    }
}