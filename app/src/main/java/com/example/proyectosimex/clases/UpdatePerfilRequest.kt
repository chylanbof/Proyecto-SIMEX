package com.example.proyectosimex.clases

data class UpdatePerfilRequest(
    val nom: String? = null,
    val empresa: String? = null,
    val telefon: String? = null,
    val dniFoto: String? = null
)