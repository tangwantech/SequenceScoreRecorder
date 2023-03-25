package com.example.sequencescorerecorder.dataModels

data class StudentScore(val studentId: String, val studentName: String, var studentScore: Double?, var classNumber: String?=null)
