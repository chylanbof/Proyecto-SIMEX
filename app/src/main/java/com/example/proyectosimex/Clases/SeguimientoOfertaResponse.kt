package com.example.proyectosimex

import com.google.gson.annotations.SerializedName

data class SeguimientoOfertaResponse(
    @SerializedName("ofertaId") val ofertaId: Int,
    @SerializedName("incotermNombre") val incotermNombre: String,
    @SerializedName("estatEnvioGeneralId") val estatEnvioGeneralId: Int?,
    @SerializedName("pasos") val pasos: List<PasoSeguimientoItem>
)

data class PasoSeguimientoItem(
    @SerializedName("trackingStepId") val trackingStepId: Int,
    @SerializedName("nombrePaso") val nombrePaso: String,
    @SerializedName("orden") val orden: Int,
    @SerializedName("estadoActualId") var estadoActualId: Int?
)
