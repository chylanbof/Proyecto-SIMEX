package com.example.proyectosimex.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.Clases.Oferta
import com.example.proyectosimex.R

// adapter que se encargara de inflar el recycleview con las ofertas guadadas de la api
class OfertaAdapter(private var listaOfertas: List<Oferta>, private val onOfertaClick: (Oferta)-> Unit):
RecyclerView.Adapter<OfertaAdapter.OfertaViewHolder>(){
    class OfertaViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvId: TextView = view.findViewById(R.id.tvIdOferta)
        val tvOrigen: TextView = view.findViewById(R.id.tvOrigen)
        val tvDestino: TextView = view.findViewById(R.id.tvDestino)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OfertaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_ofertas, parent, false)
        return OfertaViewHolder(view)
    }

    override fun onBindViewHolder(holder: OfertaViewHolder, position: Int) {
        val oferta = listaOfertas[position]
        holder.tvId.text = "Oferta #${oferta.id}"
        holder.tvOrigen.text = "Origen: ${oferta.portOrigenId ?: "No definido"}"
        holder.tvDestino.text = "Destino: ${oferta.portDestiId ?: "No definido"}"
        holder.itemView.setOnClickListener { onOfertaClick(oferta) }
    }

    override fun getItemCount() = listaOfertas.size

    fun updateData(nuevaLista: List<Oferta>) {
        listaOfertas = nuevaLista
        notifyDataSetChanged()
    }
}