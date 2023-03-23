package com.example.sequencescorerecorder.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeActivityViewModel: ViewModel() {
    private var schoolIndex: Int? = null
    private var academicYearIndex: Int? = null
    private val _areSchoolAndAcademicYearSelected = MutableLiveData<Boolean>()
    val areSchoolAndAcademicYearSelected: LiveData<Boolean> = _areSchoolAndAcademicYearSelected

    fun setSchoolIndex(index: Int){
        schoolIndex = index
        checkIfSchoolAndAcademicYearAreSelected()
    }
    fun setAcademicYearIndex(academicYearIndex: Int){
        this.academicYearIndex = academicYearIndex
        checkIfSchoolAndAcademicYearAreSelected()
    }

    private fun checkIfSchoolAndAcademicYearAreSelected(){
        _areSchoolAndAcademicYearSelected.value = (schoolIndex != null && academicYearIndex != null)

    }

    fun getSchoolIndex(): Int? {
        return schoolIndex
    }

    fun getAcademicYearIndex(): Int? {
        return academicYearIndex
    }

}