package com.example.sequencescorerecorder.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import com.example.sequencescorerecorder.R
import com.google.android.material.textfield.TextInputEditText

class SequenceDatabaseActivity : AppCompatActivity() {
    private lateinit var studentId: TextInputEditText
    private lateinit var studentName: TextInputEditText
    private lateinit var studentGender: AutoCompleteTextView
    private lateinit var studentClass: AutoCompleteTextView
    private lateinit var btnClear: Button
    private lateinit var btnAdd: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sequence_database)
        initViews()
        setupViewAdapters()
        setupViewListeners()

    }

    private fun initViews(){
        studentId = findViewById(R.id.studentId)
        studentName = findViewById(R.id.studentName)
        studentGender = findViewById(R.id.studentGender)
        studentClass = findViewById(R.id.studentClass)
        btnClear = findViewById(R.id.btnClear)
        btnAdd = findViewById(R.id.btnAdd)


    }
    private fun setupViewAdapters(){
        val genderAdapter = ArrayAdapter<String>(this, R.layout.drop_down_item, resources.getStringArray(R.array.gender))
        studentGender.setAdapter(genderAdapter)

        val studentClassesAdapter = ArrayAdapter<String>(this, R.layout.drop_down_item, resources.getStringArray(R.array.classes))
        studentClass.setAdapter(studentClassesAdapter)
    }
    private fun setupViewListeners(){
        studentGender.setOnItemClickListener { _, _, _, l ->
            Toast.makeText(this, "${studentGender.text.toString()}", Toast.LENGTH_SHORT).show()
        }
        btnClear.setOnClickListener{
            clearInputFields()
        }
        btnAdd.setOnClickListener {
            clearInputFields()
        }
    }

    private fun clearInputFields(){
        studentId.apply {
            text = null
        }

        studentName.apply {
            text = null
            isFocusable = false
        }

        studentGender.apply {
            text = null
            isFocusable = false
        }

        studentClass.apply {
            text = null
            isFocusable = false
        }
    }

    override fun onResume() {
        super.onResume()
        title = intent.getStringExtra("title")
    }
}