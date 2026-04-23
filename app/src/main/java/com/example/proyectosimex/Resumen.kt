package com.example.proyectosimex

import Envio
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyectosimex.adapters.ResumenAdapter
import com.example.proyectosimex.api.RetrofitClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class Resumen : AppCompatActivity() {

    private lateinit var adapter: ResumenAdapter
    private var clientId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resumen)

        clientId = intent.getIntExtra("usuario_id", -1)

        val header = findViewById<View>(R.id.layoutDeArriba)
        header.findViewById<ImageView>(R.id.imgBackgroundHeader).setImageResource(R.drawable.resumen)
        header.findViewById<TextView>(R.id.txtHeaderTitle).setText("Resumen")

        findViewById<ImageButton>(R.id.btnAtras).setOnClickListener { finish() }

        val rv = findViewById<RecyclerView>(R.id.rvResumen)
        rv.layoutManager = LinearLayoutManager(this)

        adapter = ResumenAdapter(
            emptyList(),
            onVerMas = { envio -> mostrarPopupVerMas(envio) },
            onTracking = { envio ->
                val ofertaIdInt = envio.ofertaId?.toIntOrNull() ?: -1
                Log.d("TRACKING", "Enviando idOferta: $ofertaIdInt, ofertaId string: ${envio.ofertaId}")
                val intent = Intent(this, Tracking::class.java)
                intent.putExtra("idOferta", ofertaIdInt)
                startActivity(intent)
            }



        )
        rv.adapter = adapter

        cargarEnvios()
    }

    private fun cargarEnvios() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = RetrofitClient.api.getEnviosByCliente(clientId)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val envios = response.body() ?: emptyList()
                        val activos = envios.filter { it.estadoEnvio != "Entregado hoy" }

                        findViewById<TextView>(R.id.tvSinEnvios).visibility =
                            if (activos.isEmpty()) View.VISIBLE else View.GONE

                        adapter.updateData(activos)
                    }
                }
            } catch (e: Exception) {
                Log.e("RESUMEN", "Error: ${e.message}")
            }
        }
    }

    private fun mostrarPopupVerMas(envio: Envio) {
        val dialog = android.app.Dialog(this)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_ver_mas)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        dialog.findViewById<TextView>(R.id.tvPopupIdPedido).text = "Envío #${envio.id}"
        dialog.findViewById<TextView>(R.id.tvPopupTipoPedido).text = envio.metodoTransporte ?: ""
        dialog.findViewById<TextView>(R.id.tvPopupNombreEnvio).text = envio.contenidoEnvio ?: "Sin descripción"
        dialog.findViewById<TextView>(R.id.tvPopupEmpresa).text = envio.compania ?: ""

        mostrarCampoOpcional(dialog, R.id.rowPago, R.id.tvPopupPago,
            if (envio.tipoDivisa != null) "Divisa: ${envio.tipoDivisa}" else null)
        mostrarCampoOpcional(dialog, R.id.rowFecha, R.id.tvPopupFecha,
            "Fecha pedido: ${envio.fechaPedido}")
        mostrarCampoOpcional(dialog, R.id.rowPaisDestino, R.id.tvPopupPaisDestino,
            "Destino: ${envio.destino}")

        dialog.show()
    }

    private fun mostrarCampoOpcional(dialog: android.app.Dialog, rowId: Int, tvId: Int, valor: String?) {
        val row = dialog.findViewById<View>(rowId)
        if (!valor.isNullOrBlank()) {
            row.visibility = View.VISIBLE
            dialog.findViewById<TextView>(tvId).text = valor
        } else {
            row.visibility = View.GONE
        }
    }
}