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
import android.widget.Button
import android.widget.ImageView

class Perfil : AppCompatActivity() {

    private var dniBase64: String? = null
    private var usuarioId: Int = -1

    // Launcher para seleccionar imagen de galería
    // Launcher - ahora sube automáticamente al seleccionar
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        uri?.let {
            val original = MediaStore.Images.Media.getBitmap(contentResolver, it)

            // Reducir tamaño antes de convertir
            val scaled = Bitmap.createScaledBitmap(original, 800, 600, true)

            val outputStream = ByteArrayOutputStream()
            scaled.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)  // calidad 60%
            val base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)  // NO_WRAP evita saltos de línea

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val request = UpdatePerfilRequest(dniFoto = base64)
                    val response = RetrofitClient.api.updateDni(usuarioId, request)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            android.widget.Toast.makeText(this@Perfil, "DNI subido correctamente", android.widget.Toast.LENGTH_SHORT).show()
                        } else {
                            android.widget.Toast.makeText(this@Perfil, "Error del servidor: ${response.code()}", android.widget.Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(this@Perfil, "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                    }
                }
            }
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
            pickImageLauncher.launch("image/*")
        }


        findViewById<Button>(R.id.btnDescargarDNI).setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val response = RetrofitClient.api.getDni(usuarioId)
                    withContext(Dispatchers.Main) {
                        if (response.isSuccessful) {
                            val base64 = response.body()?.dniFoto
                            if (base64 != null) {
                                val bytes = Base64.decode(base64, Base64.DEFAULT)
                                val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

                                // Guardar en galería
                                val nombreArchivo = "DNI_${usuarioId}_${System.currentTimeMillis()}"
                                val savedUri = MediaStore.Images.Media.insertImage(
                                    contentResolver,
                                    bitmap,
                                    nombreArchivo,
                                    "Foto DNI"
                                )

                                // Mostrar dialog con imagen Y botón de descarga
                                val imageView = ImageView(this@Perfil)
                                imageView.setImageBitmap(bitmap)
                                imageView.adjustViewBounds = true

                                AlertDialog.Builder(this@Perfil)
                                    .setTitle("Tu DNI")
                                    .setView(imageView)
                                    .setPositiveButton("Cerrar", null)
                                    .setNeutralButton("Guardar en galería") { _, _ ->
                                        if (savedUri != null) {
                                            android.widget.Toast.makeText(
                                                this@Perfil,
                                                "DNI guardado en galería",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        } else {
                                            android.widget.Toast.makeText(
                                                this@Perfil,
                                                "Error al guardar",
                                                android.widget.Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                    .show()
                            } else {
                                android.widget.Toast.makeText(this@Perfil, "No hay DNI guardado", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        } else {
                            android.widget.Toast.makeText(this@Perfil, "DNI no encontrado", android.widget.Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        android.widget.Toast.makeText(this@Perfil, "Error: ${e.message}", android.widget.Toast.LENGTH_SHORT).show()
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
}