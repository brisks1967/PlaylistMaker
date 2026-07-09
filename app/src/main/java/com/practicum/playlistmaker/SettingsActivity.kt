package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import com.google.android.material.button.MaterialButton


class SettingsActivity : Activity()  {



        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_settings)


            // Обработка кнопки «Назад»
            val backButton = findViewById<MaterialButton>(R.id.btn_back)

            backButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

        }


}