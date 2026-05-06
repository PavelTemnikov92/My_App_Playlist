package com.practicum.myapp.domain.interactor

import com.practicum.myapp.Track
import com.practicum.myapp.domain.repositories.HistoryRepository

/**
 * Интерактор для добавления трека в историю прослушиваний.
 */
class AddTrackInteractor(
    private val historyRepository: HistoryRepository
) {
    suspend operator fun invoke(track: Track) {
        historyRepository.addTrack(track)
    }
}
