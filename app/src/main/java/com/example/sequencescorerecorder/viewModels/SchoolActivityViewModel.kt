package com.example.sequencescorerecorder.viewModels

import androidx.lifecycle.ViewModel

class SchoolActivityViewModel: ViewModel() {
    private var schoolIndex: Int? = null
    private var academicYearIndex: Int? = null

    fun setSchoolIndex(index: Int?) {
        this.schoolIndex = index
    }

    fun setAcademicYearIndex(index: Int?){
        this.academicYearIndex = index
    }

    fun getSchoolIndex(): Int{
        return schoolIndex!!
    }

    fun getAcademicYearIndex(): Int{
        return academicYearIndex!!
    }


}