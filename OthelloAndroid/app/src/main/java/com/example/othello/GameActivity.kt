package com.example.othello

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class GameActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        val difficultyName = intent.getStringExtra("difficulty") ?: "Unknown"
        val textView: TextView = findViewById(R.id.gameInfo)
        textView.text = "Game started with $difficultyName AI"
    }
}
