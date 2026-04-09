package com.example.proyectosimex.Clases

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("nom") val nom: String?,
    @SerializedName("cognoms") val cognoms: String?,
    @SerializedName("empresa") val empresa: String?,
    @SerializedName("rol") val rol: String?
                  )