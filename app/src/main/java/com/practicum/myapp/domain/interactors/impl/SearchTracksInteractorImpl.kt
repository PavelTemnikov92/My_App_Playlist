package com.practicum.myapp.domain.interactors.impl

import com.practicum.myapp.domain.interactors.ISearchTracksInteractor
import com.practicum.myapp.domain.repositories.TrackRepository
import com.practicum.myapp.Track
import kotlinx.coroutines.flow.first

/**
 * Реализация {@link ISearchTracksInteractor}.
 */
class SearchTracksInteractorImpl(
    private val trackRepository: TrackRepository
) : ISearchTracksInteractor {
    override suspend fun search(query: String): List<Track> {
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

    override suspend fun getAll(): List<Track> {
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
