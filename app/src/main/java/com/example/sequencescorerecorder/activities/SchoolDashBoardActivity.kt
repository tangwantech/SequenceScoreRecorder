package com.example.sequencescorerecorder.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.viewModels.SchoolDashBoardActivityViewModel

//Provides options to navigate to the following activities
//StudentDatabaseManagerActivity
//SequenceScoresManagerActivity
//StudentSubjectManagerActivity
class SchoolDashBoardActivity : AppCompatActivity() {
    private lateinit var tvAcademicYear: TextView
    private lateinit var btnStudentDatabase: Button
    private lateinit var btnSequenceEditor: Button
    private lateinit var btnSubjectManager: Button
    private lateinit var viewModel: SchoolDashBoardActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setContentView(R.layout.activity_school_dashboard)
        initViewModel()
        initViews()
        setupViews()
        setViewListeners()

    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(this)[SchoolDashBoardActivityViewModel::class.java]
        viewModel.setSchoolIndex(intent.getIntExtra("schoolIndex", 0))
        viewModel.setAcademicYearIndex(intent.getIntExtra("academicYearIndex", 0))
//        println(intent.getIntExtra("academicYearIndex", 0))
    }

    private fun initViews() {
        tvAcademicYear = findViewById(R.id.tvAcademicYear)
        btnStudentDatabase = findViewById(R.id.btnDatabase)
        btnSequenceEditor = findViewById(R.id.btnSequenceEditor)
        btnSubjectManager = findViewById(R.id.btnSubjectManager)
    }

    private fun setupViews(){
        tvAcademicYear.text = "${resources.getString(R.string.academic_year)}: ${resources.getStringArray(R.array.academic_years)[viewModel.getAcademicYearIndex()]}"
    }
    private fun setViewListeners() {
        btnStudentDatabase.setOnClickListener {
            gotoSchoolDatabaseManagerActivity()
        }

        btnSequenceEditor.setOnClickListener {
            gotoSequenceClassSubjectSelectorActivity()
        }

        btnSubjectManager.setOnClickListener {
            gotoSubjectManagerActivity()
        }
    }

    private fun gotoSchoolDatabaseManagerActivity() {
        val intent = Intent(this, SchoolDatabaseManagerActivity::class.java)
        intent.apply{
            putExtra("title", btnStudentDatabase.text.toString())
            putExtra("schoolIndex", viewModel.getSchoolIndex())
            putExtra("academicYearIndex", viewModel.getAcademicYearIndex())
        }
        startActivity(intent)
    }

    private fun gotoSequenceClassSubjectSelectorActivity() {
        val intent = Intent(this, SequenceClassSubjectActivity::class.java)
        intent.apply{
            putExtra("title", btnSequenceEditor.text.toString())
            putExtra("schoolIndex", viewModel.getSchoolIndex())
            putExtra("academicYearIndex", viewModel.getAcademicYearIndex())
        }
        startActivity(intent)
    }

    private fun gotoSubjectManagerActivity(){
        val intent = Intent(this, SubjectManagerActivity::class.java)
        intent.apply{
            putExtra("title", btnSequenceEditor.text.toString())
            putExtra("schoolIndex", viewModel.getSchoolIndex())
            putExtra("academicYearIndex", viewModel.getAcademicYearIndex())
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        title = resources.getStringArray(R.array.schools)[viewModel.getSchoolIndex()]
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }


}