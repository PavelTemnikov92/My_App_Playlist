package com.practicum.myapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity

class CreatePlaylistActivity : AppCompatActivity() {

    private lateinit var buttonBack: ImageButton
    private lateinit var playlistName: EditText
    private lateinit var playlistDescription: EditText
    private lateinit var createPlaylistButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_playlist)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        buttonBack = findViewById(R.id.buttonBack)
        playlistName = findViewById(R.id.playlistName)
        playlistDescription = findViewById(R.id.playlistDescription)
        createPlaylistButton = findViewById(R.id.createPlaylistButton)
    }

    private fun setupListeners() {
        buttonBack.setOnClickListener {
            finish()
        }

        // Валидация поля названия - активируем кнопку только при вводе текста
        playlistName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val text = s?.toString() ?: ""
                createPlaylistButton.isEnabled = text.isNotEmpty()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        createPlaylistButton.setOnClickListener {
            // Получаем название плейлиста
            val playlistNameText = playlistName.text.toString().trim()

            // Создаём Intent для возврата результата
            val resultIntent = Intent()
            resultIntent.putExtra("playlist_name", playlistNameText)
            setResult(RESULT_OK, resultIntent)

            // Задерживаем закрытие активности, чтобы уведомление успело появиться
            Handler(Looper.getMainLooper()).postDelayed({
                finish()
            }, 1000)
        }
    }
}
