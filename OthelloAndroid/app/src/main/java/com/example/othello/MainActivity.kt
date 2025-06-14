package com.example.othello

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val group: RadioGroup = findViewById(R.id.difficultyGroup)
        val button: Button = findViewById(R.id.startButton)

        button.setOnClickListener {
            val checkedId = group.checkedRadioButtonId
            val difficulty = when (checkedId) {
                R.id.radioBeginner -> AIPlayer.Difficulty.BEGINNER
                R.id.radioEasy -> AIPlayer.Difficulty.EASY
                R.id.radioMedium -> AIPlayer.Difficulty.MEDIUM
                R.id.radioHard -> AIPlayer.Difficulty.HARD
                else -> AIPlayer.Difficulty.BEGINNER
            }
            val intent = Intent(this, GameActivity::class.java)
            intent.putExtra("difficulty", difficulty.name)
            startActivity(intent)
        }
    }
}
