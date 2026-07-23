package com.practicum.playlistmaker

import androidx.appcompat.app.AppCompatActivity
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import com.google.android.material.button.MaterialButton


class SettingsActivity : Activity()  {



        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_settings)


            // Обработка кнопки «Назад»
            val backButton = findViewById<LinearLayout>(R.id.btn_settings)

            backButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                finish()
            }

            // Обработка кнопки «Поделиться приложением»
            val sendButton: LinearLayout = findViewById<LinearLayout>(R.id.btn_send)

            sendButton.setOnClickListener {
                shareApp()
            }

            // Обработка кнопки «Написать в поддержку»
            val sendLetter: LinearLayout = findViewById<LinearLayout>(R.id.btn_letter)

            sendLetter.setOnClickListener {
                writeLetter()
            }


            // Обработка кнопки «Пользовательское соглашение»
            val acceptEU: LinearLayout = findViewById<LinearLayout>(R.id.btn_accept)

            acceptEU.setOnClickListener {
                readAcceptText()
            }


        }

            private fun shareApp() {
                val letterIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, getString(R.string.sendLink))
                }
                startActivity(Intent.createChooser(letterIntent, getString(R.string.sendAcross)))
            }



            private fun writeLetter() {
                val writeSupportIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "message/rfc822" // тип для email
                        putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.email))) // адрес e mail
                        putExtra(Intent.EXTRA_SUBJECT, getString(R.string.writeSupport)) // тема письма
                        putExtra(Intent.EXTRA_TEXT, getString(R.string.writeSupport2)) // тело письма
                    }
                try {
                    startActivity(Intent.createChooser(writeSupportIntent , getString(R.string.send_email)))
                } catch (e: Exception) {
                    Toast.makeText(this, getString(R.string.errorLetter), Toast.LENGTH_LONG).show()
                }
            }

    private fun readAcceptText() {
        val url = getString(R.string.agreement)
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        startActivity(intent)
    }


}