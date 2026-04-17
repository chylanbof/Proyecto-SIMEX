package com.example.proyectosimex.Fragments

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.AgenteComercial
import com.example.proyectosimex.R
import com.example.proyectosimex.adapters.IncotermEstadoAdapter
import com.example.proyectosimex.api.RetrofitClient
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.collections.emptyMap

// Muestra la oferta del cliente y el agente comercial puede administrarla,
// en ese fragment cambiaremos el estado de los pasos segun el incoterm que tenga la oferta
// y luego lo guardaremos en la base de datos.

// siguiente a trabajar!!!!
// Finalizar

class AdministrarDetallesOfertaFragment : Fragment(R.layout.fragment_ofertas_administrar_cambiar_estados) {

    private var idOferta: Int = 0
    private lateinit var incotermAdaptar: IncotermEstadoAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as? AgenteComercial)?.configurarBotonAtras(false)

        idOferta = arguments?.getInt("idOferta") ?: 0
        (activity as? AgenteComercial)?.actualizarTitulosHeader("Gestión de Oferta")

        // 1. INICIALIZAR EL ADAPTER ANTES DE ASIGNARLO
        incotermAdaptar = IncotermEstadoAdapter(emptyList(), true)

        val rv = view.findViewById<RecyclerView>(R.id.rvIncotermsEstados)
        rv.layoutManager = LinearLayoutManager(requireContext())
        rv.adapter = incotermAdaptar

        cargarDatosSeguimiento(view)

        view.findViewById<Button>(R.id.btnGuardarEstado).setOnClickListener {
            guardarCambios()
        }

        view.findViewById<Button>(R.id.btnCancelarEstado).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun cargarDatosSeguimiento(view: View) {
        lifecycleScope.launch {
            try {
                val puertosDef = async { RetrofitClient.api.getPorts() }
                val aeropuertosDef = async { RetrofitClient.api.getAeroports() }
                val transportistasDef = async { RetrofitClient.api.getTransportistes() }

                val puertos = puertosDef.await().associate { it.id to it.nom }
                val aeropuertos = aeropuertosDef.await().associate { it.id to it.nom }
                val transportistas = transportistasDef.await().associate { it.id to it.nom }

                val resOferta = RetrofitClient.api.getOfertas(idOferta)

                if (resOferta.isSuccessful) {
                    val o = resOferta.body()
                    o?.let { oferta ->
                        val estaAceptada = oferta.estatOfertaId == 2

                        val btnGuardar = view.findViewById<Button>(R.id.btnGuardarEstado)
                        val btnCancelar = view.findViewById<Button>(R.id.btnCancelarEstado)
                        val tvTituloSeguimiento = view.findViewById<TextView>(R.id.tvTituloDetalle)
                        val tvIncotermNombre = view.findViewById<TextView>(R.id.tvDetalleIncoterm)
                        val rvSeguimiento = view.findViewById<RecyclerView>(R.id.rvIncotermsEstados)

                        if (!estaAceptada) {
                            btnGuardar.visibility = View.GONE
                            btnCancelar.text = "Volver"

                            // OCULTAMOS la sección de hitos
                            tvTituloSeguimiento.visibility = View.GONE
                            tvIncotermNombre.visibility = View.GONE
                            rvSeguimiento.visibility = View.GONE

                            Toast.makeText(requireContext(), "Oferta no aceptada: Seguimiento no disponible", Toast.LENGTH_SHORT).show()
                        } else {
                            // ASEGURAMOS que se vean si está aceptada
                            tvTituloSeguimiento.visibility = View.VISIBLE
                            tvIncotermNombre.visibility = View.VISIBLE
                            rvSeguimiento.visibility = View.VISIBLE
                        }

                        // Pintar datos de la Card (Esto se ve siempre)
                        val origen = puertos[oferta.portOrigenId] ?: aeropuertos[oferta.aeroportOrigenId] ?: "No definido"
                        val destino = puertos[oferta.portDestiId] ?: aeropuertos[oferta.aeroportDestiId] ?: "No definido"

                        view.findViewById<TextView>(R.id.tvDetalleOrigen).text = "Origen: $origen"
                        view.findViewById<TextView>(R.id.tvDetalleDestino).text = "Destino: $destino"
                        view.findViewById<TextView>(R.id.tvDetallePeso).text = "Peso: ${oferta.pesBrut} kg"
                        view.findViewById<TextView>(R.id.tvDetalleVolumen).text = "Volumen: ${oferta.volum} m³"
                        view.findViewById<TextView>(R.id.tvDetalleTransportista).text = "Transportista: ${transportistas[oferta.transportistaId] ?: "Pendiente"}"
                        view.findViewById<TextView>(R.id.tvNotasComercial).text = oferta.comentaris ?: "Sin comentarios"

                        // SOLO cargamos el seguimiento si está aceptada
                        if (estaAceptada) {
                            val resSeguimiento = RetrofitClient.api.getSeguimientoOferta(idOferta)
                            if (resSeguimiento.isSuccessful) {
                                resSeguimiento.body()?.let { info ->
                                    tvTituloSeguimiento.text = "Oferta #${info.ofertaId}"
                                    tvIncotermNombre.text = "Incoterm: ${info.incotermNombre}"
                                    incotermAdaptar.updateData(info.pasos)
                                }
                            }
                        }
                    }
                }

            } catch (e: Exception) {
                android.util.Log.e("API_ERROR", "Error en cargarDatosSeguimiento: ${e.message}")
                Toast.makeText(requireContext(), "Error de conexión con el servidor", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarCambios() {
        // Asegúrate que el método en el adapter se llame así
        val pasosParaGuardar = incotermAdaptar.getListaActualizada()
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.api.guardarSeguimiento(idOferta, pasosParaGuardar)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Estados actualizados con éxito", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error al guardar", Toast.LENGTH_SHORT).show()
            }
        }
    }
}