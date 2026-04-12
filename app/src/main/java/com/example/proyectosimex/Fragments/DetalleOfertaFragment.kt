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
import com.example.proyectosimex.AgenteComercial
import com.example.proyectosimex.R

// Muestra la oferta del cliente y el agente comercial puede administrarla,
// en ese fragment cambiaremos el estado de los pasos segun el incoterm que tenga la oferta
// y luego lo guardaremos en la base de datos.
class DetalleOfertaFragment : Fragment(R.layout.fragment_detalle_oferta) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Cambiar Header
        (activity as? AgenteComercial)?.actualizarTitulosHeader("Gestión de Oferta")

        // Recuperar datos de la oferta seleccionada
        val idOferta = arguments?.getInt("idOferta") ?: 0
        val incotermSimulado = "EXW" // Esto vendrá de la API o del bundle más adelante

        // Pintar datos en la Card (Simulados de momento)
        view.findViewById<TextView>(R.id.tvNumeroOferta).text = "Oferta #$idOferta"
        view.findViewById<TextView>(R.id.tvIncoterms).text = "Incoterm: $incotermSimulado"

        // Generar Hitos Dinámicos
        val contenedorHitos = view.findViewById<LinearLayout>(R.id.contenedorHitos)
        generarHitosDinamicos(contenedorHitos, incotermSimulado)

        // Botón Guardar
        view.findViewById<Button>(R.id.btnGuardarCambios).setOnClickListener {
            Toast.makeText(requireContext(), "Seguimiento guardado para $incotermSimulado", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        //Boton Cancelar
        view.findViewById<Button>(R.id.btnCancelarDetalle).setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun generarHitosDinamicos(contenedor: LinearLayout, incoterm: String) {
        // Limpiamos por si acaso
        contenedor.removeAllViews()

        // Definimos los pasos según el Incoterm
        val pasos = when (incoterm) {
            "EXW" -> listOf("Disponibilidad en Fábrica", "Carga en Vehículo")
            "CIF" -> listOf("Salida Puerto Origen", "Tránsito Marítimo", "Llegada Puerto Destino", "Seguro Contratado")
            "DDP" -> listOf("Despacho Aduana Origen", "Transporte Principal", "Despacho Aduana Destino", "Entrega Final")
            else -> listOf("Estado General de la Oferta")
        }

        val estadosPosibles = arrayOf("Pendiente", "En curso", "Completado")

        // Creamos la interfaz para cada paso
        pasos.forEach { nombrePaso ->
            // Título del Hito
            val tvHito = TextView(requireContext()).apply {
                text = nombrePaso
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(Color.parseColor("#FD7121")) // Tu naranja corporativo
                setPadding(0, 32, 0, 8) // Margen superior para separar
            }

            // Spinner para el estado del Hito
            val spinnerHito = Spinner(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    130 // Altura en píxeles (puedes ajustarlo)
                )
                // Usamos el fondo estándar de dropdown
                background = ContextCompat.getDrawable(context, android.R.drawable.btn_dropdown)
            }

            // Llenar el spinner
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, estadosPosibles)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerHito.adapter = adapter

            // Añadir al contenedor del XML
            contenedor.addView(tvHito)
            contenedor.addView(spinnerHito)
        }
    }
}