package com.practicum.playlistmaker

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class SearchActivity : AppCompatActivity() {
    var saveText : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        // Обработка кнопки «Назад»
        val backButton = findViewById<LinearLayout>(R.id.containerSearchBack)

        backButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            finish()
        }


        val linearLayout = findViewById<LinearLayout>(R.id.searchField2)
        val inputEditText = findViewById<EditText>(R.id.inputEditText)
        val clearButton = findViewById<ImageView>(R.id.clearIcon)


        clearButton.setOnClickListener {
            inputEditText.setText("")
         clearButton.visibility = View.GONE

          // Запускаем обновление через TextWatcher
        }

        val simpleTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // empty
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // empty
            }

            override fun afterTextChanged(s: Editable?) {
                // Изменение фона поисковой строки - начало блока
                if (s.isNullOrEmpty()) {
                    linearLayout.setBackgroundColor(getColor(R.color.neutral))
                } else {
                    linearLayout.setBackgroundColor(getColor(R.color.neutral))
                }
                // Изменение фона поисковой строки - конец блока
                saveText = s.toString()
                clearButton.visibility = clearButtonVisibility(s)
            }
        }

        inputEditText.addTextChangedListener(simpleTextWatcher)

    }

    private fun clearButtonVisibility(s: CharSequence?): Int {
        return if (s.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

        override fun onSaveInstanceState(outState: Bundle) {
            super.onSaveInstanceState(outState)
            outState.putString("EDIT_TEXT_KEY", saveText)
        }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)

        val restoredText = savedInstanceState.getString("EDIT_TEXT_KEY")
        val myEditText: EditText = findViewById(R.id.inputEditText)

        if (restoredText != null) {
            myEditText.setText(restoredText)
        }
    }


}






