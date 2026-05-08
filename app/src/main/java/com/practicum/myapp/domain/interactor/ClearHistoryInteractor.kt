package com.practicum.myapp.domain.interactor

import com.practicum.myapp.domain.repositories.HistoryRepository

/**
 * Интерактор для очистки истории прослушиваний.
 */
class ClearHistoryInteractor(
    private val historyRepository: HistoryRepository
) {
    operator fun invoke() {
        historyRepository.clearHistory()
    }
}
