package com.example.proyectosimex
import com.example.proyectosimex.Api.ApiService

import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.Retrofit

object RetrofitClient {

    private const val BASE_URL = "http://10.0.2.2:8080/"

    val instancia: ApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ApiService::class.java)
    }

}