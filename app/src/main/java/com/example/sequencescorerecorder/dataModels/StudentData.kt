package com.example.sequencescorerecorder.dataModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="student_data_table")
data class StudentData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = 0,

    @ColumnInfo(name="student_school")
    var studentSchool: String? = null,

    @ColumnInfo(name="student_id")
    var studentId: String? = null,

    @ColumnInfo(name = "student_name")
    var studentName: String? = null,

    @ColumnInfo(name = "student_gender")
    var studentGender: String? = null,

    @ColumnInfo(name = "current_class")
    var currentClass: String? = null,

    @ColumnInfo(name= "current_academic_year")
    var currentAcademicYear: String? = null,

    @ColumnInfo(name = "academic_years")
    var academicYears: List<AcademicYear>
    )