package com.practicum.myapp.presentation.ui

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
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
import com.practicum.myapp.R
import com.practicum.myapp.api.RetrofitClient
import com.practicum.myapp.data.repository.HistoryRepositoryImpl
import com.practicum.myapp.ItunesResponse
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

    // Debounce для поискового запроса
    private val searchHandler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null
    private val SEARCH_DEBOUNCE_DELAY: Long = 2000L // 2 секунды

    // ProgressBar
    private lateinit var progressBar: com.google.android.material.progressindicator.CircularProgressIndicator
    
    // History Repository
    private val historyRepository = HistoryRepositoryImpl(applicationContext)

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
        progressBar = findViewById(R.id.progressBar)

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
            historyRepository.addTrack(track)
            // Скрыть историю после добавления трека
            hideHistorySection()
        }
        searchResultsRecyclerView.adapter = trackAdapter
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
        
        // Адаптер для истории
        historyAdapter = TrackAdapter { track ->
            // При клике на трек в истории - добавляем его снова (перемещаем вверх)
            historyRepository.addTrack(track)
            loadHistory()
        }
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        // Обработка нажатия на кнопку Done на клавиатуре
        searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                // Отменяем debounce запрос, если он был
                searchRunnable?.let {
                    searchHandler.removeCallbacks(it)
                }
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

        // TextWatcher для отслеживания текста с debounce
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

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

                // Debounce для поискового запроса
                performSearchWithDebounce(p0?.toString() ?: "")
            }

            override fun afterTextChanged(p0: Editable?) {}
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
            // Отменяем debounce запрос, если он был
            searchRunnable?.let {
                searchHandler.removeCallbacks(it)
            }
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
            // Отменяем debounce запрос, если он был
            searchRunnable?.let {
                searchHandler.removeCallbacks(it)
            }
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
            historyRepository.clearHistory()
            hideHistorySection()
        }

        updateClearButtonVisibility()
    }

    private fun setupKeyboardListener() {
        val rootView = findViewById<View>(android.R.id.content)
        rootView.viewTreeObserver.addOnGlobalLayoutListener {
            val rect = android.graphics.Rect()
            rootView.getWindowVisibleDisplayFrame(rect)
            val screenHeight = rootView.rootView.height
            val keypadHeight = screenHeight - rect.bottom
            
            if (keypadHeight > screenHeight * 0.15) {
                // Keyboard is showing
            } else {
                // Keyboard is hidden
            }
        }
    }

    private fun updateClearButtonVisibility() {
        clearSearchButton.visibility = if (searchEditText.text.isNullOrEmpty()) {
            View.GONE
        } else {
            View.VISIBLE
        }
    }

    private fun performSearchWithDebounce(query: String) {
        // Отменяем предыдущий запрос, если он был
        searchRunnable?.let {
            searchHandler.removeCallbacks(it)
        }

        // Если запрос пустой, скрываем результаты и показываем историю
        if (query.isBlank()) {
            trackAdapter.updateTracks(emptyList())
            showRecyclerView()
            lastSearchQuery = ""
            // Показать историю, так как поле пустое
            val isTextEmpty = searchEditText.text.isNullOrEmpty()
            if (searchEditText.hasFocus() && isTextEmpty) {
                searchHint.visibility = View.VISIBLE
                loadHistory()
            } else {
                searchHint.visibility = View.GONE
                hideHistorySection()
            }
            return
        }

        // Создаём новый отложенный запрос
        searchRunnable = Runnable {
            performSearch(query)
        }
        searchHandler.postDelayed(searchRunnable!!, SEARCH_DEBOUNCE_DELAY)
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
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

        // Показываем ProgressBar
        showProgressBar()

        // Скрываем историю при выполнении поиска
        hideHistorySection()
        showRecyclerView()

        RetrofitClient.itunesApi.search(query).enqueue(object : Callback<ItunesResponse> {
            override fun onResponse(call: Call<ItunesResponse>, response: Response<ItunesResponse>) {
                isSearching = false
                // Скрываем ProgressBar
                hideProgressBar()

                if (response.isSuccessful && response.body() != null) {
                    val results = response.body()?.results ?: emptyList()

                    if (results.isEmpty()) {
                        showEmptyResult()
                    } else {
                        // Convert API Track to domain Track
                        val domainTracks = results.map { apiTrack ->
                            com.practicum.myapp.domain.model.Track(
                                trackId = apiTrack.trackId,
                                trackName = apiTrack.trackName,
                                artistName = apiTrack.artistName,
                                trackTimeMillis = apiTrack.trackTimeMillis,
                                artworkUrl100 = apiTrack.artworkUrl100,
                                collectionName = apiTrack.collectionName,
                                releaseDate = apiTrack.releaseDate,
                                primaryGenreName = apiTrack.primaryGenreName,
                                country = apiTrack.country,
                                previewUrl = apiTrack.previewUrl
                            )
                        }
                        trackAdapter.updateTracks(domainTracks)
                        showRecyclerView()
                    }
                } else {
                    showError()
                }
            }

            override fun onFailure(call: Call<ItunesResponse>, t: Throwable) {
                isSearching = false
                // Скрываем ProgressBar
                hideProgressBar()
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
        val history = historyRepository.getHistory()

        // Скрываем результаты поиска при показе истории
        hideSearchResults()

        if (history.isEmpty()) {
            historySection.visibility = View.GONE
            // Если истории нет, показываем только подсказку
            searchHint.visibility = View.VISIBLE
        } else {
            historySection.visibility = View.VISIBLE
            historyAdapter.updateTracks(history.map { it.toTrack() })
        }
    }

    private fun hideHistorySection() {
        historySection.visibility = View.GONE
    }

    private fun hideSearchResults() {
        searchResultsRecyclerView.visibility = View.GONE
        emptyResultLayout.visibility = View.GONE
        errorLayout.visibility = View.GONE
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
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        searchRunnable?.let {
            searchHandler.removeCallbacks(it)
        }
    }
}
