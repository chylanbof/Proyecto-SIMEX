package com.example.proyectosimex.Api

import com.example.proyectosimex.Clases.Usuario
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {

    @GET("api/Usuaris/resumen")
    suspend fun obtenerUsuarios(): List<Usuario>

}