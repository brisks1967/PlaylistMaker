package com.practicum.playlistmaker.presentation.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityLibBinding
import com.practicum.playlistmaker.databinding.ActivityMainBinding


class LibActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLibBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}