package com.example.proyectosimex.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.getValue
import kotlin.jvm.java

object RetrofitClientChat {

    // 10.0.2.2 es la IP que usa el emulador de Android para acceder a localhost del Mac
    // N8N corre en el puerto 5678
    private const val BASE_URL = "http://10.0.2.2:5678/"

    val api: ChatApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ChatApiService::class.java)
    }
}