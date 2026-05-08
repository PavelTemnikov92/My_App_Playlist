package com.practicum.myapp.domain.interactor

import com.practicum.myapp.domain.model.Track
import com.practicum.myapp.domain.repositories.TrackRepository

/**
 * Интерактор для поиска треков по запросу.
 */
class SearchTracksInteractor(
    private val trackRepository: TrackRepository
) {
    suspend operator fun invoke(query: String): List<Track> {
        val apiTracks = trackRepository.searchTracks(query)
        // Convert API Track to domain Track
        return apiTracks.map { apiTrack ->
            Track(
                trackId = apiTrack.trackId,
                trackName = apiTrack.trackName,
                artistName = apiTrack.artistName,
                trackTimeMillis = apiTrack.trackTimeMillis,
                artworkUrl100 = apiTrack.artworkUrl100,
                collectionName = apiTrack.collectionName,
                releaseDate = apiTrack.releaseDate,
                primaryGenreName = apiTrack.primaryGenreName,
                country = apiTrack.country,
                previewUrl = apiTrack.previewUrl
            )
        }
    }
}
