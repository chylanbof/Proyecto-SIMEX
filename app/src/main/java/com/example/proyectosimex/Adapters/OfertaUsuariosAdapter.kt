package com.example.proyectosimex.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.Clases.Oferta
import com.example.proyectosimex.R

// Adpatar que se encarga de inflar las ofertas creadas por el Agente comercial para que
// el usuario las acepte o las rechace
// Finalizado
// se vuelve a usar este adapter en administrarOfertasFragmente para ahorrar codigo
class OfertaUsuariosAdapter(private var lista: List<Oferta>,
                            private var mapaIncoterms: Map<Int, String> = emptyMap(),
                            private var mapaPuertos: Map<Int, String> = emptyMap(),
                            private var mapaAeros: Map<Int, String> = emptyMap(),
                            private val mostrarEstado: Boolean = false,
                            private val clickListener: (Oferta) -> Unit) :
    RecyclerView.Adapter<OfertaUsuariosAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.tvIdOfertaUsuario)
        val ruta: TextView = view.findViewById(R.id.tvRutaUsuario)
        val incoterm: TextView = view.findViewById(R.id.tvIncotermUsuario)

        val estado: TextView = view.findViewById(R.id.tvEstadoOferta)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ofertas_usuarios, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val oferta = lista[position]


        // --- LÓGICA DE VISIBILIDAD ---
        if (mostrarEstado) {
            holder.estado.visibility = View.VISIBLE

            // AWhen para asingar los colores a los estados
            when (oferta.estatOfertaId) {
                1 -> { holder.estado.text = "PENDIENTE"; holder.estado.setBackgroundColor(android.graphics.Color.parseColor("#FFA500")) }
                2 -> { holder.estado.text = "ACEPTADA"; holder.estado.setBackgroundColor(android.graphics.Color.parseColor("#4CAF50")) }
                3 -> { holder.estado.text = "RECHAZADA"; holder.estado.setBackgroundColor(android.graphics.Color.parseColor("#F44336")) }
            }
        } else {
            holder.estado.visibility = View.GONE // <--- SE OCULTA TOTALMENTE
        }


        holder.id.text = "Oferta #${oferta.id}"

        // Traducimos Origen/Destino usando los mapas
        val origen = if (oferta.portOrigenId != null) {
            mapaPuertos[oferta.portOrigenId] ?: "Puerto ${oferta.portOrigenId}"
        } else {
            mapaAeros[oferta.aeroportOrigenId] ?: "Aeropuerto ${oferta.aeroportOrigenId}"
        }

        val destino = if (oferta.portDestiId != null) {
            mapaPuertos[oferta.portDestiId] ?: "Puerto ${oferta.portDestiId}"
        } else {
            mapaAeros[oferta.aeroportDestiId] ?: "Aeropuerto ${oferta.aeroportDestiId}"
        }

        holder.ruta.text = "$origen -> $destino"

        // Traducimos Incoterm (EXW, FTA, etc)
        val nombreInco = mapaIncoterms[oferta.incotermId] ?: "ID: ${oferta.incotermId}"
        holder.incoterm.text = "Incoterm: $nombreInco"

        holder.itemView.setOnClickListener { clickListener(oferta) }
    }

    // Función para actualizar todo junto
    fun updateData(nuevaLista: List<Oferta>, incos: Map<Int, String>, puertos: Map<Int, String>, aeros: Map<Int, String>) {
        lista = nuevaLista
        mapaIncoterms = incos
        mapaPuertos = puertos
        mapaAeros = aeros
        notifyDataSetChanged()
    }

    override fun getItemCount() = lista.size
}