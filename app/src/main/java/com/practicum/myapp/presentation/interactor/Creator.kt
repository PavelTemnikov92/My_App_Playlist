package com.practicum.myapp.presentation.interactor

import android.app.Application
import com.practicum.myapp.domain.interactors.AddTrackInteractor
import com.practicum.myapp.domain.interactors.ClearHistoryInteractor
import com.practicum.myapp.domain.interactors.LoadHistoryInteractor
import com.practicum.myapp.domain.interactors.SearchTracksInteractor
import com.practicum.myapp.domain.repositories.HistoryRepository
import com.practicum.myapp.domain.repositories.TrackRepository
import com.practicum.myapp.data.repository.HistoryRepositoryImpl
import com.practicum.myapp.data.repository.TrackRepositoryImpl

/**
 * Фабрика для создания интеракторов и инициализации репозиториев.
 * Используется в классе {@link com.practicum.myapp.App} для настройки зависимостей
 * без прямой связи слоёв Presentation и Domain.
 */
object Creator {
    private lateinit var trackRepository: TrackRepository
    private lateinit var historyRepository: HistoryRepository

    /**
     * Инициализирует репозитории. Вызывается один раз из Application.onCreate().
     */
    fun init(app: Application) {
        // Реализации репозиториев находятся в слое Data
        trackRepository = TrackRepositoryImpl()
        historyRepository = HistoryRepositoryImpl(app.applicationContext)
    }

    fun getSearchTracksInteractor(): SearchTracksInteractor {
        return SearchTracksInteractor(trackRepository)
    }

    fun getLoadHistoryInteractor(): LoadHistoryInteractor {
        return LoadHistoryInteractor(historyRepository)
    }

    fun getAddTrackInteractor(): AddTrackInteractor {
        return AddTrackInteractor(trackRepository)
    }

    fun getClearHistoryInteractor(): ClearHistoryInteractor {
        return ClearHistoryInteractor(historyRepository)
    }
}
