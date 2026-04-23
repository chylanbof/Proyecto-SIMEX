package com.example.proyectosimex.clases

import com.google.gson.annotations.SerializedName

data class Oferta(
    @SerializedName("id") val id: Int = 0,
    @SerializedName("tipusTransportId") val tipusTransportId: Int,
    @SerializedName("tipusFluxeId") val tipusFluxeId: Int,
    @SerializedName("tipusCarregaId") val tipusCarregaId: Int,
    @SerializedName("incotermId") val incotermId: Int,
    @SerializedName("clientId") val clientId: Int,
    @SerializedName("comentaris") val comentaris: String? = null,
    @SerializedName("agentComercialId") val agentComercialId: Int? = null,
    @SerializedName("pesBrut") val pesBrut: Double? = null,
    @SerializedName("volum") val volum: Double? = null,
    @SerializedName("tipusValidacioId") val tipusValidacioId: Int = 1,
    @SerializedName("estatOfertaId") val estatOfertaId: Int = 1,
    @SerializedName("operadorId") val operadorId: Int,
    @SerializedName("raoRebuig") val raoRebuig: String? = null,
    @SerializedName("portOrigenId") val portOrigenId: Int? = null,
    @SerializedName("portDestiId") val portDestiId: Int? = null,
    @SerializedName("aeroportOrigenId") val aeroportOrigenId: Int? = null,
    @SerializedName("aeroportDestiId") val aeroportDestiId: Int? = null,
    @SerializedName("transportistaId") val transportistaId: Int? = null,
    @SerializedName("tipusContenidorId") val tipusContenidorId: Int? = null,
    @SerializedName("estatEnvioId") val estatEnvioId: Int? = null,
    @SerializedName("dataCreacio") val dataCreacio: String,
    @SerializedName("dataValidessaInicial") val dataValidessaInicial: String?,
    @SerializedName("dataValidessaFina") val dataValidessaFina: String?
)