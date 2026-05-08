package com.practicum.myapp.domain.interactors

import com.practicum.myapp.Track

interface IAddTrackInteractor {
    suspend fun addTrack(track: Track)
}
