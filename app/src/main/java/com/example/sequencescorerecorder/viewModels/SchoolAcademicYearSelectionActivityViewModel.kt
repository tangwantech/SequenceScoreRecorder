package com.example.sequencescorerecorder.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SchoolAcademicYearSelectionActivityViewModel: ViewModel() {
    private val _schoolIndex = MutableLiveData<Int>()
    val schoolIndex: LiveData<Int> = _schoolIndex
    private val _academicYearIndex = MutableLiveData<Int>()
    val academicYearIndex: LiveData<Int> = _academicYearIndex

    private val _areSchoolAndAcademicYearSelected = MutableLiveData<Boolean>()
    val areSchoolAndAcademicYearSelected: LiveData<Boolean> = _areSchoolAndAcademicYearSelected

    fun setSchoolIndex(index: Int){
        _schoolIndex.value = index
        checkIfSchoolAndAcademicYearAreSelected()
    }
    fun setAcademicYearIndex(academicYearIndex: Int){
        this._academicYearIndex.value = academicYearIndex
        checkIfSchoolAndAcademicYearAreSelected()
    }

    private fun checkIfSchoolAndAcademicYearAreSelected(){
        _areSchoolAndAcademicYearSelected.value = (_schoolIndex.value != null && _academicYearIndex.value != null)

    }

    fun getSchoolIndex(): Int? {
        return _schoolIndex.value
    }

    fun getAcademicYearIndex(): Int? {
        return _academicYearIndex.value
    }

}