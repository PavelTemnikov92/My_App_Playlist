package com.practicum.myapp.domain.interactors

import com.practicum.myapp.domain.repositories.HistoryRepository
import com.practicum.myapp.Track
import com.practicum.myapp.domain.model.HistoryTrack

/**
 * Интерактор для загрузки истории прослушиваний.
 */
class LoadHistoryInteractor(private val historyRepository: HistoryRepository) {
    fun getHistory(): List<HistoryTrack> =
        historyRepository.getHistory()
}