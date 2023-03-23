package com.example.sequencescorerecorder.dataModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="school_data_table")
data class SchoolData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = 0,

    @ColumnInfo(name="school_name")
    var schoolName: String,

    @ColumnInfo(name="academic_years")
    val AcademicYear: List<AcademicYear>
    )
