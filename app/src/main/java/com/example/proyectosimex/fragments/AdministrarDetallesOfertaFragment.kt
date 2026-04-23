package com.example.proyectosimex.fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.AgenteComercial
import com.example.proyectosimex.R
import com.example.proyectosimex.adapters.TrackingAdapter
import com.example.proyectosimex.api.RetrofitClient
import com.example.proyectosimex.clases.PasoSeguimiento
import kotlinx.coroutines.launch

class AdministrarDetallesOfertaFragment : Fragment(R.layout.fragment_ofertas_administrar_cambiar_estados) {

    private lateinit var adapter: TrackingAdapter
    private var idOferta: Int = 0
    private var pasoActual: List<PasoSeguimiento> = emptyList()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AgenteComercial)?.actualizarTitulosHeader("Gestión de Oferta")

        idOferta = arguments?.getInt("idOferta") ?: 0

        val rv = view.findViewById<RecyclerView>(R.id.rvTracking)
        rv.layoutManager = LinearLayoutManager(requireContext())
        adapter = TrackingAdapter(emptyList(), modoEdicion = true)
        rv.adapter = adapter

        cargarSeguimiento(view)

        view.findViewById<Button>(R.id.btnGuardarCambios).setOnClickListener {
            guardarCambios()
        }
    }

    private fun cargarSeguimiento(view: View) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.getSeguimiento(idOferta)
                if (response.isSuccessful) {
                    val seg = response.body()!!
                    pasoActual = seg.pasos

                    view.findViewById<TextView>(R.id.tvNumeroOferta).text =
                        "Oferta #${seg.ofertaId}"
                    view.findViewById<TextView>(R.id.tvIncoterms).text =
                        "Incoterm: ${seg.incotermNombre}"

                    adapter.updateData(seg.pasos)
                }
            } catch (e: Exception) {
                Log.e("ADMIN_DETALLE", "Error: ${e.message}")
                Toast.makeText(requireContext(), "Error al cargar", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCambios() {
        lifecycleScope.launch {
            try {
                val pasos = adapter.getPasosActuales()
                Log.d("GUARDAR", "idOferta: $idOferta, pasos: $pasos") // ← añade esto

                val response = RetrofitClient.api.guardarSeguimiento(idOferta, pasos)

                Log.d("GUARDAR", "Código respuesta: ${response.code()}") // ← y esto
                Log.d("GUARDAR", "Error body: ${response.errorBody()?.string()}") // ← y esto

                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Guardado correctamente", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Error al guardar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("GUARDAR", "Excepción: ${e.message}", e) // ← y esto
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}