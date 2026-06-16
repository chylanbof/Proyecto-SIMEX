package com.example.proyectosimex

import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileOutputStream

class NombrePedido : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_nombre_pedido)

        // Recibir datos
        val numeroOferta = intent.getStringExtra("numeroOferta") ?: ""
        val nombrePedido = intent.getStringExtra("nombrePedido") ?: "Pedido"
        val cliente = intent.getStringExtra("cliente") ?: "-"
        val ruta = intent.getStringExtra("ruta") ?: "-"
        val modo = intent.getStringExtra("modo") ?: "-"
        val peso = intent.getStringExtra("peso") ?: "-"
        val incoterm = intent.getStringExtra("incoterm") ?: "-"
        val urgencia = intent.getStringExtra("urgencia") ?: "-"

        // Rellenar UI
        findViewById<TextView>(R.id.tvNumeroOferta).text = numeroOferta
        findViewById<TextView>(R.id.tvTituloSolicitud).text = nombrePedido
        findViewById<TextView>(R.id.tvCliente).text = cliente
        findViewById<TextView>(R.id.tvRuta).text = ruta
        findViewById<TextView>(R.id.tvModo).text = modo
        findViewById<TextView>(R.id.tvPeso).text = "$peso kg"
        findViewById<TextView>(R.id.tvIncoterm).text = incoterm
        findViewById<TextView>(R.id.tvUrgencia).text = urgencia

        val header = findViewById<View>(R.id.layoutDeArriba)
        header.findViewById<ImageView>(R.id.imgBackgroundHeader).setImageResource(R.drawable.nombrepedido)
        header.findViewById<TextView>(R.id.txtHeaderTitle).text = nombrePedido

        findViewById<ImageButton>(R.id.btnAtras).setOnClickListener { finish() }

        findViewById<Button>(R.id.btnDescargarPdf).setOnClickListener {
            generarPdf(numeroOferta, cliente, ruta, modo, peso, incoterm, urgencia)
        }
    }

    private fun generarPdf(
        numeroOferta: String, cliente: String, ruta: String,
        modo: String, peso: String, incoterm: String, urgencia: String
    ) {
        val document = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = document.startPage(pageInfo)
        val canvas = page.canvas

        val paintTitulo = Paint().apply {
            textSize = 20f
            isFakeBoldText = true
        }
        val paintLabel = Paint().apply {
            textSize = 14f
            color = android.graphics.Color.GRAY
        }
        val paintValor = Paint().apply {
            textSize = 14f
            isFakeBoldText = true
        }

        var y = 60f
        val xLabel = 40f
        val xValor = 320f
        val lineHeight = 36f

        canvas.drawText("Detalle de Envío", xLabel, y, paintTitulo)
        y += lineHeight * 1.5f

        val campos = listOf(
            "Número de oferta:" to numeroOferta,
            "Cliente:" to cliente,
            "Ruta:" to ruta,
            "Modo:" to modo,
            "Peso:" to "$peso kg",
            "Incoterm:" to incoterm,
            "Urgencia:" to urgencia
        )

        campos.forEach { (label, valor) ->
            canvas.drawText(label, xLabel, y, paintLabel)
            canvas.drawText(valor, xValor, y, paintValor)
            y += lineHeight
        }

        document.finishPage(page)

        try {
            val dir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(dir, "envio_${numeroOferta.replace(" ", "_")}.pdf")
            document.writeTo(FileOutputStream(file))
            document.close()
            Toast.makeText(this, "PDF guardado en: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}