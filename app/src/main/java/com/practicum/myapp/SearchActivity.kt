package com.practicum.myapp

import android.os.Bundle
import android.text.TextWatcher
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    private lateinit var backButton: ImageButton
    private lateinit var searchEditText: EditText
    private lateinit var clearSearchButton: ImageButton
    private lateinit var searchResultsRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupClickListeners()
    }

    private fun initViews() {
        backButton = findViewById(R.id.backButton)
        searchEditText = findViewById(R.id.searchEditText)
        clearSearchButton = findViewById(R.id.clearSearchButton)

    }

    private fun setupClickListeners() {
        // Обработка нажатия на кнопку "Назад"
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Обработка изменения текста в поле поиска
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Показать/скрыть кнопку очистки в зависимости от наличия текста
                clearSearchButton.visibility = if (s.isNullOrEmpty()) {
                    android.view.View.GONE
                } else {
                    android.view.View.VISIBLE
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {
                // Выполнить поиск при изменении текста
                performSearch(s.toString())
            }
        })

        // Обработка нажатия на кнопку очистки
        clearSearchButton.setOnClickListener {
            searchEditText.setText("")
        }
    }

    private fun performSearch(query: String) {
        // Здесь будет реализация поиска
        // В реальном приложении здесь будет фильтрация данных
    }
}