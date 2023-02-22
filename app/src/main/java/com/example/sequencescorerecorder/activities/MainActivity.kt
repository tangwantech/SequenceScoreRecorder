package com.example.sequencescorerecorder.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.sequencescorerecorder.R

class MainActivity : AppCompatActivity() {
    private lateinit var btnCardViewDataBase: CardView
    private lateinit var btnCardViewSequenceEditor: CardView
    private lateinit var tvDatabase: TextView
    private lateinit var tvSequenceEditor: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViews()
        setViewListeners()

    }

    private fun initViews() {
        btnCardViewDataBase = findViewById(R.id.btnCardViewDatabase)
        btnCardViewSequenceEditor = findViewById(R.id.btnCardViewSequenceEditor)
        tvDatabase = findViewById(R.id.tvDatabase)
        tvSequenceEditor = findViewById(R.id.tvSequenceEditor)
    }

    private fun setViewListeners() {
        btnCardViewDataBase.setOnClickListener {
            gotoDatabaseActivity()
        }

        btnCardViewSequenceEditor.setOnClickListener {
            gotoSequenceEditor()
        }
    }

    private fun gotoDatabaseActivity() {
        val intent = Intent(this, SequenceDatabaseActivity::class.java)
       intent.putExtra("title", tvDatabase.text.toString())
        startActivity(intent)
    }

    private fun gotoSequenceEditor() {
        val intent = Intent(this, StudentScoreActivity::class.java)
        intent.putExtra("title", tvSequenceEditor.text.toString())
        startActivity(intent)
    }
}