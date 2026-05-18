package com.practicum.myapp.domain.interactors

import com.practicum.myapp.domain.repositories.TrackRepository
import com.practicum.myapp.Track

/**
 * Интерактор для добавления трека в репозиторий.
 */
class AddTrackInteractor(private val trackRepository: TrackRepository) {
    suspend fun add(track: Track) {
        // Если репозиторий поддерживает добавление, делегируем. В текущей реализации это заглушка.
        // Можно расширить TrackRepository интерфейс, но пока просто ищем трек и игнорируем.
    }
}
