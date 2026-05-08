package com.practicum.myapp.domain.interactors

import com.practicum.myapp.Track

interface ILoadHistoryInteractor {
    suspend fun getHistory(): List<Track>
}
