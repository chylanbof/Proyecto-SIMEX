
package com.example.proyectosimex.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import com.example.proyectosimex.R
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.proyectosimex.AgenteComercial
import com.example.proyectosimex.clases.ItemCatalogo
import com.example.proyectosimex.clases.Oferta
import com.example.proyectosimex.api.RetrofitClient
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// Fragment que se encarga de cargar los datos para crear nueva oferta
//va ligada a la tabla ofertas, Apartado del agente comercial


// Finalizado
class CrearOfertasFragment : Fragment(R.layout.fragment_crear_ofertas){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        val nombre = arguments?.getString("nombreUsuario") ?: ""
        val clientId = arguments?.getInt("clientId") ?: 0
        (activity as? AgenteComercial)?.actualizarTitulosHeader("Crear Oferta $nombre")


        Log.d("DEBUG_SIMEX", ">>> CLIENTE SELECCIONADO: $nombre con ID: $clientId")

        if (clientId == 0) {
            Log.e("DEBUG_SIMEX", "ERROR: El ID del cliente llegó como 0")
        }

        cargarCatalogos(view)
        configurarDivisas(view)

        // Configuración de botones
        view.findViewById<Button>(R.id.btnCancelar).setOnClickListener { parentFragmentManager.popBackStack() }

        view.findViewById<Button>(R.id.btnCrear).setOnClickListener {
            ejecutarProcesoGuardado(view, clientId)
        }

    }

    private fun configurarDivisas(v: View) {
        val divisas = arrayOf("EUR (€)", "USD ($)", "GBP (£)")
        val spDivisa = v.findViewById<Spinner>(R.id.spinnerDivisa)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, divisas)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spDivisa.adapter = adapter
    }

    private fun cargarCatalogos(v: View) {
        // Referencias a todos los spinners
        val spInco = v.findViewById<Spinner>(R.id.spinnerIncoterm)
        val spTrans = v.findViewById<Spinner>(R.id.spinnerTransporte)
        val spOri = v.findViewById<Spinner>(R.id.spinnerOrigen)
        val spDes = v.findViewById<Spinner>(R.id.spinnerDesti)
        val spTransportista = v.findViewById<Spinner>(R.id.spinnerTransportista)
        val spContenidor = v.findViewById<Spinner>(R.id.spinnerContenidor)
        val spEstado = v.findViewById<Spinner>(R.id.spinnerEstatOferta)

        lifecycleScope.launch {
            try {
                // 1. Carga de catálogos independientes
                val incoterms = RetrofitClient.api.getIncoterms()
                val transportes = RetrofitClient.api.getTipusTransports()
                val transportistas = RetrofitClient.api.getTransportistas()
                val contenedores = RetrofitClient.api.getTipusContenidors()
                val estadoOferta = RetrofitClient.api.getTipusValidacions()

                // 2. Asignar Adapters
                spInco.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, incoterms)
                spTrans.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, transportes)
                spTransportista.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, transportistas)
                spContenidor.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, contenedores)
                spEstado.adapter = ArrayAdapter(requireContext(),android.R.layout.simple_spinner_item, estadoOferta)

                // 3. Lógica dependiente (Puertos vs Aeropuertos)
                spTrans.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val seleccionado = parent?.getItemAtPosition(position) as? ItemCatalogo ?: return

                        lifecycleScope.launch {
                            try {
                                val nombreTrans = seleccionado.nom ?: ""
                                val listaRuta = if (nombreTrans.contains("Aéreo", ignoreCase = true)) {
                                    RetrofitClient.api.getAeroports()
                                } else {
                                    RetrofitClient.api.getPorts()
                                }

                                val routeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listaRuta)
                                spOri.adapter = routeAdapter
                                spDes.adapter = routeAdapter

                            } catch (e: Exception) {
                                Log.e("API_ERROR", "Error en sub-catálogo: ${e.message}")
                            }
                        }
                    }
                    override fun onNothingSelected(p0: AdapterView<*>?) {}
                }

            } catch (e: Exception) {
                Log.e("API_ERROR", "Error principal: ${e.message}")
                Toast.makeText(requireContext(), "Error al cargar datos", Toast.LENGTH_SHORT).show()
            }
        }
    }



    // --- LÓGICA DE GUARDADO ---

    private fun ejecutarProcesoGuardado(view: View, clientId: Int) {
        Log.d("DEBUG_ID", "ID del cliente recibido: $clientId")
        val nuevaOferta = recolectarDatosDeInterfaz(view, clientId)

        if (validarOferta(nuevaOferta)) {
            enviarOferta(nuevaOferta)
        }
    }

    private fun recolectarDatosDeInterfaz(v: View, clientId: Int): Oferta {
        // Formateo de Fechas
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val hoy = sdf.format(Calendar.getInstance().time)
        val cal = Calendar.getInstance()
        cal.add(Calendar.MONTH, 1)
        val mesQueVe = sdf.format(cal.time)

        // Capturar Selecciones de los Spinners
        val transporteSel = v.findViewById<Spinner>(R.id.spinnerTransporte).selectedItem as? ItemCatalogo
        val incotermSel = v.findViewById<Spinner>(R.id.spinnerIncoterm).selectedItem as? ItemCatalogo
        val transportistaSel = v.findViewById<Spinner>(R.id.spinnerTransportista).selectedItem as? ItemCatalogo
        val contenidorSel = v.findViewById<Spinner>(R.id.spinnerContenidor).selectedItem as? ItemCatalogo
        val estatOfertaSel = v.findViewById<Spinner>(R.id.spinnerEstatOferta).selectedItem as? ItemCatalogo
        val origenSel = v.findViewById<Spinner>(R.id.spinnerOrigen).selectedItem as? ItemCatalogo
        val destiSel = v.findViewById<Spinner>(R.id.spinnerDesti).selectedItem as? ItemCatalogo

        // Lógica de Puertos vs Aeropuertos (según el transporte seleccionado)
        val nombreTrans = transporteSel?.nom ?: ""
        var pOrigen: Int? = null
        var pDesti: Int? = null
        var aOrigen: Int? = null
        var aDesti: Int? = null

        if (nombreTrans.contains("Aéreo", ignoreCase = true)) {
            aOrigen = origenSel?.id
            aDesti = destiSel?.id
        } else {
            pOrigen = origenSel?.id
            pDesti = destiSel?.id
        }

        // Retornar el objeto con los datos reales de la UI
        return Oferta(
            id = 0,
            tipusTransportId = transporteSel?.id ?: 1,
            tipusFluxeId = 2,
            tipusCarregaId = 2,
            incotermId = incotermSel?.id ?: 1,
            clientId = clientId,
            agentComercialId = 4,
            operadorId = 1,
            pesBrut = v.findViewById<EditText>(R.id.etPesBrut).text.toString().toDoubleOrNull() ?: 0.0,
            volum = v.findViewById<EditText>(R.id.etVolum).text.toString().toDoubleOrNull() ?: 0.0,
            comentaris = v.findViewById<EditText>(R.id.etComentarios).text.toString(),

            // Asignamos los IDs reales seleccionados en los Spinners
            transportistaId = transportistaSel?.id ?: 1,
            tipusContenidorId = contenidorSel?.id ?: 1,

            // Asignación de Puertos/Aeropuertos
            portOrigenId = pOrigen,
            portDestiId = pDesti,
            aeroportOrigenId = aOrigen,
            aeroportDestiId = aDesti,

            // Fechas
            dataCreacio = hoy,
            dataValidessaInicial = hoy,
            dataValidessaFina = mesQueVe,

            // Estados
            estatOfertaId = estatOfertaSel?.id ?:1
                     )
    }

    private fun validarOferta(oferta: Oferta): Boolean {
        if (oferta.incotermId == 0 || oferta.tipusTransportId == 0) {
            Toast.makeText(requireContext(), "Incoterm y Transporte son obligatorios", Toast.LENGTH_SHORT).show()
        }
        if (oferta.pesBrut == null || oferta.volum == null) {
            Toast.makeText(requireContext(), "Por favor, introduce peso y volumen válidos", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun enviarOferta(oferta: Oferta){
        lifecycleScope.launch {
            try {
                val gson = com.google.gson.Gson()
                Log.d("DEBUG_JSON", gson.toJson(oferta))  // ← añade esta línea aquí

                val response = RetrofitClient.api.crearOferta(oferta)
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Oferta guardada correctamente", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Log.e("API_ERROR", "Error: ${response.code()} - ${response.errorBody()?.string()}")
                    Toast.makeText(requireContext(), "Error al guardar: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("API_ERROR", "Fallo de red: ${e.message}")
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

