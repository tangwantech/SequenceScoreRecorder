package com.example.sequencescorerecorder.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.sequencescorerecorder.R

class MainActivity : AppCompatActivity() {
    private lateinit var btnStudentDatabase: Button
    private lateinit var btnSequenceEditor: Button
//    private lateinit var tvDatabase: TextView
//    private lateinit var tvSequenceEditor: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        setViewListeners()

    }

    private fun initViews() {
        btnStudentDatabase = findViewById(R.id.btnDatabase)
        btnSequenceEditor = findViewById(R.id.btnSequenceEditor)
//        tvDatabase = findViewById(R.id.tvDatabase)
//        tvSequenceEditor = findViewById(R.id.tvSequenceEditor)
    }

    private fun setViewListeners() {
        btnStudentDatabase.setOnClickListener {
            gotoDatabaseActivity()
        }

        btnSequenceEditor.setOnClickListener {
            gotoSequenceEditor()
        }
    }

    private fun gotoDatabaseActivity() {
        val intent = Intent(this, StudentDatabaseActivity::class.java)
       intent.putExtra("title", btnStudentDatabase.text.toString())
        startActivity(intent)
    }

    private fun gotoSequenceEditor() {
        val intent = Intent(this, StudentScoreActivity::class.java)
        intent.putExtra("title", btnSequenceEditor.text.toString())
        startActivity(intent)
    }
}