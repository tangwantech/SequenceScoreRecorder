package com.example.sequencescorerecorder.interfaces

import android.os.Bundle
import androidx.lifecycle.LiveData

interface RequestSchoolAndAcademicYearBundleListener {
    fun onRequestSchoolAndAcademicYearBundle(): LiveData<Bundle>
}