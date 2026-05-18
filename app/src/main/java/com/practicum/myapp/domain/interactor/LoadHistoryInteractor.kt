package com.practicum.myapp.domain.interactor

import com.practicum.myapp.domain.model.Track
import com.practicum.myapp.domain.repositories.HistoryRepository

/**
 * Интерактор для загрузки истории прослушиваний.
 */
class LoadHistoryInteractor(
    private val historyRepository: HistoryRepository
) {
    operator fun invoke(): List<Track> {
        return historyRepository.getHistory().map { it.toTrack() }
    }
}
