package com.example.proyectosimex

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.proyectosimex.api.RetrofitClient
import com.example.proyectosimex.clases.UpdatePerfilRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast

class Perfil : AppCompatActivity() {

    private var dniBase64: String? = null
    private var usuarioId: Int = -1

    // Launcher para seleccionar imagen de galería
    // Launcher - ahora sube automáticamente al seleccionar
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val btnSubir = findViewById<Button>(R.id.btnSubirDNI)
            btnSubir.isEnabled = false

            val original = MediaStore.Images.Media.getBitmap(contentResolver, it)
            val scaled = Bitmap.createScaledBitmap(original, 800, 600, true)
            val outputStream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val bytesImagen = outputStream.toByteArray() // ← se define AQUÍ, antes del coroutine

            CoroutineScope(Dispatchers.IO).launch {
                val claveBase64 = DniSocketClient.subirDNI(usuarioId, bytesImagen)
                withContext(Dispatchers.Main) {
                    btnSubir.isEnabled = true
                    if (claveBase64 != null) {
                        guardarClave(usuarioId, claveBase64)
                        Toast.makeText(this@Perfil, "DNI subido correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this@Perfil, "Error al subir el DNI", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } ?: run {
            findViewById<Button>(R.id.btnSubirDNI).isEnabled = true
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_perfil)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        usuarioId = intent.getIntExtra("usuario_id", -1)
        var usuarioNom = intent.getStringExtra("usuario_nombre") ?: ""
        val usuarioCognoms = intent.getStringExtra("usuario_cognoms") ?: ""
        var usuarioEmpresa = intent.getStringExtra("usuario_empresa") ?: ""
        var usuarioTelefon = intent.getStringExtra("usuario_telefon") ?: ""

        val tvNombre = findViewById<TextView>(R.id.tvNombreUsuario)
        val item1 = findViewById<View>(R.id.itemTipo)
        val item2 = findViewById<View>(R.id.itemEmpresa)
        val item3 = findViewById<View>(R.id.itemPuesto)

        fun actualizarUI() {
            tvNombre.text = usuarioNom
            item1.findViewById<TextView>(R.id.tvNumero).text = "1"
            item1.findViewById<TextView>(R.id.tvDato).text = "$usuarioNom $usuarioCognoms"
            item2.findViewById<TextView>(R.id.tvNumero).text = "2"
            item2.findViewById<TextView>(R.id.tvDato).text = "Empresa: $usuarioEmpresa"
            item3.findViewById<TextView>(R.id.tvNumero).text = "3"
            item3.findViewById<TextView>(R.id.tvDato).text = "Teléfono: $usuarioTelefon"
        }

        actualizarUI()

        findViewById<TextView>(R.id.tvEditar).setOnClickListener {
            val dialogView = layoutInflater.inflate(R.layout.dialog_editar_perfil, null)
            val etNombre = dialogView.findViewById<android.widget.EditText>(R.id.etEditNombre)
            val etEmpresa = dialogView.findViewById<android.widget.EditText>(R.id.etEditEmpresa)
            val etTelefon = dialogView.findViewById<android.widget.EditText>(R.id.etEditTelefon)

            // Rellenar con valores actuales
            etNombre.setText(usuarioNom)
            etEmpresa.setText(usuarioEmpresa)
            etTelefon.setText(usuarioTelefon)

            AlertDialog.Builder(this)
                .setTitle("Editar perfil")
                .setView(dialogView)
                .setPositiveButton("Guardar") { _, _ ->
                    val nuevoNom = etNombre.text.toString()
                    val nuevaEmpresa = etEmpresa.text.toString()
                    val nuevoTelefon = etTelefon.text.toString()

                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val request = UpdatePerfilRequest(nuevoNom, nuevaEmpresa, nuevoTelefon)
                            val response = RetrofitClient.api.updatePerfil(usuarioId, request)
                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    usuarioNom = nuevoNom
                                    usuarioEmpresa = nuevaEmpresa
                                    usuarioTelefon = nuevoTelefon
                                    actualizarUI()
                                    android.widget.Toast.makeText(this@Perfil, "Perfil actualizado", android.widget.Toast.LENGTH_SHORT).show()
                                } else {
                                    android.widget.Toast.makeText(this@Perfil, "Error al actualizar", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) {
                                android.widget.Toast.makeText(this@Perfil, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                .setNegativeButton("Cancelar", null)
                .show()
        }


        findViewById<Button>(R.id.btnSubirDNI).setOnClickListener {
            findViewById<Button>(R.id.btnSubirDNI).isEnabled = false
            pickImageLauncher.launch("image/*")
        }


        val btnDescargar = findViewById<Button>(R.id.btnDescargarDNI)

        btnDescargar.setOnClickListener {
            btnDescargar.isEnabled = false
            btnDescargar.text = "Descargando..."

            val claveBase64 = obtenerClave(usuarioId)
            Log.d("Perfil", "🔑 Clave recuperada para usuario $usuarioId: $claveBase64")
            if (claveBase64 == null) {
                Toast.makeText(
                    this,
                    "No tienes clave guardada para este usuario",
                    Toast.LENGTH_LONG
                ).show()
                btnDescargar.isEnabled = true
                btnDescargar.text = "Descargar DNI"
                return@setOnClickListener
            }

            CoroutineScope(Dispatchers.IO).launch {
                val bytesImagen = DniSocketClient.bajarDNI(usuarioId, claveBase64)
                withContext(Dispatchers.Main) {
                    btnDescargar.isEnabled = true
                    btnDescargar.text = "Descargar DNI"

                    if (bytesImagen != null) {
                        val bitmap = BitmapFactory.decodeByteArray(bytesImagen, 0, bytesImagen.size)
                        val imageView = ImageView(this@Perfil).apply {
                            setImageBitmap(bitmap)
                            adjustViewBounds = true
                        }
                        AlertDialog.Builder(this@Perfil)
                            .setTitle("Tu DNI")
                            .setView(imageView)
                            .setPositiveButton("Cerrar", null)
                            .setNeutralButton("Guardar en galería") { _, _ ->
                                guardarEnGaleria(bitmap)
                            }
                            .show()
                    } else {
                        Toast.makeText(this@Perfil, "No se pudo obtener el DNI", Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
        }


        findViewById<ImageButton>(R.id.btnAtras).setOnClickListener { finish() }

        findViewById<ImageButton>(R.id.btnExit).setOnClickListener {
            AlertDialog.Builder(this).setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de querer cerrar la sesión?")
                .setPositiveButton("Sí") { _, _ ->
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                    startActivity(intent)
                }.setNegativeButton("No", null).show()
        }
    }

    // Guarda la clave en SharedPreferences
    private fun guardarClave(usuarioId: Int, claveBase64: String) {
        val prefs = getSharedPreferences("dni_keys", MODE_PRIVATE)
        prefs.edit().putString("clave_$usuarioId", claveBase64).apply()
    }

    // Recupera la clave guardada
    private fun obtenerClave(usuarioId: Int): String? {
        val prefs = getSharedPreferences("dni_keys", MODE_PRIVATE)
        return prefs.getString("clave_$usuarioId", null)
    }

                private fun guardarEnGaleria(bitmap: Bitmap) {
                    val filename = "DNI_${usuarioId}_${System.currentTimeMillis()}.jpg"
                    val contentValues = android.content.ContentValues().apply {
                        put(android.provider.MediaStore.Images.Media.DISPLAY_NAME, filename)
                        put(android.provider.MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                        put(android.provider.MediaStore.Images.Media.RELATIVE_PATH, "Pictures/DNI")
                    }
                    val uri = contentResolver.insert(
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        contentValues
                    )
                    if (uri != null) {
                        contentResolver.openOutputStream(uri)?.use { out ->
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
                        }
                        Toast.makeText(this, "DNI guardado en Galería/Pictures/DNI", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Error al guardar en galería", Toast.LENGTH_SHORT).show()
                    }
                }

}