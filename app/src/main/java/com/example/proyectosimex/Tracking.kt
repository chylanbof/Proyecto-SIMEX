package com.example.proyectosimex

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Tracking : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_tracking)

        val header = findViewById<View>(R.id.layoutDeArriba)
        header.findViewById<ImageView>(R.id.imgBackgroundHeader).setImageResource(R.drawable.tracking)

        header.findViewById<TextView>(R.id.txtHeaderTitle).setText("Tracking")

        findViewById<ImageButton>(R.id.btnAtras).setOnClickListener {
            finish()
        }
    }
}