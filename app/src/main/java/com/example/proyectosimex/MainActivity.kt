package com.example.proyectosimex

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.widget.Button
import android.widget.EditText
import com.example.proyectosimex.clases.LoginRequest
import kotlinx.coroutines.*
import com.example.proyectosimex.api.RetrofitClient

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val textBoxUsuario = findViewById<EditText>(R.id.usuarioTextBox)

        val textBoxPassword = findViewById<EditText>(R.id.contraseñaTextBox)
        val btnIniciarSesion = findViewById<Button>(R.id.btnLogin)
        btnIniciarSesion.setOnClickListener {

            val usuario = textBoxUsuario.text.toString()
            val password = textBoxPassword.text.toString()

            val request = LoginRequest(usuario, password)

            CoroutineScope(Dispatchers.IO).launch {

                try {
                    val response = RetrofitClient.api.login(request)

                    withContext(Dispatchers.Main) {

                        if (response.isSuccessful) {
                            val user = response.body()

                            val intent = when (user?.rolId) {
                                4 -> Intent(this@MainActivity, AgenteComercial::class.java)  // rol 4 = agente
                                else -> Intent(this@MainActivity, Dashboard::class.java)      // rol 3 = cliente
                            }

                            intent.putExtra("usuario_nombre", user?.nom)
                            intent.putExtra("usuario_id", user?.id)
                            intent.putExtra("usuario_cognoms", user?.cognoms)
                            intent.putExtra("usuario_empresa", user?.empresa)
                            intent.putExtra("usuario_telefon", user?.telefon)
                            startActivity(intent)

                        } else {
                            android.widget.Toast.makeText(
                                this@MainActivity,
                                "Usuario o contraseña incorrectos",
                                android.widget.Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(
                            this@MainActivity,
                            "Error conexión: ${e.message}",
                            android.widget.Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }
}