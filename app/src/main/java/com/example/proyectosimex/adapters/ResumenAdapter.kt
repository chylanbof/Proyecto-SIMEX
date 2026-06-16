package com.example.proyectosimex.adapters

import Envio
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.R
import com.example.proyectosimex.clases.Oferta
import com.google.android.material.button.MaterialButton

class ResumenAdapter(
    private var lista: List<Envio>,
    private val onVerMas: (Envio) -> Unit,
    private val onTracking: (Envio) -> Unit
) : RecyclerView.Adapter<ResumenAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvIdPedido: TextView = view.findViewById(R.id.tvIdPedido)
        val tvTipoPedido: TextView = view.findViewById(R.id.tvTipoPedido)
        val tvEstado: TextView = view.findViewById(R.id.tvEstado)
        val tvMetodoEnvio: TextView = view.findViewById(R.id.tvMetodoEnvio)
        val btnVerMas: MaterialButton = view.findViewById(R.id.btnVerMas)
        val btnTracking: MaterialButton = view.findViewById(R.id.btnTracking)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_resumen, parent, false))

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val envio = lista[position]

        holder.tvIdPedido.text = "Envío #${envio.id}"
        holder.tvTipoPedido.text = "${envio.origen ?: "?"} → ${envio.destino ?: "?"}"
        holder.tvEstado.text = envio.estadoEnvio ?: "Sin estado"
        holder.tvMetodoEnvio.text = envio.metodoTransporte ?: "No especificado"

        holder.btnVerMas.setOnClickListener { onVerMas(envio) }
        holder.btnTracking.setOnClickListener { onTracking(envio) }
    }

    fun updateData(nuevaLista: List<Envio>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}