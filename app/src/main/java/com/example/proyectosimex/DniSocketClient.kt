package com.example.proyectosimex

import android.util.Base64
import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import javax.crypto.spec.SecretKeySpec
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

object DniSocketClient {

    private const val HOST = "10.0.2.2"
    private const val PORT = 9000
    private const val TAG = "DniSocketClient"

    private val mutex = Mutex()

    fun subirDNI(usuarioId: Int, bytesImagen: ByteArray): String? {
        Log.d(TAG, "▶ subirDNI iniciado — usuarioId=$usuarioId, tamaño=${bytesImagen.size} bytes")

        // El cliente genera la clave
        val clave = CryptoUtils.generarClave()
        val claveBase64 = CryptoUtils.claveABase64(clave)
        val bytesEncriptados = CryptoUtils.encriptar(bytesImagen, clave)

        val socket = Socket().apply {
            connect(java.net.InetSocketAddress(HOST, PORT), 5000)
            soTimeout = 10000
        }
        return try {
            val salida = DataOutputStream(socket.getOutputStream())
            val entrada = DataInputStream(socket.getInputStream())

            // Envía los bytes ya encriptados
            salida.writeUTF("SUBIR")
            salida.writeInt(usuarioId)
            salida.writeInt(bytesEncriptados.size)
            Log.d(TAG, "📤 Enviando ${bytesEncriptados.size} bytes, primeros: ${bytesEncriptados.take(16).map { it.toInt() and 0xFF }}")
            salida.write(bytesEncriptados)
            salida.flush()
            Log.d(TAG, "📤 Datos enviados al servidor")

            val respuesta = entrada.readUTF()
            Log.d(TAG, "📥 Respuesta del servidor: $respuesta")

            if (respuesta == "OK") {
                Log.d(TAG, "✅ Subida confirmada, clave: $claveBase64")
                claveBase64

            } else {
                Log.e(TAG, "❌ El servidor no devolvió OK")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "💥 Excepción en subirDNI: ${e.javaClass.simpleName} — ${e.message}")
            null
        } finally {
            socket.close()
            Log.d(TAG, "🔌 Socket cerrado (subir)")
        }
    }

    suspend fun bajarDNI(usuarioId: Int, claveBase64: String): ByteArray? {
        return mutex.withLock {
            bajarDNIInterno(usuarioId, claveBase64)
        }
    }

    private fun bajarDNIInterno(usuarioId: Int, claveBase64: String): ByteArray? {
        Log.d(TAG, "▶ bajarDNI iniciado — usuarioId=$usuarioId")
        val socket = Socket().apply {
            connect(java.net.InetSocketAddress(HOST, PORT), 10000)
            soTimeout = 30000
        }
        return try {
            val salida = DataOutputStream(socket.getOutputStream())
            val entrada = DataInputStream(socket.getInputStream())

            salida.writeUTF("BAJAR")
            salida.writeInt(usuarioId)
            salida.flush()

            val respuesta = entrada.readUTF()
            Log.d(TAG, "📥 Respuesta del servidor: $respuesta")

            if (respuesta == "OK") {
                val tamaño = entrada.readInt()
                Log.d(TAG, "📦 Tamaño del archivo encriptado: $tamaño bytes")

                if (tamaño <= 0 || tamaño > 10_000_000) {
                    Log.e(TAG, "❌ Tamaño inválido: $tamaño")
                    return null
                }

                val bytesEncriptados = ByteArray(tamaño)
                var totalLeidos = 0
                while (totalLeidos < tamaño) {
                    val leidos = entrada.read(bytesEncriptados, totalLeidos, tamaño - totalLeidos)
                    if (leidos == -1) {
                        Log.e(TAG, "❌ EOF tras $totalLeidos bytes de $tamaño esperados")
                        break
                    }
                    totalLeidos += leidos
                    Log.d(TAG, "📊 Leídos: $totalLeidos / $tamaño")
                }
                if (totalLeidos < tamaño) return null
                Log.d(TAG, "📥 Bytes recibidos correctamente")

                val claveBytes = Base64.decode(claveBase64, Base64.NO_WRAP)
                val clave = SecretKeySpec(claveBytes, "AES")
                // Antes de CryptoUtils.desencriptar
                Log.d(TAG, "🔓 Desencriptando ${bytesEncriptados.size} bytes, primeros: ${bytesEncriptados.take(16).map { it.toInt() and 0xFF }}")
                val resultado = CryptoUtils.desencriptar(bytesEncriptados, clave)
                Log.d(TAG, "✅ Desencriptado OK — tamaño final: ${resultado.size} bytes")
                resultado
            } else {
                val error = entrada.readUTF()
                Log.e(TAG, "❌ Error del servidor: $error")
                null
            }
        } catch (e: java.net.SocketTimeoutException) {
            Log.e(TAG, "⏱️ Timeout leyendo datos del servidor")
            null
        } catch (e: Exception) {
            Log.e(TAG, "💥 Excepción en bajarDNI: ${e.javaClass.simpleName} — ${e.message}")
            e.printStackTrace()
            null
        } finally {
            socket.close()
            Log.d(TAG, "🔌 Socket cerrado (bajar)")
        }
    }
}