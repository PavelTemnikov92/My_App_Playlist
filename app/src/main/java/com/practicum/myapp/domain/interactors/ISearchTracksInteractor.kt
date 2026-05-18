package com.practicum.myapp.domain.interactors

import com.practicum.myapp.Track

interface ISearchTracksInteractor {
    suspend fun search(query: String): List<Track>
    suspend fun getAll(): List<Track>
}
