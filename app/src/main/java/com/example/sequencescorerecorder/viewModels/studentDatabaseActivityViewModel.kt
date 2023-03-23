package com.example.sequencescorerecorder.viewModels

import android.os.Bundle
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class StudentDatabaseActivityViewModel: ViewModel() {
    private var schoolIndex: Int? = null
    private var academicYearIndex: Int? = null

    fun setSchoolIndex(index: Int){
        this.schoolIndex = index
    }

    fun setAcademicYearIndex(index: Int){
        academicYearIndex = index
    }

    fun getSchoolIndex(): Int{
        return schoolIndex!!
    }

    fun getAcademicYearIndex(): Int{
        return academicYearIndex!!
    }

}