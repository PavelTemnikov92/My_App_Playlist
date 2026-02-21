package com.practicum.myapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.startActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //  IDs
        val searchButton = findViewById<Button>(R.id.btnSearch)
        val mediaButton = findViewById<Button>(R.id.btnMedia)
        val settingsButton = findViewById<Button>(R.id.btnSettings)

        // 1.button (Search)
        searchButton.setOnClickListener(object : android.view.View.OnClickListener {
            override fun onClick(v: android.view.View?) {
                val intent = Intent(this@MainActivity, SearchActivity::class.java)
                startActivity(intent)
            }
        })

        // 2.button (Media)
        mediaButton.setOnClickListener {
            val intent = Intent(this@MainActivity, MediaActivity::class.java)
            startActivity(intent)
        }

        // 3.button (Settings)
        settingsButton.setOnClickListener {
            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
        }
    }
}