package com.example.proyectosimex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.cardview.widget.CardView
import kotlinx.coroutines.*
import com.example.proyectosimex.api.RetrofitClient

class Dashboard : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        val usuario = intent.getStringExtra("usuario_nombre") ?: ""
        val usuarioId = intent.getIntExtra("usuario_id", -1)

        val textViewBienvenido = findViewById<TextView>(R.id.txtHeaderTitle)
        textViewBienvenido.text = "Bienvenido, $usuario"

        val txtNumero1 = findViewById<TextView>(R.id.numero1)
        val txtNumero2 = findViewById<TextView>(R.id.numero2)
        val txtNumero3 = findViewById<TextView>(R.id.numero3)

        // Cargar contadores si tenemos un id válido
        if (usuarioId != -1) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.api.getContadors(usuarioId)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val contadors = response.body() ?: emptyList()

                            // Busca cada estado y actualiza su contador
                            // Ajusta los nombres según lo que devuelve tu API
                            val enTransito = contadors.find {
                                it.estat.contains("trànsit", ignoreCase = true) ||
                                        it.estat.contains("transito", ignoreCase = true) ||
                                        it.estat.contains("Enviada", ignoreCase = true)
                            }?.count ?: 0

                            val enPreparacion = contadors.find {
                                it.estat.contains("preparaci", ignoreCase = true) ||
                                        it.estat.contains("Acceptada", ignoreCase = true)
                            }?.count ?: 0

                            val entregado = contadors.find {
                                it.estat.contains("entregad", ignoreCase = true) ||
                                        it.estat.contains("entregat", ignoreCase = true)
                            }?.count ?: 0

                            txtNumero1.text = enTransito.toString()
                            txtNumero2.text = enPreparacion.toString()
                            txtNumero3.text = entregado.toString()
                        }
                    }
                } catch (e: Exception) {
                    // Si falla la conexión, los contadores se quedan en 0
                }
            }
        }

        val btnNotificaciones = findViewById<ImageButton>(R.id.btnNotificaciones)
        btnNotificaciones.setOnClickListener {
            mostrarMenu(btnNotificaciones)
        }

        val btnPerfil = findViewById<AppCompatButton>(R.id.perfil)
        btnPerfil.setOnClickListener {
            val intent = Intent(this, Perfil::class.java)
            intent.putExtra("usuario_nombre", usuario)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardResumen).setOnClickListener {
            val intent = Intent(this, Resumen::class.java)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardHistorial).setOnClickListener {
            val intent = Intent(this, Historial::class.java)
            startActivity(intent)
        }
    }

    fun mostrarMenu(anchor: View) {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup_notificaciones, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        )

        val location = IntArray(2)
        anchor.getLocationInWindow(location)
        val posX = location[0] + anchor.width + 10
        val posY = location[1]

        popupWindow.showAtLocation(anchor, android.view.Gravity.NO_GRAVITY, posX, posY)
    }
}