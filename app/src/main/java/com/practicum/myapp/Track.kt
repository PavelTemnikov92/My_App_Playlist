package com.practicum.myapp

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
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
    val artworkUrl100: String? = null,

    @SerializedName("collectionName")
    val collectionName: String? = null,

    @SerializedName("releaseDate")
    val releaseDate: String? = null,

    @SerializedName("primaryGenreName")
    val primaryGenreName: String? = null,

    @SerializedName("country")
    val country: String? = null,

    @SerializedName("previewUrl")
    val previewUrl: String? = null
) : Parcelable {
    // Вспомогательное свойство для отображения времени в формате mm:ss
    val trackTime: String
        get() {
            val millis = trackTimeMillis ?: 0L
            val minutes = millis / 60000
            val seconds = (millis % 60000) / 1000
            return String.format("%02d:%02d", minutes, seconds)
        }

    // Вспомогательное свойство для получения года из releaseDate
    val releaseYear: String?
        get() {
            return releaseDate?.take(4)
        }

    // Функция для получения ссылки на обложку в высоком качестве
    fun getCoverArtwork(): String? {
        return artworkUrl100?.replaceAfterLast('/', "512x512bb.jpg")
    }
}

// Data class для ответа от iTunes API
data class ItunesResponse(
    @SerializedName("resultCount")
    val resultCount: Int? = null,
    
    @SerializedName("results")
    val results: List<Track>? = null
)
