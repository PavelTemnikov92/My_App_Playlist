
package com.practicum.myapp.domain.interactors

import com.practicum.myapp.domain.repositories.TrackRepository
import com.practicum.myapp.Track
import kotlinx.coroutines.flow.first

/**
 * Интерактор для поиска треков.
 */
class SearchTracksInteractor(private val trackRepository: TrackRepository) {
    suspend fun search(query: String): List<Track> {
        val domainTracks = trackRepository.searchTracks(query)
        // Convert domain Track to API Track
        return domainTracks.map { domainTrack ->
            Track(
                trackId = domainTrack.trackId,
                trackName = domainTrack.trackName,
                artistName = domainTrack.artistName,
                trackTimeMillis = domainTrack.trackTimeMillis,
                artworkUrl100 = domainTrack.artworkUrl100,
                collectionName = domainTrack.collectionName,
                releaseDate = domainTrack.releaseDate,
                primaryGenreName = domainTrack.primaryGenreName,
                country = domainTrack.country,
                previewUrl = domainTrack.previewUrl
            )
        }
    }

    suspend fun getAll(): List<Track> {
        val domainTracks = trackRepository.getAllTracks().first()
        // Convert domain Track to API Track
        return domainTracks.map { domainTrack ->
            Track(
                trackId = domainTrack.trackId,
                trackName = domainTrack.trackName,
                artistName = domainTrack.artistName,
                trackTimeMillis = domainTrack.trackTimeMillis,
                artworkUrl100 = domainTrack.artworkUrl100,
                collectionName = domainTrack.collectionName,
                releaseDate = domainTrack.releaseDate,
                primaryGenreName = domainTrack.primaryGenreName,
                country = domainTrack.country,
                previewUrl = domainTrack.previewUrl
            )
        }
    }
}
