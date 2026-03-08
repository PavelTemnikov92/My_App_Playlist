package com.practicum.myapp

import com.google.gson.annotations.SerializedName

data class HistoryTrack(
    @SerializedName("trackId")
    val trackId: Long,
    
    @SerializedName("trackName")
    val trackName: String,
    
    @SerializedName("artistName")
    val artistName: String,
    
    @SerializedName("trackTimeMillis")
    val trackTimeMillis: Long,
    
    @SerializedName("artworkUrl100")
    val artworkUrl100: String
) {
    // Вспомогательное свойство для отображения времени в формате mm:ss
    val trackTime: String
        get() {
            val minutes = trackTimeMillis / 60000
            val seconds = (trackTimeMillis % 60000) / 1000
            return String.format("%02d:%02d", minutes, seconds)
        }
    
    // Конвертация в Track
    fun toTrack(): Track {
        return Track(
            trackId = trackId,
            trackName = trackName,
            artistName = artistName,
            trackTimeMillis = trackTimeMillis,
            artworkUrl100 = artworkUrl100
        )
    }
    
    companion object {
        fun fromTrack(track: Track): HistoryTrack {
            return HistoryTrack(
                trackId = track.trackId ?: 0L,
                trackName = track.trackName ?: "",
                artistName = track.artistName ?: "",
                trackTimeMillis = track.trackTimeMillis ?: 0L,
                artworkUrl100 = track.artworkUrl100 ?: ""
            )
        }
    }
}
