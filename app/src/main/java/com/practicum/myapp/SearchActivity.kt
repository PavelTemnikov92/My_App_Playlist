package com.practicum.myapp

import android.content.Context
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

    // Заглушки
    private lateinit var emptyResultLayout: LinearLayout
    private lateinit var errorLayout: LinearLayout

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
    }

    private fun initViews() {
        searchEditText = findViewById(R.id.searchEditText)
        clearSearchButton = findViewById(R.id.clearSearchButton)
        searchResultsRecyclerView = findViewById(R.id.searchResultsRecyclerView)
        backButton = findViewById(R.id.backButton)
        retryButton = findViewById(R.id.retryButton)
        emptyResultLayout = findViewById(R.id.emptyResultLayout)
        errorLayout = findViewById(R.id.errorLayout)
    }

    private fun setupRecyclerView() {
        trackAdapter = TrackAdapter()
        searchResultsRecyclerView.adapter = trackAdapter
        searchResultsRecyclerView.layoutManager = LinearLayoutManager(this)
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
        val searchWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                clearSearchButton.visibility = if (s.isNullOrEmpty()) {
                    View.GONE
                } else {
                    View.VISIBLE
                }
                searchText = s?.toString() ?: ""
            }

            override fun afterTextChanged(s: Editable?) {}
        }
        searchEditText.addTextChangedListener(searchWatcher)

        // Кнопка очистки
        clearSearchButton.setOnClickListener {
            searchEditText.setText("")
            searchEditText.clearFocus()
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
