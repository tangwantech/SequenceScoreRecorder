package com.example.sequencescorerecorder.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.viewModels.SchoolAcademicYearSelectionActivityViewModel

class SchoolAcademicYearSelectionActivity : AppCompatActivity() {
    private lateinit var viewModel: SchoolAcademicYearSelectionActivityViewModel
    private lateinit var autoCompleteSchool: AutoCompleteTextView
    private lateinit var autoCompleteAcademicYear: AutoCompleteTextView
    private lateinit var btnOk: Button
    private lateinit var btnExit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_academic_year_selection)
        initViewModel()
        initActivityViews()
        setupActivityViewListeners()
        setupViewObservers()
    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(this)[SchoolAcademicYearSelectionActivityViewModel::class.java]

    }
    private fun initActivityViews(){
        autoCompleteSchool = findViewById(R.id.autoCompleteSchool)
        autoCompleteAcademicYear = findViewById(R.id.autoCompleteAcademicYear)
        btnOk = findViewById(R.id.btnLoad)
        btnExit = findViewById(R.id.btnExit)
    }

    private fun setupAutoCompleteAdapters(){
        val autoCompleteSchoolAdapter = ArrayAdapter<String>(this, R.layout.drop_down_item, resources.getStringArray(R.array.schools))
        viewModel.schoolIndex.value?.let{
            autoCompleteSchool.setText(resources.getStringArray(R.array.schools)[it])
        }
        autoCompleteSchool.setAdapter(autoCompleteSchoolAdapter)


        val autoCompleteAcademicYearAdapter = ArrayAdapter<String>(this, R.layout.drop_down_item, resources.getStringArray(R.array.academic_years))
        viewModel.academicYearIndex.value?.let{
            autoCompleteAcademicYear.setText(resources.getStringArray(R.array.academic_years)[it])
        }
        autoCompleteAcademicYear.setAdapter(autoCompleteAcademicYearAdapter)

    }

    private fun setupActivityViewListeners(){
        autoCompleteSchool.setOnItemClickListener { _, _, index, _ ->
            viewModel.setSchoolIndex(index)

        }

        autoCompleteAcademicYear.setOnItemClickListener { _, _, index, _ ->
            viewModel.setAcademicYearIndex(index)


        }

        btnOk.setOnClickListener {
            gotoSchoolDashBoardActivity()
        }

        btnExit.setOnClickListener {
            exitActivity()
        }
    }

    private fun setupViewObservers(){
        viewModel.areSchoolAndAcademicYearSelected.observe(this, Observer {
            btnOk.isEnabled = it
        })
    }

    override fun onResume() {
        super.onResume()
        title = resources.getString(R.string.app_name)
        setupAutoCompleteAdapters()

    }

    private fun exitActivity(){
        finish()
    }

    private fun gotoSchoolDashBoardActivity(){
        val intent = Intent(this, SchoolDashBoardActivity::class.java)
        intent.apply {
            putExtra("schoolIndex", viewModel.getSchoolIndex())
            putExtra("academicYearIndex", viewModel.getAcademicYearIndex())
        }
        startActivity(intent)
    }
}