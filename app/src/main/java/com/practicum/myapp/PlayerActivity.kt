package com.practicum.myapp

import android.content.Intent
import android.graphics.Color
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import java.io.IOException

class PlayerActivity : AppCompatActivity() {

    private lateinit var buttonBack: ImageButton
    private lateinit var buttonAddTrack: ImageButton
    private lateinit var buttonPlayPause: ImageButton
    private lateinit var buttonLike: ImageButton
    private lateinit var imageCover: ImageView
    private lateinit var textTrackName: TextView
    private lateinit var textArtistName: TextView
    private lateinit var textPlayTime: TextView
    private lateinit var textDurationValue: TextView
    private lateinit var textCollectionName: TextView
    private lateinit var textReleaseYear: TextView
    private lateinit var textGenre: TextView
    private lateinit var textCountry: TextView

    private val handler = Handler(Looper.getMainLooper())
    private var isPlaying = false
    private var isLiked = false
    private var currentTrack: Track? = null

    // MediaPlayer
    private var mediaPlayer: MediaPlayer? = null
    private var isMediaPlayerPrepared: Boolean = false
    private var playbackPosition = 0 // позиция в мс
    private var shouldStartOnPrepared = false // флаг: начать воспроизведение после подготовки

    // Runnable для обновления прогресса
    private val progressRunnable = object : Runnable {
        override fun run() {
            mediaPlayer?.let { mp ->
                if (isPlaying && isMediaPlayerPrepared) {
                    val currentPosition = mp.currentPosition
                    textPlayTime.text = formatTimeMs(currentPosition)
                    handler.postDelayed(this, PROGRESS_UPDATE_INTERVAL)
                }
            }
        }
    }

