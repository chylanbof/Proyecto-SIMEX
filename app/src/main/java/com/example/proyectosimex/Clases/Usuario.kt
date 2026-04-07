package com.example.proyectosimex.Clases

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("Id") val id: Int,
    @SerializedName("Correu") val correu: String,
    @SerializedName("Contrasenya") val contrasenya: String,
    @SerializedName("Nom") val nom: String,
    @SerializedName("Cognoms") val cognoms: String,
    @SerializedName("Empresa") val empresa: String,
    @SerializedName("RolId") val rol_id: Int
                  )