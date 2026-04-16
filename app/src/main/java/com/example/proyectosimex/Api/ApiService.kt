package com.example.proyectosimex.api

import com.example.proyectosimex.clases.ContadorEstat
import com.example.proyectosimex.clases.DniResponse
import com.example.proyectosimex.clases.ItemCatalogo
import com.example.proyectosimex.clases.LoginRequest
import com.example.proyectosimex.clases.Oferta
import com.example.proyectosimex.clases.UpdatePerfilRequest
import com.example.proyectosimex.clases.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("api/Usuaris/login")
    suspend fun login(@Body request: LoginRequest): Response<Usuario>

    @GET("api/Usuaris/contadors/{clientId}")
    suspend fun getContadors(@Path("clientId") clientId: Int): Response<List<ContadorEstat>>

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

    //
    //Anthony Api -- Unificar Ofertas.
    //
    @GET("api/Usuaris/rol/3")
    suspend fun obtenerUsuariosRol3(): List<Usuario>

    @GET("api/Incoterms")
    suspend fun getIncoterms(): List<ItemCatalogo>

    @GET("api/TipusTransports")
    suspend fun getTipusTransports(): List<ItemCatalogo>

    @GET("api/Ports")
    suspend fun getPorts(): List<ItemCatalogo>

    @GET("api/Aeroports")
    suspend fun getAeroports(): List<ItemCatalogo>

    @GET("api/transportistes")
    suspend fun getTransportistas(): List<ItemCatalogo>

    @GET("api/tipusvalidacions")
    suspend fun getTipusValidacions(): List<ItemCatalogo>

    @GET("api/tipuscontenidors")
    suspend fun getTipusContenidors(): List<ItemCatalogo>

    @POST("api/Ofertes")
    suspend fun crearOferta(@Body oferta: Oferta): Response<Oferta>

    @GET("api/Ofertes/Cliente/{id}")
    suspend fun getOfertasByCliente(@Path("id") id: Int): Response<List<Oferta>>

    @PUT("api/Ofertes/{id}")
    suspend fun actualizarOferta(@Path("id") id: Int, @Body oferta: Oferta): Response<Unit>

    @GET("api/Ofertes/{id}")
    suspend fun getOfertasById(@Path("id") id: Int): Response<Oferta>
}