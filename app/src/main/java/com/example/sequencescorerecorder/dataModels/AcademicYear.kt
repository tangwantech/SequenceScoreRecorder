package com.example.sequencescorerecorder.dataModels

import androidx.room.TypeConverter
import com.google.gson.Gson

data class AcademicYear(
    var academicYear: String? = null,
    var className: String? = null,
    val subjects: ArrayList<SubjectData>? = null
)

class AcademicYearsTypeConverter {
    @TypeConverter
    fun listToJson(value: List<AcademicYear>?) = Gson().toJson(value)

    @TypeConverter
    fun jsonToList(value: String) = Gson().fromJson(value, Array<AcademicYear>::class.java).toList()
}