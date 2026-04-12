package com.example.proyectosimex.Fragments

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.proyectosimex.R

// Fragment para el control de las ofertas que aceptara o rechazara el usuario
class OfertaDetalladaUsuarioFragment : Fragment(R.layout.fragment_oferta_detallada_para_usuario) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aquí recuperaríamos los datos del Bundle (ID de la oferta)
        val idOferta = arguments?.getInt("idOferta") ?: 0

        val btnAceptar = view.findViewById<Button>(R.id.btnAceptarOferta)
        val btnRechazar = view.findViewById<Button>(R.id.btnRechazarOferta)

        btnAceptar.setOnClickListener {
            // Lógica para actualizar en la API: estado = "Aceptada"
            Toast.makeText(requireContext(), "Oferta #$idOferta Aceptada", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        btnRechazar.setOnClickListener {
            // Lógica para actualizar en la API: estado = "Rechazada"
            mostrarDialogoRechazo(idOferta)
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
                // -----
                // AQUÍ enviarás a la API: idOferta, estado="Rechazado" y el motivo
                //--
                Toast.makeText(requireContext(), "Enviando rechazo: $motivo", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
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