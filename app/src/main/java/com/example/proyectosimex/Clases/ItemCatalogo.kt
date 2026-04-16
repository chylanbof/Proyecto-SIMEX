package com.example.proyectosimex.Clases

import com.google.gson.annotations.SerializedName

data class ItemCatalogo(
    @SerializedName("id") val id: Int,
    @SerializedName("nom") val nom: String?
                       ) {
    // El Spinner usa toString() para mostrar el texto al usuario
    override fun toString(): String {
        return nom ?: "Sin nombre"
    }
}