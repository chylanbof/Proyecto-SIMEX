package com.example.proyectosimex.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.Clases.Oferta
import com.example.proyectosimex.R

// Adpatar que se encarga de ver las ofertas creadas por el Agente comercial par aque
// el usuario las acepte o las rechace
class OfertaUsuariosAdapter(private var lista: List<Oferta>, private val clickListener: (Oferta) -> Unit) :
    RecyclerView.Adapter<OfertaUsuariosAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val id: TextView = view.findViewById(R.id.tvIdOfertaUsuario)
        val ruta: TextView = view.findViewById(R.id.tvRutaUsuario)
        val incoterm: TextView = view.findViewById(R.id.tvIncotermUsuario)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ofertas_usuarios, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val oferta = lista[position]
        holder.id.text = "Oferta #${oferta.id}"
        holder.ruta.text = "${oferta.origen} -> ${oferta.destino}"
        holder.incoterm.text = "Incoterm: No asignado"

        holder.itemView.setOnClickListener { clickListener(oferta) }
    }

    override fun getItemCount() = lista.size

    fun updateData(nuevaLista: List<Oferta>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}