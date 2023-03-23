package com.example.sequencescorerecorder.dataModels

data class SubjectData(
    var subjectName: String,
    var doesSubject: Boolean,
    val sequenceScores: ArrayList<SequenceScore>
)
