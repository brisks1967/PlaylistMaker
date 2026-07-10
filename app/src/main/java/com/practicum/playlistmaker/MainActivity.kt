package com.practicum.playlistmaker

import android.content.Intent
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

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        val image = findViewById<MaterialButton>(R.id.search)
        val imageClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {

                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                // Запускаем новую Activity
                startActivity(intent)


            }
        }

        image.setOnClickListener(imageClickListener)


        val image3 = findViewById<MaterialButton>(R.id.lib)

        image3.setOnClickListener {
            val intent =  Intent(this@MainActivity, LibActivity::class.java)
            startActivity(intent)
        }


        val image2 = findViewById<MaterialButton>(R.id.settings)

        image2.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }


    }

}