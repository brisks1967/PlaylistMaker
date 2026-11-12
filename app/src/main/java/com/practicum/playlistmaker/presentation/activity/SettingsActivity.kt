package com.practicum.playlistmaker.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.presentation.app.App
import com.practicum.playlistmaker.presentation.app.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity()  {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
            setContentView(binding.root)

            // получаем состояние темы через репозиторий
           val isDark = Creator.createThemeInteractor(this)

            // Обработка кнопки «Назад»
        //    val backButton = findViewById<LinearLayout>(R.id.btn_settings)
            val backButton = binding.btnSettings

            backButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                finish()
            }

           // Обработка кнопки «Темная тема»

           //  val themeSwitcher = findViewById<SwitchMaterial>(R.id.themeSwitch)
           val themeSwitcher = binding.themeSwitch


            val isDarkThemeSaved = isDark.isDarkThemeEnabled()

            themeSwitcher.setChecked(isDarkThemeSaved)

            themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
                (application as App).switchTheme(checked)
                recreate()
            }

            // Обработка кнопки «Поделиться приложением»
           // val sendButton: LinearLayout = findViewById<LinearLayout>(R.id.btn_send)
            val sendButton: LinearLayout = binding.btnSend

            sendButton.setOnClickListener {
                shareApp()
            }

            // Обработка кнопки «Написать в поддержку»
           //  val sendLetter: LinearLayout = findViewById<LinearLayout>(R.id.btn_letter)
           val sendLetter: LinearLayout = binding.btnLetter

            sendLetter.setOnClickListener {
                writeLetter()
            }

            // Обработка кнопки «Пользовательское соглашение»
           // val acceptEU: LinearLayout = findViewById<LinearLayout>(R.id.btn_accept)
           val acceptEU: LinearLayout = binding.btnAccept

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