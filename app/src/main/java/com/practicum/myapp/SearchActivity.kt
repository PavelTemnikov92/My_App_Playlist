package com.practicum.myapp

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewTreeObserver
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearSearchButton: ImageButton
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var backButton: ImageButton
    private var searchText: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupClickListeners()
        setupKeyboardListener()
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.searchEditText)
        clearSearchButton = findViewById(R.id.clearSearchButton)
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)
        backButton = findViewById(R.id.backButton)


    }

    private fun setupClickListeners() {
        // Добавляем новый TextWatcher с заглушкой для будущих задач
        val searchWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Заглушка для будущей функциональности
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Показать/скрыть кнопку очистки в зависимости от наличия текста
                clearSearchButton.visibility = if (s.isNullOrEmpty()) {
                    android.view.View.GONE
                } else {
                    android.view.View.VISIBLE
                }

                // Сохраняем текст в переменную
                searchText = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: android.text.Editable?) {
                // Выполнить поиск при изменении текста
                performSearch(s.toString())
            }
        }

        // Применяем TextWatcher к полю поиска
        searchEditText.addTextChangedListener(searchWatcher)

        // Обработка нажатия на кнопку очистки
        clearSearchButton.setOnClickListener {
            searchEditText.setText("")
            searchEditText.clearFocus()

            // Скрыть клавиатуру
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
        }

        // Обработка нажатия на кнопку назад
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Установить начальное состояние видимости кнопки очистки
        updateClearButtonVisibility()
    }

    private fun setupKeyboardListener() {
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
    }

    private fun updateClearButtonVisibility() {
        clearSearchButton.visibility = if (searchEditText.text.isNullOrEmpty()) {
            android.view.View.GONE
        } else {
            android.view.View.VISIBLE
        }
    }

    private fun performSearch(query: String) {
        // Здесь будет реализация поиска
        // В реальном приложении здесь будет фильтрация данных
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        // Сохраняем текст из EditText в Bundle
        outState.putString("search_text", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        // Достаем данные из Bundle
        searchText = savedInstanceState.getString("search_text", "")
        // Устанавливаем восстановленные данные обратно в EditText
        searchEditText.setText(searchText)
        // Обновляем видимость кнопки очистки в соответствии с восстановленным текстом
        updateClearButtonVisibility()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Удаляем слушатель чтобы избежать утечки памяти
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.removeOnGlobalLayoutListener(keyboardLayoutListener)
    }

    private val keyboardLayoutListener = object : ViewTreeObserver.OnGlobalLayoutListener {
        private var isKeyboardShowing = false
        private var initialHeight = 0

        override fun onGlobalLayout() {
            val rootView = findViewById<View>(android.R.id.content)
            val rect = Rect()
            rootView.getWindowVisibleDisplayFrame(rect)

            // При использовании adjustResize getWindowVisibleDisplayFrame дает размеры окна без учета клавиатуры
            // Поэтому мы будем отслеживать изменение высоты окна
            val currentHeight = rect.height()

            // Устанавливаем начальную высоту при первом вызове
            if (initialHeight == 0) {
                initialHeight = currentHeight
            }

            // Определяем, появилась ли клавиатура, по изменению высоты окна
            // Если высота уменьшилась значительно (например, более чем на 200px), значит клавиатура появилась
            val heightDifference = initialHeight - currentHeight
            val keyboardThreshold = 200 // порог, при котором считаем, что клавиатура появилась

            val isKeyboardDetected = heightDifference > keyboardThreshold

            if (isKeyboardDetected && !isKeyboardShowing) {
                // Клавиатура только что появилась
                isKeyboardShowing = true
            } else if (!isKeyboardDetected && isKeyboardShowing) {
                // Клавиатура исчезла
                isKeyboardShowing = false
            }
        }
    }
}