package com.example.proyectosimex.Fragments

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyectosimex.clases.Oferta
import com.example.proyectosimex.R
import com.example.proyectosimex.api.RetrofitClient
import kotlinx.coroutines.launch

// Fragment para el control de las ofertas que aceptara o rechazara el usuario


//Finalizado
class OfertaDetalladaUsuarioFragment : Fragment(R.layout.fragment_oferta_detallada_para_usuario) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val idOferta = arguments?.getInt("idOferta") ?: 0

        // Cargamos todos los datos
        cargarDatosOferta(view, idOferta)

        // Configuramos los botones
        setupBotones(view, idOferta)

    }

    private fun cargarDatosOferta(view: View, idOferta: Int) {
        lifecycleScope.launch {
            try {
                // Llamadas en paralelo para los catálogos
                val incos = RetrofitClient.api.getIncoterms().associate { it.id to it.nom }
                val puertos = RetrofitClient.api.getPorts().associate { it.id to it.nom }
                val aeros = RetrofitClient.api.getAeroports().associate { it.id to it.nom }
                val transportistas = RetrofitClient.api.getTransportistes().associate { it.id to it.nom }
                val contenedores = RetrofitClient.api.getTipusContenidors().associate { it.id to it.nom }

                // Llamada a la oferta
                val response = RetrofitClient.api.getOfertas(idOferta)

                if (response.isSuccessful) {
                    response.body()?.let { oferta ->
                        rellenarInterfaz(view, oferta, incos, puertos, aeros, transportistas, contenedores)
                    }
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Error: ${e.message}")
                Toast.makeText(requireContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun rellenarInterfaz(
        v: View,
        o: Oferta,
        incos: Map<Int, String?>,
        puertos: Map<Int, String?>,
        aeros: Map<Int, String?>,
        transp: Map<Int, String?>,
        cont: Map<Int, String?>
    ) {
        val origen = if (o.portOrigenId != null) puertos[o.portOrigenId] else aeros[o.aeroportOrigenId]
        val destino = if (o.portDestiId != null) puertos[o.portDestiId] else aeros[o.aeroportDestiId]

        v.findViewById<TextView>(R.id.tvTituloDetalle).text = "Oferta #${o.id}"
        v.findViewById<TextView>(R.id.tvDetalleOrigen).text = "Origen: ${origen ?: "No definido"}"
        v.findViewById<TextView>(R.id.tvDetalleDestino).text = "Destino: ${destino ?: "No definido"}"
        v.findViewById<TextView>(R.id.tvDetalleIncoterm).text = "Incoterm: ${incos[o.incotermId] ?: o.incotermId}"

        v.findViewById<TextView>(R.id.tvDetallePeso).text = "Peso: ${o.pesBrut} kg"
        v.findViewById<TextView>(R.id.tvDetalleVolumen).text = "Volumen: ${o.volum} m³"

        v.findViewById<TextView>(R.id.tvDetalleTransportista).text = "Transportista: ${transp[o.transportistaId] ?: "Estándar"}"
        v.findViewById<TextView>(R.id.tvDetalleContenedor).text = "Contenedor: ${cont[o.tipusContenidorId] ?: "N/A"}"

        v.findViewById<TextView>(R.id.tvNotasComercial).text = o.comentaris ?: "Sin comentarios."
    }

    private fun setupBotones(view: View, idOferta: Int) {
        view.findViewById<Button>(R.id.btnAceptarOferta).setOnClickListener {
            actualizarEstadoEnServidor(idOferta, nuevoEstado = 2) // 2 = Validada
        }

        view.findViewById<Button>(R.id.btnRechazarOferta).setOnClickListener {
            mostrarDialogoRechazo(idOferta)
        }
    }

    private fun actualizarEstadoEnServidor(idOferta: Int, nuevoEstado: Int, motivo: String? = null) {
        lifecycleScope.launch {
            try {
                // Primero obtenemos la oferta actual para tener todos sus datos
                val responseGet = RetrofitClient.api.getOfertas(idOferta)

                if (responseGet.isSuccessful) {
                    val ofertaActual = responseGet.body()

                    if (ofertaActual != null) {

                        // Creamos una copia modificando solo el estado y el motivo de rechazo
                        val ofertaActualizada = ofertaActual.copy(
                            estatOfertaId = nuevoEstado,
                            raoRebuig = motivo
                        )

                        // Enviamos la actualización a la API
                        val responsePut = RetrofitClient.api.actualizarOferta(idOferta, ofertaActualizada)

                        if (responsePut.isSuccessful) {
                            val texto: String
                            if (nuevoEstado == 2){
                                texto = "Oferta Aceptada"
                            } else {
                                texto = "Oferta Rechazada"
                            }
                            Toast.makeText(requireContext(), texto, Toast.LENGTH_SHORT).show()

                            // Enviamos un refresh para que la lista de ofertas solo muestre los pendientes
                            val result = Bundle()
                            result.putBoolean("refresh", true)
                            parentFragmentManager.setFragmentResult("request_refresh", result)

                            // Volvemos atrás a la lista
                            parentFragmentManager.popBackStack()
                        } else {
                            Toast.makeText(requireContext(), "Error al actualizar estado", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun mostrarDialogoRechazo(idOferta: Int){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Rechazar Oferta #$idOferta")
        builder.setMessage("Por favos, especifica el motivo del rechazo: ")

        //Creamos un editText dinamico para que el usuario escriba
        val input = EditText(requireContext())
        input.hint = "Ej: El precio es demasiado alto..."

        //añadimos margen al editText para que no haya problema con los bordes
        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        input.layoutParams = lp

        val container = LinearLayout(requireContext())
        container.setPadding(50, 20, 50, 0) // Margen izquierdo, arriba, derecho, abajo
        container.addView(input)

        builder.setView(container)

        // Configuración de los botones del diálogo
        builder.setPositiveButton("Enviar") { _, _ ->
            val motivo = input.text.toString()
            if (motivo.isNotEmpty()) {
                // Llamamos a la función con estado 3 (Rechazada)
                actualizarEstadoEnServidor(idOferta, nuevoEstado = 3, motivo = motivo)
            } else {
                Toast.makeText(requireContext(), "Debes indicar un motivo", Toast.LENGTH_SHORT).show()
            }
        }

        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.cancel()
        }

        builder.show()

    }
}