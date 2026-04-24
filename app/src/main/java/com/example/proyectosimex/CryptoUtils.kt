package com.example.proyectosimex

import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import java.security.SecureRandom
import android.util.Base64

object CryptoUtils {

    fun generarClave(): SecretKey {
        val keyGen = KeyGenerator.getInstance("AES")
        keyGen.init(256)
        return keyGen.generateKey()
    }

    fun claveABase64(clave: SecretKey): String =
        Base64.encodeToString(clave.encoded, Base64.NO_WRAP)



    fun encriptar(datos: ByteArray, clave: SecretKey): ByteArray {
        val iv = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, clave, IvParameterSpec(iv))
        val encriptado = cipher.doFinal(datos)
        return iv + encriptado
    }


    fun desencriptar(datos: ByteArray, clave: SecretKey): ByteArray {
        val iv = datos.copyOfRange(0, 16)
        val contenido = datos.copyOfRange(16, datos.size)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.DECRYPT_MODE, clave, IvParameterSpec(iv))
        return cipher.doFinal(contenido)
    }
}