    private val PROGRESS_UPDATE_INTERVAL = 200L // обновление каждые 200мс

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        setupListeners()
        initMediaPlayer()
    }

    private fun initViews() {
        buttonBack = findViewById(R.id.backButton)
        buttonAddTrack = findViewById(R.id.button_add_track)
        buttonPlayPause = findViewById(R.id.button_play_pause)
        buttonLike = findViewById(R.id.button_like)
        imageCover = findViewById(R.id.image_cover)
        textTrackName = findViewById(R.id.text_track_name)
        textArtistName = findViewById(R.id.text_artist_name)
        textPlayTime = findViewById(R.id.text_play_time)
        textDurationValue = findViewById(R.id.text_duration_value)
        textCollectionName = findViewById(R.id.text_collection_name)
        textReleaseYear = findViewById(R.id.text_release_year)
        textGenre = findViewById(R.id.text_genre)
        textCountry = findViewById(R.id.text_country)

        // Получение данных трека из intent
        currentTrack = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            intent.getParcelableExtra("track", Track::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent.getParcelableExtra("track")
        }

        // Отображение данных трека
        displayTrackInfo()

        // Начальное состояние кнопки — Play
        updatePlayPauseIcon(isPlaying = false)
    }

    private fun initMediaPlayer() {
        val previewUrl = currentTrack?.previewUrl
        if (previewUrl.isNullOrBlank()) {
            return
        }

        val mp = MediaPlayer()
        mediaPlayer = mp

        try {
            mp.setDataSource(previewUrl)
            mp.setOnPreparedListener {
                this@PlayerActivity.isMediaPlayerPrepared = true
                if (this@PlayerActivity.shouldStartOnPrepared) {
                    this@PlayerActivity.shouldStartOnPrepared = false
                    this@PlayerActivity.performStart()
                } else if (this@PlayerActivity.playbackPosition > 0) {
                    mp.seekTo(this@PlayerActivity.playbackPosition)
                }
            }
            mp.setOnCompletionListener {
                this@PlayerActivity.isPlaying = false
                this@PlayerActivity.isMediaPlayerPrepared = false
                this@PlayerActivity.playbackPosition = 0
                this@PlayerActivity.shouldStartOnPrepared = false
                this@PlayerActivity.textPlayTime.text = formatTimeMs(0)
                this@PlayerActivity.updatePlayPauseIcon(isPlaying = false)
            }
            mp.setOnErrorListener { _, _, _ ->
                this@PlayerActivity.isPlaying = false
                this@PlayerActivity.isMediaPlayerPrepared = false
                this@PlayerActivity.playbackPosition = 0
                this@PlayerActivity.shouldStartOnPrepared = false
                this@PlayerActivity.textPlayTime.text = formatTimeMs(0)
                this@PlayerActivity.updatePlayPauseIcon(isPlaying = false)
                true
            }
            mp.prepareAsync()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun displayTrackInfo() {
        val track = currentTrack

        textTrackName.text = track?.trackName ?: getString(R.string.track_name_placeholder)
        textArtistName.text = track?.artistName ?: getString(R.string.artist_name_placeholder)

        // Длительность трека (статичное значение, не меняется)
        val trackMillis = track?.trackTimeMillis ?: 0L
        val durationText = formatTimeMs(trackMillis.toInt())
        textDurationValue.text = durationText

        // Начальное время прогресса
        textPlayTime.text = formatTimeMs(0)

        // Название альбома
        track?.collectionName?.let { collectionName ->
            textCollectionName.text = collectionName
            textCollectionName.visibility = TextView.VISIBLE
        } ?: run {
            textCollectionName.visibility = TextView.GONE
        }

        // Год релиза
        track?.releaseYear?.let { releaseYear ->
            textReleaseYear.text = releaseYear
            textReleaseYear.visibility = TextView.VISIBLE
        } ?: run {
            textReleaseYear.visibility = TextView.GONE
        }

        // Жанр
        track?.primaryGenreName?.let { genre ->
            textGenre.text = genre
            textGenre.visibility = TextView.VISIBLE
        } ?: run {
            textGenre.visibility = TextView.GONE
        }

        // Страна
        track?.country?.let { country ->
            textCountry.text = country
            textCountry.visibility = TextView.VISIBLE
        } ?: run {
            textCountry.visibility = TextView.GONE
        }

        // Загрузка изображения обложки в высоком качестве
        val artworkUrl = track?.getCoverArtwork()
        if (!artworkUrl.isNullOrBlank()) {
            Glide.with(this)
                .load(artworkUrl)
                .centerCrop()
                .placeholder(R.drawable.cover_placeholder)
                .error(R.drawable.cover_placeholder)
                .into(imageCover)
        } else {
            Glide.with(this)
                .load(R.drawable.cover_placeholder)
                .centerCrop()
                .into(imageCover)
        }
    }

    private fun setupListeners() {
        buttonBack.setOnClickListener {
            stopPlayback()
            finish()
        }

        buttonAddTrack.setOnClickListener {
            // TODO: CreatePlaylistActivity не найден в проекте
        }

        buttonPlayPause.setOnClickListener {
            if (isPlaying) {
                pausePlayback()
            } else {
                startPlayback()
            }
        }

        buttonLike.setOnClickListener {
            toggleLike()
        }
    }

    private fun startPlayback() {
        val previewUrl = currentTrack?.previewUrl
        if (previewUrl.isNullOrBlank()) {
            return
        }

        // Если MediaPlayer уже готов — запускаем
        if (mediaPlayer != null && isMediaPlayerPrepared) {
            performStart()
            return
        }

        // Иначе — пересоздаём и запускаем после подготовки
        playbackPosition = 0
        mediaPlayer?.release()
        mediaPlayer = null
        isMediaPlayerPrepared = false
        shouldStartOnPrepared = true
        initMediaPlayer()
    }

    private fun performStart() {
        mediaPlayer?.let { mp ->
            if (playbackPosition > 0 && playbackPosition < mp.duration) {
                mp.seekTo(playbackPosition)
            } else {
                playbackPosition = 0
                mp.seekTo(0)
            }
            mp.start()
            isPlaying = true
            updatePlayPauseIcon(isPlaying = true)
            handler.post(progressRunnable)
        }
    }

    private fun pausePlayback() {
        mediaPlayer?.let { mp ->
            if (mp.isPlaying) {
                playbackPosition = mp.currentPosition
                mp.pause()
                isPlaying = false
                updatePlayPauseIcon(isPlaying = false)
                handler.removeCallbacks(progressRunnable)
            }
        }
    }

    private fun stopPlayback() {
        handler.removeCallbacks(progressRunnable)
        mediaPlayer?.let { mp ->
            try {
                if (mp.isPlaying) {
                    mp.stop()
                }
                mp.reset()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            mp.release()
        }
        mediaPlayer = null
        isPlaying = false
        isMediaPlayerPrepared = false
        playbackPosition = 0
        shouldStartOnPrepared = false
    }

    private fun updatePlayPauseIcon(isPlaying: Boolean) {
        val isNightMode = resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
        buttonPlayPause.setImageResource(
            if (isPlaying) {
                if (isNightMode) R.drawable.ic_pause_n else R.drawable.ic_pause
            } else {
                if (isNightMode) R.drawable.ic_play_n else R.drawable.ic_play
            }
        )
    }

    private fun toggleLike() {
        isLiked = !isLiked
        val isNightMode = resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES
        if (isLiked) {
            buttonLike.setImageResource(if (isNightMode) R.drawable.ic_like_r_n else R.drawable.ic_like_r)
        } else {
            buttonLike.setImageResource(if (isNightMode) R.drawable.ic_like_n else R.drawable.ic_like)
        }
    }

    private fun formatTime(seconds: Int): String {
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return String.format("%d:%02d", minutes, remainingSeconds)
    }

    private fun formatTimeMs(milliseconds: Int): String {
        val totalSeconds = milliseconds / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onPause() {
        super.onPause()
        // При уходе в фон — останавливаем воспроизведение
        if (isPlaying) {
            pausePlayback()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopPlayback()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        stopPlayback()
        @Suppress("DEPRECATION")
        super.onBackPressed()
    }
}
