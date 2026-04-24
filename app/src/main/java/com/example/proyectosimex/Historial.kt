package com.example.proyectosimex

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.adapters.HistorialAdapter
import com.example.proyectosimex.adapters.ResumenAdapter
import com.example.proyectosimex.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Historial : AppCompatActivity() {

    private lateinit var adapter: HistorialAdapter
    private var clientId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial)

        clientId = intent.getIntExtra("usuario_id", -1)

        val header = findViewById<View>(R.id.layoutDeArriba)
        header.findViewById<ImageView>(R.id.imgBackgroundHeader).setImageResource(R.drawable.historial)
        header.findViewById<TextView>(R.id.txtHeaderTitle).text = "Historial"

        findViewById<ImageButton>(R.id.btnAtras).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvHistorial)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = HistorialAdapter(emptyList()) { envio ->

        }
        rv.adapter = adapter

        cargarHistorial()
    }

    private fun cargarHistorial() {
        Log.d("HISTORIAL", "Cargando historial para clientId: $clientId")
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getEnviosByCliente(clientId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val envios = response.body() ?: emptyList()

                        val finalizados = envios.filter { it.estadoEnvio == "Entregado hoy" }

                        findViewById<TextView>(R.id.tvSinPedidos).visibility =
                            if (finalizados.isEmpty()) View.VISIBLE else View.GONE

                        adapter.updateData(finalizados)
                    }
                }
            } catch (e: Exception) {
                Log.e("HISTORIAL", "Error: ${e.message}")
            }
        }
    }
}