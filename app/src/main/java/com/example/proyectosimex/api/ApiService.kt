package com.example.proyectosimex.api

import com.example.proyectosimex.clases.ContadorEstat
import com.example.proyectosimex.clases.DniResponse
import com.example.proyectosimex.clases.LoginRequest
import com.example.proyectosimex.clases.UpdatePerfilRequest
import com.example.proyectosimex.clases.Usuario
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.Response
import retrofit2.http.PATCH
import retrofit2.http.Path

interface ApiService {

    @GET("api/Usuaris/resumen")
    suspend fun obtenerUsuarios(): List<Usuario>

    @POST("api/Usuaris/login")
    suspend fun login(@Body request: LoginRequest): Response<Usuario>

    @GET("api/Usuaris/contadors/{operadorId}")
    suspend fun getContadors(@Path("operadorId") operadorId: Int): Response<List<ContadorEstat>>

    @PATCH("api/Usuaris/perfil/{id}")
    suspend fun updatePerfil(
        @Path("id") id: Int,
        @Body request: UpdatePerfilRequest
    ): Response<Usuario>

    @PATCH("api/Usuaris/dni/{id}")
    suspend fun updateDni(
        @Path("id") id: Int,
        @Body request: UpdatePerfilRequest
    ): Response<Unit>

    @GET("api/Usuaris/dni/{id}")
    suspend fun getDni(@Path("id") id: Int): Response<DniResponse>

}