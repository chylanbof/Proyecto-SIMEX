package com.example.proyectosimex.adapters
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.clases.Usuario
import com.example.proyectosimex.R

class UsuarioAdapter( var listaUsuario: List<Usuario>,
                     private val onItemClick: (Usuario) -> Unit):
    RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>(){

    class UsuarioViewHolder(view: View): RecyclerView.ViewHolder(view){
        val tvNombre: TextView = view.findViewById(R.id.tvTituloResumen)
        val tvRol: TextView = view.findViewById(R.id.tvSubtituloResumen)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_usuarios, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = listaUsuario[position]

        // tenemos el nombre y los apellidos en minúsculas
        holder.tvNombre.text = "${usuario.nom ?: ""} ${usuario.cognoms ?: ""}"

        // Mostramos empresa y el texto del rol
        holder.tvRol.text = "Empresa: ${usuario.empresa ?: "N/A"} - ${usuario.rol ?: "Sin Rol"}"

        //detecta que se ha seleccionado un cliente
        holder.itemView.setOnClickListener {
            onItemClick(usuario)
        }
    }

    override fun getItemCount(): Int = listaUsuario.size

    fun updateData(nuevaLista: List<Usuario>){
        listaUsuario = nuevaLista
        notifyDataSetChanged()
    }

    }