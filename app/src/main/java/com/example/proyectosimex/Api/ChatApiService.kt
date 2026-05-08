package com.example.proyectosimex.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

// Request: lo que enviamos al chatbot
data class ChatRequest(
    val pregunta: String
)

// Un item de la respuesta (cada fila devuelta por el SQL)
// Usamos Map<String, Any> porque las columnas cambian segun la pregunta
data class ChatResponse(
    val pregunta: String,
    val sql: String,
    val respuesta: List<Map<String, Any>>,
    val total_filas: Int
)

interface ChatApiService {

    // POST /webhook/chatbot -> envia la pregunta y recibe la respuesta con SQL y datos
    @POST("webhook/chatbot")
    suspend fun enviarPregunta(@Body request: ChatRequest): Response<ChatResponse>
}