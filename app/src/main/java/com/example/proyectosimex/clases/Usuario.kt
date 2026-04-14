package com.example.proyectosimex.clases

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id") val id: Int,
    @SerializedName("nom") val nom: String?,
    @SerializedName("cognoms") val cognoms: String?,
    @SerializedName("empresa") val empresa: String?,
    @SerializedName("rol") val rol: String?
                  )