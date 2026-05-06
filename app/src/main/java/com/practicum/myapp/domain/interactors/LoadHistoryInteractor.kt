package com.practicum.myapp.domain.interactors

import com.practicum.myapp.domain.repositories.HistoryRepository
import com.practicum.myapp.Track
import kotlinx.coroutines.flow.first

/**
 * Интерактор для загрузки истории прослушиваний.
 */
class LoadHistoryInteractor(private val historyRepository: HistoryRepository) {
    suspend fun getHistory(): List<Track> =
        historyRepository.getHistory().first()
}