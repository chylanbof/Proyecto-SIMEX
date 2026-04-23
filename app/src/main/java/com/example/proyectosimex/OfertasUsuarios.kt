package com.example.proyectosimex


import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyectosimex.fragments.UsuariosOfertasFragment

class OfertasUsuarios : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_ofertas_clientes)

        val usuarioId = intent.getIntExtra("usuario_id", -1)

        val header = findViewById<View>(R.id.LayoutOfertasClientes)
        header.findViewById<ImageView>(R.id.imgBackgroundHeader).setImageResource(R.drawable.oferta)

        header.findViewById<TextView>(R.id.txtHeaderTitle).setText("Historial")

        findViewById<ImageButton>(R.id.btnAtras).setOnClickListener {
            finish()
        }

        if (savedInstanceState == null) {
            val fragment = UsuariosOfertasFragment()
            val bundle = Bundle()
            bundle.putInt("usuario_id", usuarioId)
            fragment.arguments = bundle

            supportFragmentManager.beginTransaction()
                .replace(R.id.FramgmentContainerOfertasCliente, fragment)
                .commit()
        }
    }

    fun actualizarTitulosHeader(nuevoTitulo: String){
        //buscamos el textView dentro del layout incluido
        val txtHeader = findViewById<TextView>(R.id.txtHeaderTitle)
        txtHeader?.text = nuevoTitulo
    }

}