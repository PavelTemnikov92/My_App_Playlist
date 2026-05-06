package com.practicum.myapp.domain.interactor

import com.practicum.myapp.Track
import com.practicum.myapp.domain.repositories.HistoryRepository
import kotlinx.coroutines.flow.Flow

/**
 * Интерактор для загрузки истории прослушиваний.
 */
class LoadHistoryInteractor(
    private val historyRepository: HistoryRepository
) {
    operator fun invoke(): Flow<List<Track>> {
        return historyRepository.getHistory()
    }
}
