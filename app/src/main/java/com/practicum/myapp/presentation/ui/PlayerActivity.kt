package com.practicum.myapp.presentation.ui

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
import com.practicum.myapp.R
import com.practicum.myapp.domain.model.Track
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
                .placeholder(R.drawable.album)
                .error(R.drawable.album)
                .into(imageCover)
        } else {
            Glide.with(this)
                .load(R.drawable.album)
                .centerCrop()
                .into(imageCover)
        }
    }

    private fun setupListeners() {
        buttonBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        buttonPlayPause.setOnClickListener {
            if (isPlaying) {
                performPause()
            } else {
                performStart()
            }
        }

        buttonLike.setOnClickListener {
            isLiked = !isLiked
            updateLikeIcon()
            showSnackBar(if (isLiked) "Трек добавлен в избранное" else "Трек удалён из избранного")
        }

        buttonAddTrack.setOnClickListener {
            showSnackBar("Трек добавлен в плейлист")
        }
    }

    private fun performStart() {
        mediaPlayer?.let { mp ->
            if (isMediaPlayerPrepared) {
                mp.start()
                isPlaying = true
                updatePlayPauseIcon(isPlaying = true)
                handler.post(progressRunnable)
            } else {
                shouldStartOnPrepared = true
            }
        }
    }

    private fun performPause() {
        mediaPlayer?.let { mp ->
            if (isPlaying) {
                mp.pause()
                playbackPosition = mp.currentPosition
                isPlaying = false
                updatePlayPauseIcon(isPlaying = false)
                handler.removeCallbacks(progressRunnable)
            }
        }
    }

    private fun updatePlayPauseIcon(isPlaying: Boolean) {
        val iconRes = if (isPlaying) {
            R.drawable.ic_pause
        } else {
            R.drawable.ic_play
        }
        buttonPlayPause.setImageResource(iconRes)
    }

    private fun updateLikeIcon() {
        val iconRes = if (isLiked) {
            R.drawable.ic_liked
        } else {
            R.drawable.ic_not_liked
        }
        buttonLike.setImageResource(iconRes)
    }

    private fun showSnackBar(message: String) {
        val rootView = findViewById<FrameLayout>(android.R.id.content)
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(Color.parseColor("#1A1B22"))
            .setTextColor(Color.WHITE)
            .show()
    }

    private fun formatTimeMs(ms: Int): String {
        val seconds = (ms / 1000) % 60
        val minutes = (ms / 1000) / 60
        return String.format("%02d:%02d", minutes, seconds)
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
        handler.removeCallbacks(progressRunnable)
    }

    override fun onPause() {
        super.onPause()
        if (isPlaying) {
            performPause()
        }
    }
}
