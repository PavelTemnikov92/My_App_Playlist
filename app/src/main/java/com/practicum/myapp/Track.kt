package com.practicum.myapp

import com.google.gson.annotations.SerializedName

data class Track(
    @SerializedName("trackId")
    val trackId: Long? = null,
    
    @SerializedName("trackName")
    val trackName: String? = null,
    
    @SerializedName("artistName")
    val artistName: String? = null,
    
    @SerializedName("trackTimeMillis")
    val trackTimeMillis: Long? = null,
    
    @SerializedName("artworkUrl100")
    val artworkUrl100: String? = null
) {
    // Вспомогательное свойство для отображения времени в формате mm:ss
    val trackTime: String
        get() {
            val millis = trackTimeMillis ?: 0L
            val minutes = millis / 60000
            val seconds = (millis % 60000) / 1000
            return String.format("%02d:%02d", minutes, seconds)
        }
}

// Data class для ответа от iTunes API
data class ItunesResponse(
    @SerializedName("resultCount")
    val resultCount: Int? = null,
    
    @SerializedName("results")
    val results: List<Track>? = null
)
