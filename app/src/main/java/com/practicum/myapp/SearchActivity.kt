package com.practicum.myapp

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewTreeObserver
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.myapp.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchActivity : AppCompatActivity() {
    private lateinit var searchEditText: EditText
    private lateinit var clearSearchButton: ImageButton
    private lateinit var searchResultsRecyclerView: RecyclerView
    private lateinit var backButton: ImageButton
    private lateinit var retryButton: Button
    private lateinit var trackAdapter: TrackAdapter
    private lateinit var historyAdapter: TrackAdapter

    // Заглушки
    private lateinit var emptyResultLayout: LinearLayout
    private lateinit var errorLayout: LinearLayout

    // История
    private lateinit var historySection: LinearLayout
    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var clearHistoryButton: ImageButton

    // Подсказка
    private lateinit var searchHint: TextView

    private var searchText: String = ""
    private var isSearching = false
    private var lastSearchQuery: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        initViews()
        setupRecyclerView()
        setupClickListeners()
        setupKeyboardListener()

        // Запрашиваем фокус для поля поиска и показываем клавиатуру
        searchEditText.requestFocus()
        showKeyboard()
    }

    private fun showKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        searchEditText.postDelayed({
            imm.showSoftInput(searchEditText, InputMethodManager.SHOW_IMPLICIT)
        }, 100)
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.searchEditText)
        clearSearchButton = findViewById(R.id.clearSearchButton)
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)
        backButton = findViewById(R.id.backButton)
        retryButton = findViewById(R.id.retryButton)
        emptyResultLayout = findViewById(R.id.emptyResultLayout)
        errorLayout = findViewById(R.id.errorLayout)

        // История
        historySection = findViewById(R.id.historySection)
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)

        // Подсказка
        searchHint = findViewById(R.id.searchHint)
    }

    private fun setupRecyclerView() {
        // Адаптер для результатов поиска
        trackAdapter = TrackAdapter { track ->
            // Добавление трека в историю при клике
            HistoryManager.addTrack(track)
            loadHistory() // Обновить историю
        }
        searchResultsRecyclerView.adapter = trackAdapter
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        
        // Адаптер для истории
        historyAdapter = TrackAdapter { track ->
            // При клике на трек в истории - добавляем его снова (перемещаем вверх)
            HistoryManager.addTrack(track)
            loadHistory()
        }
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        // Обработка нажатия на кнопку Done на клавиатуре
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val query = searchEditText.text.toString()
                if (query.isNotBlank()) {
                    performSearch(query)
                    // Скрыть клавиатуру
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
                }
                true
            } else {
                false
            }
        }

        // TextWatcher для отслеживания текста
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                clearSearchButton.visibility = if (p0.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                searchText = p0?.toString() ?: ""
                
                // Если поле в фокусе, обновляем видимость подсказки и истории
                if (searchEditText.hasFocus()) {
                    if (p0.isNullOrEmpty()) {
                        searchHint.visibility = View.VISIBLE
                        loadHistory()
                    } else {
                        searchHint.visibility = View.GONE
                        hideHistorySection()
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }
        })

        // Отслеживание фокуса поля поиска
        searchEditText.setOnFocusChangeListener { _, hasFocus ->
            val isTextEmpty = searchEditText.text.isNullOrEmpty()
            
            // Подсказка и история показываются, когда поле в фокусе и текст пустой
            if (hasFocus && isTextEmpty) {
                searchHint.visibility = View.VISIBLE
                loadHistory()
            } else {
                searchHint.visibility = View.GONE
                hideHistorySection()
            }
        }

        // Кнопка очистки
        clearSearchButton.setOnClickListener {
            searchEditText.setText("")
            // Скрыть клавиатуру
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(searchEditText.windowToken, 0)
            // Скрыть кнопку очистки
            clearSearchButton.visibility = View.GONE
            // Очистить список результатов
            trackAdapter.updateTracks(emptyList())
            // Показать RecyclerView (пустой)
            showRecyclerView()
            // Сбросить последний поисковый запрос
            lastSearchQuery = ""
            // Показать историю, так как поле пустое и в фокусе
            val isTextEmpty = searchEditText.text.isNullOrEmpty()
            if (searchEditText.hasFocus() && isTextEmpty) {
                searchHint.visibility = View.VISIBLE
                loadHistory()
            } else {
                searchHint.visibility = View.GONE
                hideHistorySection()
            }
        }

        // Кнопка повторной попытки
        retryButton.setOnClickListener {
            if (lastSearchQuery.isNotBlank()) {
                performSearch(lastSearchQuery)
            }
        }

        // Кнопка назад
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        // Кнопка очистки истории
        clearHistoryButton.setOnClickListener {
            HistoryManager.clearHistory()
            hideHistorySection()
        }

        updateClearButtonVisibility()
    }

    private fun setupKeyboardListener() {
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener(keyboardLayoutListener)
    }

    private fun updateClearButtonVisibility() {
        clearSearchButton.visibility = if (searchEditText.text.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun performSearch(query: String) {
        if (query.isBlank()) {
            trackAdapter.updateTracks(emptyList())
            showRecyclerView()
            return
        }

        if (isSearching) return
        isSearching = true
        lastSearchQuery = query

        showRecyclerView()

        RetrofitClient.itunesApi.search(query).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(call: Call<ItunesResponse>, response: Response<ItunesResponse>) {
                isSearching = false

                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()?.results ?: emptyList()

                    if (results.isEmpty()) {
                        showEmptyResult()
                    } else {
                        trackAdapter.updateTracks(results)
                        showRecyclerView()
                    }
                } else {
                    showError()
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                isSearching = false
                showError()
            }
        })
    }

    private fun showRecyclerView() {
        searchResultsRecyclerView.visibility = View.VISIBLE
        emptyResultLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
    }

    private fun showEmptyResult() {
        searchResultsRecyclerView.visibility = View.GONE
        emptyResultLayout.visibility = View.VISIBLE
        errorLayout.visibility = View.GONE
    }

    private fun showError() {
        searchResultsRecyclerView.visibility = View.GONE
        emptyResultLayout.visibility = View.GONE
        errorLayout.visibility = View.VISIBLE
    }
    
    private fun loadHistory() {
        val history = HistoryManager.getHistory()

        if (history.isEmpty()) {
            historySection.visibility = View.GONE
        } else {
            historySection.visibility = View.VISIBLE
            historyAdapter.updateTracks(history.map { it.toTrack() })
        }
    }

    private fun hideHistorySection() {
        historySection.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        // Обновляем видимость подсказки и истории
        val hasFocus = searchEditText.hasFocus()
        val isTextEmpty = searchEditText.text.isNullOrEmpty()
        
        searchHint.visibility = if (hasFocus && isTextEmpty) {
            View.VISIBLE
        } else {
            View.GONE
        }
        
        if (hasFocus && isTextEmpty) {
            loadHistory()
        } else {
            hideHistorySection()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("search_text", searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString("search_text", "")
        searchEditText.setText(searchText)
        updateClearButtonVisibility()
    }

    override fun onDestroy() {
        super.onDestroy()
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
            val currentHeight = rect.height()

            if (initialHeight == 0) {
                initialHeight = currentHeight
            }

            val heightDifference = initialHeight - currentHeight
            val keyboardThreshold = 200

            val isKeyboardDetected = heightDifference > keyboardThreshold

            if (isKeyboardDetected && !isKeyboardShowing) {
                isKeyboardShowing = true
            } else if (!isKeyboardDetected && isKeyboardShowing) {
                isKeyboardShowing = false
            }
        }
    }
}
