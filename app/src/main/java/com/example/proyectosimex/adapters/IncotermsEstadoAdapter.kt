package com.example.proyectosimex.adapters

import android.graphics.Color
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.PasoSeguimientoItem
import com.example.proyectosimex.R

class IncotermEstadoAdapter(
    private var pasos: List<PasoSeguimientoItem>,
    private var esEditable: Boolean = true
) : RecyclerView.Adapter<IncotermEstadoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombrePaso: TextView = view.findViewById(R.id.tvNombrePaso)
        val spinnerEstado: Spinner = view.findViewById(R.id.spinnerEstadoPaso)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_incoterms, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paso = pasos[position]
        holder.tvNombrePaso.text = paso.nombrePaso

        // Configurar Opciones del Spinner (Basado en tu tabla estats_envio)
        val opciones = listOf("Preparación", "Envío", "Finalizado")
        val adapter = ArrayAdapter(holder.itemView.context, android.R.layout.simple_spinner_item, opciones)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        holder.spinnerEstado.adapter = adapter

        // Seteamos la posición actual (restando 1 porque en BD los IDs empiezan en 1)
        val posicionInicial = (paso.estadoActualId ?: 1) - 1
        holder.spinnerEstado.setSelection(if (posicionInicial >= 0) posicionInicial else 0)

        // --- LÓGICA DE BLOQUEO ---
        holder.spinnerEstado.isEnabled = esEditable
        if (!esEditable) {
            holder.tvNombrePaso.setTextColor(Color.GRAY)
        } else {
            holder.tvNombrePaso.setTextColor(Color.BLACK)
        }

        // Escuchamos el cambio para actualizar la lista en tiempo real
        holder.spinnerEstado.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, pos: Int, id: Long) {
                paso.estadoActualId = pos + 1
            }
            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    override fun getItemCount() = pasos.size

    fun getListaActualizada(): List<PasoSeguimientoItem> = pasos

    fun updateData(nuevosPasos: List<PasoSeguimientoItem>) {
        this.pasos = nuevosPasos
        notifyDataSetChanged()
    }
}