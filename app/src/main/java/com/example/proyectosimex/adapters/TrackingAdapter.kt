package com.example.proyectosimex.adapters

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.R
import com.example.proyectosimex.clases.PasoSeguimiento
import com.google.android.material.button.MaterialButton

class TrackingAdapter(
    private var pasos: List<PasoSeguimiento>,
    private val modoEdicion: Boolean = false  // false = solo vista, true = agente puede editar
) : RecyclerView.Adapter<TrackingAdapter.ViewHolder>() {

    // Copia mutable para editar estados
    private var pasosEditables: MutableList<PasoSeguimiento> = pasos.toMutableList()

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivIcono: ImageView = view.findViewById(R.id.ivEstadoIcono)
        val tvEstado: TextView = view.findViewById(R.id.tvEstadoPaso)
        val tvNombre: TextView = view.findViewById(R.id.tvNombrePaso)
        val lineaVertical: View = view.findViewById(R.id.lineaVertical)
        val layoutBotones: LinearLayout = view.findViewById(R.id.layoutBotonesEstado)
        val btnPendiente: MaterialButton = view.findViewById(R.id.btnPendiente)
        val btnEnProceso: MaterialButton = view.findViewById(R.id.btnEnProceso)
        val btnFinalizado: MaterialButton = view.findViewById(R.id.btnFinalizado)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tracking_paso, parent, false))

    override fun getItemCount() = pasosEditables.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val paso = pasosEditables[position]

        holder.tvNombre.text = paso.nombrePaso

        aplicarEstado(holder, paso.estadoActualId)

        // Mostrar botones solo en modo edición
        holder.layoutBotones.visibility = if (modoEdicion) View.VISIBLE else View.GONE

        if (modoEdicion) {
            holder.btnPendiente.setOnClickListener {
                actualizarEstado(position, 1)
            }
            holder.btnEnProceso.setOnClickListener {
                actualizarEstado(position, 2)
            }
            holder.btnFinalizado.setOnClickListener {
                actualizarEstado(position, 3)
            }
        }

        holder.lineaVertical.visibility =
            if (position == pasosEditables.size - 1) View.INVISIBLE else View.VISIBLE
    }

    private fun actualizarEstado(position: Int, nuevoEstado: Int) {
        val paso = pasosEditables[position]
        pasosEditables[position] = paso.copy(estadoActualId = nuevoEstado)
        notifyItemChanged(position)
    }

    private fun aplicarEstado(holder: ViewHolder, estadoId: Int) {
        when (estadoId) {
            3 -> {
                holder.tvEstado.text = "Finalizado"
                holder.tvEstado.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#4CAF50"))
                holder.ivIcono.setImageResource(R.drawable.ic_check_circle)
            }
            2 -> {
                holder.tvEstado.text = "En proceso"
                holder.tvEstado.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#FF6D00"))
                holder.ivIcono.setImageResource(R.drawable.ic_in_progress)
            }
            else -> {
                holder.tvEstado.text = "Pendiente"
                holder.tvEstado.backgroundTintList =
                    ColorStateList.valueOf(Color.parseColor("#BDBDBD"))
                holder.ivIcono.setImageResource(R.drawable.ic_pending)
            }
        }
    }

    fun updateData(nuevosPasos: List<PasoSeguimiento>) {
        pasos = nuevosPasos
        pasosEditables = nuevosPasos.toMutableList()
        notifyDataSetChanged()
    }

    // Para obtener los pasos modificados al guardar
    fun getPasosActuales(): List<PasoSeguimiento> = pasosEditables.toList()
}