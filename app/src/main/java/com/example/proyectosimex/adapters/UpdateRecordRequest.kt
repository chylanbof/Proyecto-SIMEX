package com.example.proyectosimex.adapters

data class PartidaRequest( //post
    val usuariId: Int,
    val puntuacion: Int
)

data class RecordRequest( //put
    val puntuacion: Int
)