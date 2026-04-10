package com.example.proyectosimex

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class Historial : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_historial)

        val header = findViewById<View>(R.id.layoutDeArriba)
        header.findViewById<ImageView>(R.id.imgBackgroundHeader).setImageResource(R.drawable.historial)

        header.findViewById<TextView>(R.id.txtHeaderTitle).setText("Historial")

        findViewById<ImageButton>(R.id.btnAtras).setOnClickListener {
            finish()
        }


        findViewById<TextView>(R.id.tvSinPedidos).visibility = View.VISIBLE

        val btnOpciones = findViewById<View>(R.id.pedidoPlaceholder)
            .findViewById<ImageButton>(R.id.btnOpciones)

        btnOpciones.setOnClickListener { anchorView ->
            val popup = PopupMenu(this, anchorView)
            popup.menuInflater.inflate(R.menu.menu_pedido, popup.menu)
            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.accionDescargar -> {

                        true
                    }
                    R.id.accionEliminar -> {

                        true
                    }
                    else -> false
                }
            }
            popup.show()
        }

        findViewById<CardView>(R.id.pedidoPlaceholder).setOnClickListener {
            val intent = Intent(this, NombrePedido::class.java).apply {
                putExtra("numeroOferta", "EC002")
                putExtra("nombrePedido", "Pedido EC002")
            }
            startActivity(intent)
        }
    }


}