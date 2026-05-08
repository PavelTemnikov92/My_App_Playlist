package com.practicum.myapp.data.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Простой сервис iTunes Search API.
 * Пример запроса: https://itunes.apple.com/search?term=beatles&limit=25
 */
interface ITunesService {
    @GET("search")
    suspend fun searchTracks(
        @Query("term") term: String,
        @Query("limit") limit: Int = 25
    ): ITunesResponse
}

// Ответ API – минимальная модель, достаточная для примера
data class ITunesResponse(
    val resultCount: Int,
    val results: List<ITunesTrackDto>
)

data class ITunesTrackDto(
    val trackId: Long,
    val trackName: String,
    val artistName: String,
    val trackTimeMillis: Long,
    val artworkUrl100: String?
)