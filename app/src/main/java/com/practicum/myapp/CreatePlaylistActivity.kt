package com.practicum.myapp

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout

class CreatePlaylistActivity : AppCompatActivity() {

    private lateinit var buttonBack: ImageButton
    private lateinit var nameLayout: TextInputLayout
    private lateinit var descriptionLayout: TextInputLayout
    private lateinit var createPlaylistButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_playlist)

        initViews()
        setupListeners()
    }

    private fun initViews() {
        buttonBack = findViewById(R.id.buttonBack)
        nameLayout = findViewById(R.id.nameLayout)
        descriptionLayout = findViewById(R.id.descriptionLayout)
        createPlaylistButton = findViewById(R.id.createPlaylistButton)
    }

    private fun setupListeners() {
        buttonBack.setOnClickListener {
            finish()
        }

        // Валидация поля названия - активируем кнопку только при вводе текста
        val nameEditText = nameLayout.editText
        if (nameEditText != null) {
            nameEditText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val text = s?.toString() ?: ""
                    createPlaylistButton.isEnabled = text.isNotEmpty()
                }

                override fun afterTextChanged(s: Editable?) {}
            })
        }

        createPlaylistButton.setOnClickListener {
            // Получаем название плейлиста
            val playlistNameText = nameLayout.editText?.text?.toString()?.trim() ?: ""

            // Создаём Intent для возврата результата
            val resultIntent = Intent()
            resultIntent.putExtra("playlist_name", playlistNameText)
            setResult(Activity.RESULT_OK, resultIntent)

            // Закрываем активность
            finish()
        }
    }
}
