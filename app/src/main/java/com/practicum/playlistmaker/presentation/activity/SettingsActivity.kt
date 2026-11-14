package com.practicum.playlistmaker.presentation.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.switchmaterial.SwitchMaterial
import com.practicum.playlistmaker.presentation.app.App
import com.practicum.playlistmaker.presentation.app.Creator
import com.practicum.playlistmaker.R
import com.practicum.playlistmaker.databinding.ActivityMainBinding
import com.practicum.playlistmaker.databinding.ActivitySettingsBinding
import com.practicum.playlistmaker.ui.ViewModel.SearchViewModel
import com.practicum.playlistmaker.ui.ViewModel.SettingsViewModel
import kotlin.getValue
import com.practicum.playlistmaker.ui.ViewModel.SettingsViewModelFactory



class SettingsActivity : AppCompatActivity()  {

    private lateinit var binding: ActivitySettingsBinding
    val viewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory(Creator.createThemeInteractor(this).getRepo())
    }

    override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
            setContentView(binding.root)
        val themeSwitcher = binding.themeSwitch

            // получаем состояние темы
        viewModel.themeState.observe(this) { isDark ->
            if (themeSwitcher.isChecked != isDark) {
                themeSwitcher.isChecked = isDark
            }
        }
        // грузим текущее состояние темы из viewModel
        viewModel.loadTheme()

            // Обработка кнопки «Назад»
        binding.btnSettings.setOnClickListener {
            finish()
        }

        // Обработка кнопки «Темная тема»
        themeSwitcher.setOnCheckedChangeListener { _, checked ->
             viewModel.setTheme(checked) // Сохраняем состояние темы в viewModel
            (application as App).switchTheme(checked) // Меняем тему
        }

        binding.btnSend.setOnClickListener { shareApp() } // Обработка кнопки «Поделиться приложением»
        binding.btnLetter.setOnClickListener { writeLetter() } // Обработка кнопки «Написать в поддержку»
        binding.btnAccept.setOnClickListener { readAcceptText() } // Обработка кнопки «Пользовательское соглашение»

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