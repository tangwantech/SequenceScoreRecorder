package com.example.sequencescorerecorder.dataModels

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName="student_data_table")
data class StudentData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = 0,

    @ColumnInfo(name="student_id")
    var studentId: String? = null,

    @ColumnInfo(name = "student_name")
    var studentName: String? = null,

    @ColumnInfo(name = "student_gender")
    var studentGender: String? = null,

    @ColumnInfo(name = "student_class")
    var studentClass: String? = null,

    @ColumnInfo(name = "score_seq1")
    var scoreSeq1: Int = 0,

    @ColumnInfo(name = "score_seq2")
    var scoreSeq2: Int = 0,

    @ColumnInfo(name = "score_seq3")
    var scoreSeq3: Int = 0,

    @ColumnInfo(name = "score_seq4")
    var scoreSeq4: Int = 0,

    @ColumnInfo(name = "score_seq5")
    var scoreSeq5: Int = 0,

    @ColumnInfo(name = "score_seq6")
    var scoreSeq6: Int = 0,

)