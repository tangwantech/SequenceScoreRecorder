package com.example.sequencescorerecorder.activities

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.viewModels.HomeActivityViewModel

class HomeActivity : AppCompatActivity() {
    private lateinit var homeActivityViewModel: HomeActivityViewModel
    private lateinit var dialogView: View
    private lateinit var autoCompleteSchool: AutoCompleteTextView
    private lateinit var autoCompleteAcademicYear: AutoCompleteTextView
    private lateinit var positiveBtn: Button

    private lateinit var pref: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        pref = getSharedPreferences(resources.getString(R.string.app_name), MODE_PRIVATE)
        initViewModel()
        setupViewObservers()
    }

    private fun initViewModel(){
        homeActivityViewModel = ViewModelProvider(this)[HomeActivityViewModel::class.java]

    }

    private fun initDialogView(){
        dialogView = layoutInflater.inflate(R.layout.school_selection, null)
        autoCompleteSchool = dialogView.findViewById(R.id.autoCompleteSchool)
        autoCompleteAcademicYear = dialogView.findViewById(R.id.autoCompleteAcademicYear)


    }

    private fun setupDialogViewAdapters(){
        val autoCompleteSchoolAdapter = ArrayAdapter<String>(this, R.layout.drop_down_item, resources.getStringArray(R.array.schools))
        autoCompleteSchool.setAdapter(autoCompleteSchoolAdapter)

        val autoCompleteAcademicYearAdapter = ArrayAdapter<String>(this, R.layout.drop_down_item, resources.getStringArray(R.array.academic_years))
        autoCompleteAcademicYear.setAdapter(autoCompleteAcademicYearAdapter)
    }

    private fun setupDialogViewListeners(){
        autoCompleteSchool.setOnItemClickListener { _, _, index, _ ->
            homeActivityViewModel.setSchoolIndex(index)
        }

        autoCompleteAcademicYear.setOnItemClickListener { _, _, index, _ ->
            homeActivityViewModel.setAcademicYearIndex(index)
        }
    }

    private fun setupViewObservers(){
        homeActivityViewModel.areSchoolAndAcademicYearSelected.observe(this, Observer {
            positiveBtn.isEnabled = it
        })
    }

    private fun displaySchoolSelectorDialog(){

        initDialogView()
        setupDialogViewAdapters()
        setupDialogViewListeners()
        val selectSchoolDialog = AlertDialog.Builder(this).apply {
            setView(dialogView)
            setPositiveButton("OK"){_, _ ->
                gotoMainActivity()
            }
            setNegativeButton(resources.getString(R.string.exit)){_, _ ->
                exitActivity()
            }
        }.create()
        selectSchoolDialog.show()
        positiveBtn = selectSchoolDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveBtn.isEnabled = false


    }

    override fun onResume() {
        super.onResume()
        title = resources.getString(R.string.app_name)
        displaySchoolSelectorDialog()
    }

    private fun exitActivity(){
        finish()
    }

    private fun gotoMainActivity(){
        val intent = Intent(this, SchoolActivity::class.java)
        intent.apply {
            putExtra("schoolIndex", homeActivityViewModel.getSchoolIndex())
            putExtra("academicYearIndex", homeActivityViewModel.getAcademicYearIndex())
        }
        startActivity(intent)
    }
}