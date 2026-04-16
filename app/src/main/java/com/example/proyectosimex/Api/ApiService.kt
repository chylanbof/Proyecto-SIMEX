import com.example.proyectosimex.Clases.ItemCatalogo
import com.example.proyectosimex.Clases.Oferta
import com.example.proyectosimex.Clases.Usuario
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("api/Usuaris/rol/3")
    suspend fun obtenerUsuariosRol3(): List<Usuario>

    // --- CATÁLOGOS PARA SPINNERS ---
    @GET("api/Incoterms")
    suspend fun getIncoterms(): List<ItemCatalogo>

    @GET("api/TipusTransports")
    suspend fun getTipusTransports(): List<ItemCatalogo>

    @GET("api/Ports")
    suspend fun getPorts(): List<ItemCatalogo>

    @GET("api/Aeroports")
    suspend fun getAeroports(): List<ItemCatalogo>

    @GET("api/transportistes")
    suspend fun getTransportistas(): List<ItemCatalogo>

    @GET("api/tipusvalidacions")
    suspend fun getTipusValidacions(): List<ItemCatalogo>

    @GET("api/tipuscontenidors")
    suspend fun getTipusContenidors(): List<ItemCatalogo>

    // --- OPERACIONES DE OFERTAS ---
    @POST("api/Ofertes")
    suspend fun crearOferta(@Body oferta: Oferta): Response<Oferta>

    @GET("api/Ofertes/Cliente/{id}")
    suspend fun getOfertasByCliente(@Path("id") id: Int): Response<List<Oferta>>

    @PUT("api/Ofertes/{id}")
    suspend fun actualizarOferta(@Path("id") id: Int, @Body oferta: Oferta): Response<Unit>

    @GET("api/Ofertes/{id}")
    suspend fun getOfertasById(@Path("id")id: Int): Response<Oferta>

}