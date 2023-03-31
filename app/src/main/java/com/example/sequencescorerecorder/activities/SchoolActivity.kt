package com.example.sequencescorerecorder.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.viewModels.SchoolActivityViewModel

//Provides options to navigate to the following activities
//StudentDatabaseManagerActivity
//SequenceScoresManagerActivity
//StudentSubjectManagerActivity
class SchoolActivity : AppCompatActivity() {
    private lateinit var tvAcademicYear: TextView
    private lateinit var btnStudentDatabase: Button
    private lateinit var btnSequenceEditor: Button
    private lateinit var btnSubjectManager: Button
    private lateinit var schoolActivityViewModel: SchoolActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_school)
        initViewModel()
        initViews()
        setupViews()
        setViewListeners()

    }

    private fun initViewModel(){
        schoolActivityViewModel = ViewModelProvider(this)[SchoolActivityViewModel::class.java]
        schoolActivityViewModel.setSchoolIndex(intent.getIntExtra("schoolIndex", 0))
        schoolActivityViewModel.setAcademicYearIndex(intent.getIntExtra("academicYearIndex", 0))
//        println(intent.getIntExtra("academicYearIndex", 0))
    }

    private fun initViews() {
        tvAcademicYear = findViewById(R.id.tvAcademicYear)
        btnStudentDatabase = findViewById(R.id.btnDatabase)
        btnSequenceEditor = findViewById(R.id.btnSequenceEditor)
        btnSubjectManager = findViewById(R.id.btnSubjectManager)
    }

    private fun setupViews(){
        tvAcademicYear.text = "${resources.getString(R.string.academic_year)}: ${resources.getStringArray(R.array.academic_years)[schoolActivityViewModel.getAcademicYearIndex()]}"
    }
    private fun setViewListeners() {
        btnStudentDatabase.setOnClickListener {
            gotoStudentDatabaseActivity()
        }

        btnSequenceEditor.setOnClickListener {
            gotoSequenceClassSubjectSelectorActivity()
        }

        btnSubjectManager.setOnClickListener {
            gotoSubjectManagerActivity()
        }
    }

    private fun gotoStudentDatabaseActivity() {
        val intent = Intent(this, StudentDatabaseManagerActivity::class.java)
        intent.apply{
            putExtra("title", btnStudentDatabase.text.toString())
            putExtra("schoolIndex", schoolActivityViewModel.getSchoolIndex())
            putExtra("academicYearIndex", schoolActivityViewModel.getAcademicYearIndex())
        }
        startActivity(intent)
    }

    private fun gotoSequenceClassSubjectSelectorActivity() {
        val intent = Intent(this, SequenceClassSubjectSelectorActivity::class.java)
        intent.apply{
            putExtra("title", btnSequenceEditor.text.toString())
            putExtra("schoolIndex", schoolActivityViewModel.getSchoolIndex())
            putExtra("academicYearIndex", schoolActivityViewModel.getAcademicYearIndex())
        }
        startActivity(intent)
    }

    private fun gotoSubjectManagerActivity(){
        val intent = Intent(this, SubjectManagerActivity::class.java)
        intent.apply{
            putExtra("title", btnSequenceEditor.text.toString())
            putExtra("schoolIndex", schoolActivityViewModel.getSchoolIndex())
            putExtra("academicYearIndex", schoolActivityViewModel.getAcademicYearIndex())
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        title = resources.getStringArray(R.array.schools)[schoolActivityViewModel.getSchoolIndex()]
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                exitActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exitActivity(){
        finish()
    }

}