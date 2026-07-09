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

class MainActivity090726 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val image = findViewById<MaterialButton>(R.id.search)
        val imageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                Toast.makeText(this@MainActivity090726, "Нажали на картинку!", Toast.LENGTH_SHORT).show()
            }
        }

        image.setOnClickListener(imageClickListener)



        val image2 = findViewById<MaterialButton>(R.id.lib)

        image2.setOnClickListener {
            Toast.makeText(this@MainActivity090726, "Нажали на картинку #2!", Toast.LENGTH_SHORT).show()
        }



        val image3 = findViewById<MaterialButton>(R.id.settings)

        image3.setOnClickListener {
            Toast.makeText(this@MainActivity090726, "Нажали на картинку #3!", Toast.LENGTH_SHORT).show()
        }



    }

}