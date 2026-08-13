package com.practicum.playlistmaker

import android.os.Bundle
import android.app.Activity
import android.content.Context
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
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    var saveText : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)


        //Карусель записей  - начало

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        val tracks = mutableListOf(
            Track("Smells Like Teen Spirit","Nirvana","5:01","https://is5-ssl.mzstatic.com/image/thumb/Music115/v4/7b/58/c2/7b58c21a-2b51-2bb2-e59a-9bb9b96ad8c3/00602567924166.rgb.jpg/100x100bb.jpg"),
            Track("Billie Jean","Michael Jackson","4:35","https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/3d/9d/38/3d9d3811-71f0-3a0e-1ada-3004e56ff852/827969428726.jpg/100x100bb.jpg"),
            Track("Whole Lotta Love","Led Zeppelin","5:33","https://is2-ssl.mzstatic.com/image/thumb/Music62/v4/7e/17/e3/7e17e33f-2efa-2a36-e916-7f808576cf6b/mzm.fyigqcbs.jpg/100x100bb.jpg"),
            Track("Sweet Child O'Mine","Guns N' Roses","5:03","https://is5-ssl.mzstatic.com/image/thumb/Music125/v4/a0/4d/c4/a04dc484-03cc-02aa-fa82-5334fcb4bc16/18UMGIM24878.rgb.jpg/100x100bb.jpg")
        )

        val trackadapter=TrackAdapter (tracks)

        recyclerView.adapter = trackadapter



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
                //  clearButton.visibility = View.GONE

            // Скрываем клавиатуру
           val inputMethodManager = getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(inputEditText.windowToken, 0)

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
          //      // Изменение фона поисковой строки - начало блока
          //      if (s.isNullOrEmpty()) {
          //          linearLayout.setBackgroundColor(getColor(R.color.neutral))
          //      } else {
          //          linearLayout.setBackgroundColor(getColor(R.color.neutral))
          //      }
          //     // Изменение фона поисковой строки - конец блока
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






