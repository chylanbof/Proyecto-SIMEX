package com.example.proyectosimex

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Resumen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resumen)

        val header = findViewById<View>(R.id.layoutDeArriba)
        header.findViewById<ImageView>(R.id.imgBackgroundHeader).setImageResource(R.drawable.resumen)

        header.findViewById<TextView>(R.id.txtHeaderTitle).setText("Resumen")

        findViewById<ImageButton>(R.id.btnAtras).setOnClickListener {
            finish()
        }

        val card = findViewById<View>(R.id.resumenPlaceholder)

        card.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnVerMas)
            .setOnClickListener {
                mostrarPopupVerMas()
            }

        card.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnTracking)
            .setOnClickListener {
                val intent = Intent(this, Tracking::class.java)
                startActivity(intent)
            }

    }

    private fun mostrarPopupVerMas() {
        val dialog = android.app.Dialog(this)
        dialog.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.popup_ver_mas)
        dialog.window?.setBackgroundDrawable(android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT))
        dialog.window?.setLayout(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
                                )

        dialog.findViewById<TextView>(R.id.tvPopupIdPedido).text = "EC001"
        dialog.findViewById<TextView>(R.id.tvPopupTipoPedido).text = "Exportacion Maritima"
        dialog.findViewById<TextView>(R.id.tvPopupNombreEnvio).text = "Envio de Material Liturgico"
        dialog.findViewById<TextView>(R.id.tvPopupEmpresa).text = "Empresa PMP"


        mostrarCampoOpcional(dialog, R.id.rowPago, R.id.tvPopupPago, "Pago: Paypal")
        mostrarCampoOpcional(dialog, R.id.rowFecha, R.id.tvPopupFecha, "Fecha de pedido: 12 abril 2025")
        mostrarCampoOpcional(dialog, R.id.rowPaisDestino, R.id.tvPopupPaisDestino, "Pais destino: China")

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