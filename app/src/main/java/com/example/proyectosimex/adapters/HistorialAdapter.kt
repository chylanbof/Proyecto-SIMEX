package com.example.proyectosimex.adapters

import Envio
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.NombrePedido
import com.example.proyectosimex.R
import com.example.proyectosimex.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistorialAdapter(
    private var lista: List<Envio>,
    private val onVerDetalle: (Envio) -> Unit
) : RecyclerView.Adapter<HistorialAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombrePedido)
        val tvOrigen: TextView = view.findViewById(R.id.tvOrigen)
        val tvDestino: TextView = view.findViewById(R.id.tvDestino)
        val btnOpciones: ImageButton = view.findViewById(R.id.btnOpciones)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false))

    override fun getItemCount() = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val envio = lista[position]

        holder.tvNombre.text = "Envío #${envio.id}"
        holder.tvOrigen.text = "Origen: ${envio.origen}"
        holder.tvDestino.text = "Destino: ${envio.destino}"

        holder.itemView.setOnClickListener { onVerDetalle(envio) }

        holder.btnOpciones.setOnClickListener { anchor ->
            val popup = PopupMenu(anchor.context, anchor)
            popup.menu.add("Descargar")
            popup.menu.add("Eliminar")
            popup.setOnMenuItemClickListener { item ->
                when (item.title) {
                    "Descargar" -> {
                        val intent = Intent(anchor.context, NombrePedido::class.java).apply {
                            putExtra("numeroOferta", "Envío #${envio.id}")
                            putExtra("nombrePedido", "Envío #${envio.id}")
                            putExtra("cliente", envio.cliente)
                            putExtra("ruta", envio.ruta)
                            putExtra("modo", envio.metodoTransporte)
                            putExtra("peso", envio.pesoKg.toString())
                            putExtra("incoterm", envio.incoterm)
                            putExtra("urgencia", envio.urgencia)
                        }
                        anchor.context.startActivity(intent)
                        true
                    }
                    "Eliminar" -> {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                val response = RetrofitClient.api.deleteEnvio(envio.id)
                                android.util.Log.d("ELIMINAR", "Código: ${response.code()}")
                                android.util.Log.d("ELIMINAR", "Error: ${response.errorBody()?.string()}")
                                withContext(Dispatchers.Main) {
                                    if (response.isSuccessful) {
                                        lista = lista.filter { it.id != envio.id }
                                        notifyDataSetChanged()
                                        android.widget.Toast.makeText(
                                            anchor.context, "Envío eliminado", android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        android.widget.Toast.makeText(
                                            anchor.context, "Error: ${response.code()}", android.widget.Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("ELIMINAR", "Excepción: ${e.message}", e)
                                withContext(Dispatchers.Main) {
                                    android.widget.Toast.makeText(
                                        anchor.context, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }
    }

    fun updateData(nuevaLista: List<Envio>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}