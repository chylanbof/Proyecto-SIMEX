package com.example.proyectosimex.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.proyectosimex.R
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import com.example.proyectosimex.AgenteComercial


// Fragment que se encarga de cargar los datos para crear nueva oferta
//va ligada a la tabla ofertas, Apartado del agente comercial.
class CrearOfertasFragment : Fragment(R.layout.fragment_crear_ofertas){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //Llamamos a la funcion del activity para cambiar el header
        val nombre = arguments?.getString("nombreUsuario")?:""
        (activity as? AgenteComercial)?.actualizarTitulosHeader("Crear Oferta ${nombre}")

        // configuramos los spinners con datos de ejemplo
        val incoterms = arrayOf("EXW - Ex Works", "FOB - Free On Board", "CIF - Cost, Insurance and Freight", "DDP - Delivered Duty Paid")
        val transportes = arrayOf("Marítimo", "Aéreo", "Terrestre")
        val divisas = arrayOf("EUR (€)", "USD ($)", "GBP (£)")

        configurarSpinner(view.findViewById(R.id.spinnerIncoterm), incoterms)
        configurarSpinner(view.findViewById(R.id.spinnerTransporte),transportes)
        configurarSpinner(view.findViewById(R.id.spinnerDivisa),divisas)

        //Boton cancelar volvemos atras
        view.findViewById<Button>(R.id.btnCancelar).setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        //Boton crear
        view.findViewById<Button>(R.id.btnCrear).setOnClickListener {
            Toast.makeText(requireContext(),"Oferta creada", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack() // boton volver a la lista despues de crear
        }




    }

    private fun configurarSpinner(spinner: Spinner, datos: Array<String>){
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, datos)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }
}