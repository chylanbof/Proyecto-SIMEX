package com.example.proyectosimex

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.adapters.TrackingAdapter
import com.example.proyectosimex.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Tracking : AppCompatActivity() {

    private lateinit var adapter: TrackingAdapter
    private var idOferta: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tracking)

        idOferta = intent.getIntExtra("idOferta", -1)
        Log.d("TRACKING", "idOferta recibido: $idOferta")

        val header = findViewById<View>(R.id.layoutDeArriba)
        header.findViewById<ImageView>(R.id.imgBackgroundHeader)
            .setImageResource(R.drawable.tracking)
        header.findViewById<TextView>(R.id.txtHeaderTitle).text = "Tracking"

        findViewById<ImageButton>(R.id.btnAtras).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvTracking)
        rv.layoutManager = LinearLayoutManager(this)
        adapter = TrackingAdapter(emptyList())
        rv.adapter = adapter

        if (idOferta != -1) cargarSeguimiento()
    }

    private fun cargarSeguimiento() {
        Log.d("TRACKING", "Cargando seguimiento para oferta: $idOferta")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getSeguimiento(idOferta)
                Log.d("TRACKING", "Response code: ${response.code()}")
                Log.d("TRACKING", "Response body: ${response.body()}")
                Log.d("TRACKING", "Error body: ${response.errorBody()?.string()}")

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val seg = response.body()!!

                        val finalizados = seg.pasos.count { it.estadoActualId == 3 }
                        findViewById<TextView>(R.id.tvInfoOferta).text =
                            "Oferta #${seg.ofertaId} · ${seg.incotermNombre} · $finalizados/${seg.pasos.size} pasos completados"

                        adapter.updateData(seg.pasos)
                    }
                }
            } catch (e: Exception) {
                Log.e("TRACKING", "Error: ${e.message}")
            }
        }
    }
}