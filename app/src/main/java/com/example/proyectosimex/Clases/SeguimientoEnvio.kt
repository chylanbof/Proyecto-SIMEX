package com.example.proyectosimex.clases

data class SeguimientoEnvio(
    val ofertaId: Int,
    val incotermNombre: String,
    val estatEnvioGeneralId: Int?,
    val pasos: List<PasoSeguimiento>
)

data class PasoSeguimiento(
    val trackingStepId: Int,
    val nombrePaso: String,
    val orden: Int,
    val estadoActualId: Int
)