data class Envio(
    val id: Int,
    val origen: String,
    val destino: String,
    val estadoEnvio: String,
    val ofertaId: String,
    val contenidoEnvio: String,
    val metodoTransporte: String,
    val tipoDivisa: String,
    val fechaPedido: String,
    val cliente: String,
    val ruta: String,
    val pesoKg: Double,
    val incoterm: String,
    val urgencia: String,
    val compania: String,
    val clienteId: Int
)