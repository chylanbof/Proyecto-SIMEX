package com.example.proyectosimex.Clases

import com.google.gson.annotations.SerializedName

data class Usuario(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("correu") val correu: String? = null,
    @SerializedName("contrasenya") val contrasenya: String? = null,
    @SerializedName("nom") val nom: String? = null,
    @SerializedName("cognoms") val cognoms: String? = null,
    @SerializedName("empresa") val empresa: String? = null,

    // Para el ID del rol (número)
    @SerializedName("rolId") val rolId: Int? = null,

    // Para el nombre del rol (texto que viene en el resumen)
    @SerializedName("rol") val rol: String? = null
                  )