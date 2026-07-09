package com.practicum.playlistmaker.archiv

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.ImageView
import androidx.appcompat.widget.AppCompatImageView
import android.view.View
import android.widget.Toast
import com.google.android.material.button.MaterialButton
import com.practicum.playlistmaker.R

class MainActivityold080726_2_lamda : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val image = findViewById<MaterialButton>(R.id.search)

        image.setOnClickListener {
            Toast.makeText(this@MainActivityold080726_2_lamda, "Нажали на картинку!", Toast.LENGTH_SHORT).show()
        }
    }

}