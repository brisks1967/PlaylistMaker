package com.practicum.playlistmaker.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Запуск Activity  - Search  - МЕТОД 1
    //    val screenSearch = findViewById<Button>(R.id.search)
        val screenSearch = binding.search
        val screenSearchClickListener: View.OnClickListener = object : View.OnClickListener {
            override fun onClick(v: View?) {
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                // Запускаем новую Activity Search
                startActivity(intent)
            }
        }

        screenSearch.setOnClickListener(screenSearchClickListener)

// Запуск Activity  - Library - Lib  - МЕТОД 2
        // val screenLibrary = findViewById<Button>(R.id.lib)
        val screenLibrary = binding.lib
        screenLibrary.setOnClickListener {
            val intent = Intent(this@MainActivity, LibActivity::class.java)
            // Запускаем новую Activity Library - Lib
            startActivity(intent)
        }

// Запуск Activity  - Settings  - МЕТОД 2
        // val screenSettings = findViewById<Button>(R.id.settings)
        val screenSettings = binding.settings
        screenSettings.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            // Запускаем новую Activity Settings
            startActivity(intent)
        }
    }
}