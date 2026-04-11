package com.example.proyectosimex.Clases

import com.google.gson.annotations.SerializedName

//  Cuando conectemos con la api tendremos que poner las clases que estan en el framework
data class Oferta(
    @SerializedName("id") val id: Int,
    @SerializedName("origen") val origen: String?,
    @SerializedName("destino") val destino: String?,
    @SerializedName("precio") val precio: Double? = 0.0 // Podrías añadir más campos luego
)
