package com.example.sequencescorerecorder.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sequencescorerecorder.R

class StudentScoreActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_score)

    }
    override fun onResume() {
        super.onResume()
        title = intent.getStringExtra("title")
    }
}