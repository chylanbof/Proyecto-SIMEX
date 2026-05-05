package com.example.proyectosimex.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.ScrollView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.proyectosimex.R
import com.example.proyectosimex.api.ChatRequest
import com.example.proyectosimex.api.RetrofitClientChat
import kotlinx.coroutines.launch

class ChatFragment : Fragment() {

    private lateinit var scrollView: ScrollView
    private lateinit var layoutMensajes: LinearLayout
    private lateinit var etPregunta: EditText
    private lateinit var btnEnviar: ImageButton
    private lateinit var progressBar: ProgressBar

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Inicializar vistas
        scrollView = view.findViewById(R.id.scrollViewChat)
        layoutMensajes = view.findViewById(R.id.layoutMensajes)
        etPregunta = view.findViewById(R.id.etPregunta)
        btnEnviar = view.findViewById(R.id.btnEnviar)
        progressBar = view.findViewById(R.id.progressBarChat)

        // Mensaje de bienvenida del bot
        agregarMensajeBot(
            "Hola! Soy el asistente de Prime Logistics. " +
                    "Puedes preguntarme sobre productos exportados, paises, " +
                    "categorias rentables o balanza comercial."
        )

        // Boton enviar
        btnEnviar.setOnClickListener {
            enviarPregunta()
        }

        // Enviar con el teclado (tecla Done/Send)
        etPregunta.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                enviarPregunta()
                true
            } else false
        }
    }

    private fun enviarPregunta() {
        val pregunta = etPregunta.text.toString().trim()
        if (pregunta.isEmpty()) return

        // Mostrar la pregunta del usuario en el chat
        agregarMensajeUsuario(pregunta)
        etPregunta.setText("")
        btnEnviar.isEnabled = false
        progressBar.visibility = View.VISIBLE

        // Llamar a la API en background con corrutinas
        lifecycleScope.launch {
            try {
                val response = RetrofitClientChat.api.enviarPregunta(ChatRequest(pregunta))

                if (response.isSuccessful) {
                    val chatResponse = response.body()
                    if (chatResponse != null) {
                        // Formatear la respuesta para mostrarla
                        val respuestaTexto = formatearRespuesta(chatResponse.respuesta, chatResponse.total_filas)
                        agregarMensajeBot(respuestaTexto)
                    } else {
                        agregarMensajeBot("No he recibido respuesta. Intentalo de nuevo.")
                    }
                } else {
                    agregarMensajeBot("Error al consultar los datos. Intentalo con otra pregunta.")
                }
            } catch (e: Exception) {
                agregarMensajeBot("No puedo conectar con el servidor. Asegurate de que el stack esta corriendo.")
            } finally {
                btnEnviar.isEnabled = true
                progressBar.visibility = View.GONE
            }
        }
    }

    // Convierte la lista de resultados en texto legible
    private fun formatearRespuesta(
        respuesta: List<Map<String, Any>>,
        totalFilas: Int
    ): String {
        if (respuesta.isEmpty()) return "No he encontrado datos para esa consulta."

        val sb = StringBuilder()
        sb.append("He encontrado $totalFilas resultado(s):\n\n")

        respuesta.forEachIndexed { index, fila ->
            sb.append("${index + 1}. ")
            fila.entries.forEach { (clave, valor) ->
                // Formatear numeros grandes con separadores de miles
                val valorFormateado = try {
                    val numero = valor.toString().toDouble().toLong()
                    "%,d".format(numero)
                } catch (e: NumberFormatException) {
                    valor.toString()
                }
                sb.append("$clave: $valorFormateado  ")
            }
            sb.append("\n")
        }

        return sb.toString().trim()
    }

    // Agrega un mensaje del usuario (alineado a la derecha, fondo naranja)
    private fun agregarMensajeUsuario(texto: String) {
        val textView = TextView(requireContext()).apply {
            this.text = texto
            textSize = 14f
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            setBackgroundResource(R.drawable.rounded_button)
            backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.holo_orange_dark)
            setPadding(32, 20, 32, 20)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.END
            setMargins(80, 8, 16, 8)
        }

        layoutMensajes.addView(textView, params)
        scrollAlFinal()
    }

    // Agrega un mensaje del bot (alineado a la izquierda, fondo gris claro)
    private fun agregarMensajeBot(texto: String) {
        val textView = TextView(requireContext()).apply {
            this.text = texto
            textSize = 14f
            setTextColor(ContextCompat.getColor(requireContext(), android.R.color.black))
            setBackgroundResource(R.drawable.rounded_button)
            backgroundTintList = ContextCompat.getColorStateList(requireContext(), android.R.color.darker_gray)
            setPadding(32, 20, 32, 20)
        }

        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply {
            gravity = android.view.Gravity.START
            setMargins(16, 8, 80, 8)
        }

        layoutMensajes.addView(textView, params)
        scrollAlFinal()
    }

    // Hacer scroll al ultimo mensaje
    private fun scrollAlFinal() {
        scrollView.post {
            scrollView.fullScroll(ScrollView.FOCUS_DOWN)
        }
    }
}