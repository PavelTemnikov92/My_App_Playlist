package com.practicum.myapp.domain.interactors

import com.practicum.myapp.domain.repositories.HistoryRepository

/**
 * Интерактор для очистки истории прослушиваний.
 */
class ClearHistoryInteractor(private val historyRepository: HistoryRepository) {
    fun clear() {
        historyRepository.clearHistory()
    }
}