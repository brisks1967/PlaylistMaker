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
import android.widget.Button
import android.widget.Toast
import com.google.android.material.button.MaterialButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)



        // Запуск Activity  - Search  - МЕТОД 1
        val screenSearch = findViewById<Button>(R.id.search)
        val screenSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {

                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                // Запускаем новую Activity Search
                startActivity(intent)
            }
        }

        screenSearch.setOnClickListener(screenSearchClickListener)

// Запуск Activity  - Library - Lib  - МЕТОД 2
        val screenLibrary = findViewById<Button>(R.id.lib)

        screenLibrary.setOnClickListener {
            val intent =  Intent(this@MainActivity, LibActivity::class.java)
            // Запускаем новую Activity Library - Lib
            startActivity(intent)
        }

// Запуск Activity  - Settings  - МЕТОД 2
        val screenSettings = findViewById<Button>(R.id.settings)

        screenSettings.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            // Запускаем новую Activity Settings
            startActivity(intent)
        }


    }

}