package com.example.proyectosimex.clases

import com.google.gson.annotations.SerializedName

data class ContadorEstat(
    @SerializedName("estat") val estat: String,
    @SerializedName("count") val count: Int
)