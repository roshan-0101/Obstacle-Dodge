package com.example.obstacledodge

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.Switch
import android.widget.TextView

class MainActivity : Activity() {
    fun loaddata(): Int {
        val prefs = getSharedPreferences("highscore", Context.MODE_PRIVATE)
        return prefs.getInt("bestscore", 0)
    }
    var hackerMode=false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.home)
        var obstacleNo=6


        var play: Button = findViewById(R.id.playbutton)
        var settings: ImageButton=findViewById(R.id.settings)
        var hacker:Switch=findViewById(R.id.hacker)
        hacker.setOnCheckedChangeListener { _, isChecked ->
            hackerMode = isChecked
        }
        var scoreText:TextView=findViewById(R.id.highScore)
        val ObsSeek:SeekBar=findViewById(R.id.noofobs)
        scoreText.text="High Score=${loaddata()}"
        ObsSeek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                // Update a TextView with the progress value
                obstacleNo=progress+6
                findViewById<TextView>(R.id.noOfObstacle).text = "$obstacleNo"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        } )

        play.setOnClickListener {
            val canvas = MyCanvas(this, obstacleNo, hackerMode)
            setContentView(canvas)
        }
    }
}