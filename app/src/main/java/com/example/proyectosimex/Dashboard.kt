package com.example.proyectosimex

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
        val usuarioCognoms = intent.getStringExtra("usuario_cognoms") ?: ""
        val usuarioEmpresa = intent.getStringExtra("usuario_empresa") ?: ""
        val usuarioTelefon = intent.getStringExtra("usuario_telefon") ?: ""

        val textViewBienvenido = findViewById<TextView>(R.id.txtHeaderTitle)
        textViewBienvenido.text = "Bienvenido, $usuario"

        val txtNumero1 = findViewById<TextView>(R.id.numero1)
        val txtNumero2 = findViewById<TextView>(R.id.numero2)
        val txtNumero3 = findViewById<TextView>(R.id.numero3)

        if (usuarioId != -1) {
            // Contadores de badges (En preparación, En tránsito, Entregado hoy)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    Log.d("ESTADOS", "Llamando API con usuarioId=$usuarioId")
                    val response = RetrofitClient.api.getContadors(usuarioId)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val contadors = response.body() ?: emptyList()
                            contadors.forEach {
                                Log.d("ESTADOS", "estat: '${it.estat}' | count: ${it.count}")
                            }

                            val enTransito = contadors.find {
                                it.estat.contains("tránsito", ignoreCase = true) ||
                                        it.estat.contains("trànsit", ignoreCase = true)
                            }?.count ?: 0

                            val enPreparacion = contadors.find {
                                it.estat.contains("preparaci", ignoreCase = true)
                            }?.count ?: 0

                            val entregado = contadors.find {
                                it.estat.contains("entregado", ignoreCase = true) ||
                                        it.estat.contains("entregat", ignoreCase = true)
                            }?.count ?: 0

                            txtNumero1.text = enTransito.toString()
                            txtNumero2.text = enPreparacion.toString()
                            txtNumero3.text = entregado.toString()
                        } else {
                            Log.e("ESTADOS", "Error HTTP: ${response.code()}")
                        }
                    }
                } catch (e: Exception) {
                    Log.e("ESTADOS", "Excepción: ${e.message}")
                }
            }

            // Contadores de tarjetas (Resumen, Ofertas, Historial)
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val tvResumen = findViewById<TextView>(R.id.tvSubtituloResumen)
                    val tvOfertas = findViewById<TextView>(R.id.tvSubtituloOfertas)
                    val tvHistorial = findViewById<TextView>(R.id.tvSubtituloHistorial)

                    val enviosResponse = RetrofitClient.api.getEnviosByCliente(usuarioId)
                    val ofertasResponse = RetrofitClient.api.getOfertasByCliente(usuarioId)

                    withContext(Dispatchers.Main) {
                        if (enviosResponse.isSuccessful) {
                            val envios = enviosResponse.body() ?: emptyList()
                            val activos = envios.filter { it.estadoEnvio != "Entregado hoy" }.size
                            val finalizados = envios.filter { it.estadoEnvio == "Entregado hoy" }.size
                            tvResumen.text = "En reparto $activos"
                            tvHistorial.text = "$finalizados Pedidos Guardados"
                        }
                        if (ofertasResponse.isSuccessful) {
                            val pendientes = ofertasResponse.body()
                                ?.filter { it.estatOfertaId == 1 }?.size ?: 0
                            tvOfertas.text = "$pendientes Ofertas Disponibles"
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DASHBOARD", "Error contadores tarjetas: ${e.message}")
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
            intent.putExtra("usuario_id", usuarioId)
            intent.putExtra("usuario_cognoms", usuarioCognoms)
            intent.putExtra("usuario_empresa", usuarioEmpresa)
            intent.putExtra("usuario_telefon", usuarioTelefon)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardResumen).setOnClickListener {
            val intent = Intent(this, Resumen::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardHistorial).setOnClickListener {
            val intent = Intent(this, Historial::class.java)
            intent.putExtra("usuario_id", usuarioId)
            startActivity(intent)
        }

        findViewById<CardView>(R.id.cardOfertas).setOnClickListener {
            val intent = Intent(this, OfertasUsuarios::class.java)
            intent.putExtra("usuario_id", usuarioId)
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