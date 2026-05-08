package com.practicum.myapp.presentation.ui

import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.practicum.myapp.R
import com.practicum.myapp.data.repository.HistoryRepositoryImpl

class HistoryActivity : AppCompatActivity() {

    private lateinit var historyRecyclerView: RecyclerView
    private lateinit var emptyHistoryLayout: LinearLayout
    private lateinit var backButton: ImageButton
    private lateinit var clearHistoryButton: ImageButton
    private lateinit var historyAdapter: TrackAdapter
    
    private val historyRepository = HistoryRepositoryImpl(applicationContext)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        initViews()
        setupRecyclerView()
        setupClickListeners()
        loadHistory()
    }

    private fun initViews() {
        historyRecyclerView = findViewById(R.id.historyRecyclerView)
        emptyHistoryLayout = findViewById(R.id.emptyHistoryLayout)
        backButton = findViewById(R.id.backButton)
        clearHistoryButton = findViewById(R.id.clearHistoryButton)
    }

    private fun setupRecyclerView() {
        historyAdapter = TrackAdapter { track ->
            // При клике на трек в истории – добавляем его снова (перемещаем вверх)
            historyRepository.addTrack(track)
            loadHistory() // Обновляем список
        }
        historyRecyclerView.adapter = historyAdapter
        historyRecyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupClickListeners() {
        backButton.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        clearHistoryButton.setOnClickListener {
            historyRepository.clearHistory()
            loadHistory()
        }
    }

    private fun loadHistory() {
        val history = historyRepository.getHistory()

        if (history.isEmpty()) {
            historyRecyclerView.visibility = View.GONE
            emptyHistoryLayout.visibility = View.VISIBLE
        } else {
            historyRecyclerView.visibility = View.VISIBLE
            emptyHistoryLayout.visibility = View.GONE
            historyAdapter.updateTracks(history.map { it.toTrack() })
        }
    }
}
