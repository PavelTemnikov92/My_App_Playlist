package com.practicum.myapp

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar

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

    // Регистрируем контракт для получения результата от CreatePlaylistActivity
    private val createPlaylistLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val playlistName = result.data?.getStringExtra("playlist_name") ?: ""
            showPlaylistCreatedSnackbar(playlistName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        initViews()
        setupListeners()
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
    }

    private fun displayTrackInfo() {
        val track = currentTrack

        textTrackName.text = track?.trackName ?: getString(R.string.track_name_placeholder)
        textArtistName.text = track?.artistName ?: getString(R.string.artist_name_placeholder)

        // Длительность трека
        val trackMillis = track?.trackTimeMillis ?: 0L
        val trackSeconds = (trackMillis / 1000).toInt()
        val durationText = formatTime(trackSeconds)
        textPlayTime.text = durationText
        textDurationValue.text = durationText

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
            finish()
        }

        buttonAddTrack.setOnClickListener {
            // Открытие экрана создания плейлиста
            val intent = Intent(this@PlayerActivity, CreatePlaylistActivity::class.java)
            createPlaylistLauncher.launch(intent)
        }

        buttonPlayPause.setOnClickListener {
            togglePlayPause()
        }

        buttonLike.setOnClickListener {
            toggleLike()
        }
    }

    private fun showPlaylistCreatedSnackbar(playlistName: String) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            "Плейлист «$playlistName» создан",
            Snackbar.LENGTH_SHORT
        )

        // Проверяем текущую тему
        val isNightMode = resources.configuration.uiMode and
            android.content.res.Configuration.UI_MODE_NIGHT_MASK ==
            android.content.res.Configuration.UI_MODE_NIGHT_YES

        // Настраиваем внешний вид уведомления
        if (isNightMode) {
            // Тёмная тема: белый фон, чёрный текст
            snackbar.setBackgroundTint(Color.WHITE)
            snackbar.setTextColor(Color.BLACK)
        } else {
            // Светлая тема: чёрный фон, белый текст
            snackbar.setBackgroundTint(Color.BLACK)
            snackbar.setTextColor(Color.WHITE)
        }

        // Центрируем уведомление внизу экрана
        val snackbarView = snackbar.view
        val params = snackbarView.layoutParams as FrameLayout.LayoutParams
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.width = FrameLayout.LayoutParams.MATCH_PARENT
        val marginPx = (16 * resources.displayMetrics.density).toInt()
        params.marginStart = marginPx
        params.marginEnd = marginPx
        snackbarView.layoutParams = params

        // Центрируем текст в уведомлении
        val snackbarText = snackbarView.findViewById<TextView>(
            com.google.android.material.R.id.snackbar_text
        )
        snackbarText.textAlignment = TextView.TEXT_ALIGNMENT_CENTER
        snackbarText.gravity = Gravity.CENTER
        snackbarText.setPadding(0, snackbarText.paddingTop, 0, snackbarText.paddingBottom)

        snackbar.show()
    }

    private fun togglePlayPause() {
        isPlaying = !isPlaying
        // Проверяем текущую тему
        val isNightMode = resources.configuration.uiMode and 
            android.content.res.Configuration.UI_MODE_NIGHT_MASK == 
            android.content.res.Configuration.UI_MODE_NIGHT_YES
        if (isPlaying) {
            buttonPlayPause.setImageResource(if (isNightMode) R.drawable.ic_pause_n else R.drawable.ic_pause)
        } else {
            buttonPlayPause.setImageResource(if (isNightMode) R.drawable.ic_play_n else R.drawable.ic_play)
        }
    }

    private fun toggleLike() {
        isLiked = !isLiked
        // Проверяем текущую тему
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
}
