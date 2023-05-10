package com.example.sequencescorerecorder.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequencescorerecorder.SequenceScoreRecorderConstants
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.dataModels.StudentScore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class ScoreEditorActivityViewModel : ViewModel() {
    private lateinit var database: com.example.sequencescorerecorder.database.StudentDatabase

    private val _students = MutableLiveData<ArrayList<StudentData>>()
    val students: LiveData<ArrayList<StudentData>> = _students

    private val _studentsScoreList = MutableLiveData<ArrayList<StudentScore>>()
    val studentsScoreList: LiveData<ArrayList<StudentScore>> = _studentsScoreList

    private val _areStudentsDataAvailable = MutableLiveData<Boolean>()
    val areStudentsDataAvailable: LiveData<Boolean> = _areStudentsDataAvailable

    //    academicYearIndex: Int,
//    className: String,
//    sequenceIndex: Int,
//    subjectIndex: Int
    private val _academicYearIndex = MutableLiveData<Int>()
    private val _className = MutableLiveData<String>()
    private val _sequenceIndex = MutableLiveData<Int>()
    private val _subjectIndex = MutableLiveData<Int>()

    private var currentStudentIndex: Int? = null
    private var firstStudentIndex: Int? = null
    private var lastStudentIndex: Int? = null

    private val _isCurrentIndexEqualFirst = MutableLiveData<Boolean>()
    val isCurrentIndexEqualFirst: LiveData<Boolean> = _isCurrentIndexEqualFirst
    private val _isCurrentIndexEqualLast = MutableLiveData<Boolean>()
    val isCurrentIndexEqualLast: LiveData<Boolean> = _isCurrentIndexEqualLast

    private val _studentDataListChangedAt = MutableLiveData<Int>()
    val studentDataListChangedAt: LiveData<Int> = _studentDataListChangedAt

    private val _studentsDataToUse = MutableLiveData<ArrayList<StudentData>>()

    private val _numberOfStudentsRegisteredSubject = MutableLiveData<Int>()
    val numberOfStudentsRegisteredSubject: LiveData<Int> = _numberOfStudentsRegisteredSubject

    private val _numberSat = MutableLiveData<Int>(0)
    val numberSat: LiveData<Int> = _numberSat

    private val _numberPassed = MutableLiveData<Int>()
    val numberPassed: LiveData<Int> = _numberPassed

    private val _percentagePassed = MutableLiveData<Double>()
    val percentagePassed: LiveData<Double> = _percentagePassed

    fun initDatabase(context: Context) {
        database =
            com.example.sequencescorerecorder.database.StudentDatabase.getStudentDatabase(context)
    }

    fun setAcademicYearIndex(index: Int) {
        _academicYearIndex.value = index
    }

    fun setClassName(className: String) {
        _className.value = className
    }

    fun setSequenceIndex(index: Int) {
        _sequenceIndex.value = index
    }

    fun setSubjectIndex(index: Int) {
        _subjectIndex.value = index
    }


    fun loadStudentsWhereSchool(schoolName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tempStudents = database.studentDataDao().getStudentsBySchool(schoolName)
            withContext(Dispatchers.Main) {
                val tempStudents2: ArrayList<StudentData> = ArrayList()
                if (tempStudents.isNotEmpty()) {
                    tempStudents.forEach { studentData ->
                        tempStudents2.add(studentData)
                    }
                    tempStudents2.sortBy { studentData -> studentData.studentName }
                    _students.value = tempStudents2

                } else {
                    _students.value = ArrayList()
                }
                _areStudentsDataAvailable.value = tempStudents2.isNotEmpty()
                setStudentsScoreList()

            }
        }

    }

    private fun setStudentsScoreList() {
        val tempStudentsScoreList = ArrayList<StudentScore>()
        val tempStudentDataList = ArrayList<StudentData>()
        _students.value?.forEach {
            val academicYearData = it.academicYears[_academicYearIndex.value!!]
//            println(academicYearData)
//
            if (_className.value!! == academicYearData.className && academicYearData.subjects!![_subjectIndex.value!!].doesSubject) {
                val score =
                    academicYearData.subjects!![_subjectIndex.value!!].sequenceScores[_sequenceIndex.value!!].score
                val studentScore = StudentScore(
                    it.studentId!!,
                    it.studentName!!,
                    score,
                    academicYearData.studentClassNumber
                )
                tempStudentsScoreList.add(studentScore)
                tempStudentDataList.add(it)

            }


        }
        _studentsScoreList.value = tempStudentsScoreList
        setNumberOfStudentsRegisteredSubject(tempStudentsScoreList)
        setStudentsDataToUse(tempStudentDataList)
        initFirstCurrentAndLastStudentIndexes()
        updateNumberSat()

    }

    private fun setNumberOfStudentsRegisteredSubject(tempScoreList: ArrayList<StudentScore>) {
        _numberOfStudentsRegisteredSubject.value = tempScoreList.size
    }

    private fun setStudentsDataToUse(studentsDataToUse: ArrayList<StudentData>) {
        _studentsDataToUse.value = studentsDataToUse
    }

    fun incrementCurrentStudentIndex(): Int {
        currentStudentIndex = currentStudentIndex!! + 1
        checkIfCurrentIndexEqualFirst()
        checkIfCurrentIndexEqualLast()
        return currentStudentIndex!!
    }

    fun decrementCurrentStudentIndex(): Int {
        currentStudentIndex = currentStudentIndex!! - 1
        checkIfCurrentIndexEqualFirst()
        checkIfCurrentIndexEqualLast()
        return currentStudentIndex!!
    }

    private fun initFirstCurrentAndLastStudentIndexes() {
        firstStudentIndex = 0
        currentStudentIndex = 0
        lastStudentIndex = if (_studentsScoreList.value!!.isNotEmpty()) {
            _studentsScoreList.value!!.size - 1
        } else {
            0
        }

        checkIfCurrentIndexEqualFirst()
        checkIfCurrentIndexEqualLast()
    }

    private fun checkIfCurrentIndexEqualFirst() {
        _isCurrentIndexEqualFirst.value = currentStudentIndex == firstStudentIndex
    }

    private fun checkIfCurrentIndexEqualLast() {
        _isCurrentIndexEqualLast.value = currentStudentIndex == lastStudentIndex
    }

    fun updateCurrentStudentIndex(position: Int) {
        currentStudentIndex = position
        checkIfCurrentIndexEqualFirst()
        checkIfCurrentIndexEqualLast()
    }

    fun updateStudentScoreAt(
        academicYearIndex: Int,
        sequenceIndex: Int,
        subjectIndex: Int,
        score: Double?
    ) {
        val studentDataToUpdate = _studentsDataToUse.value!![currentStudentIndex!!]
        studentDataToUpdate.apply {
            academicYears[academicYearIndex].subjects!![subjectIndex].sequenceScores[sequenceIndex].score =
                score
        }
        updateStudentsScoreListAt(currentStudentIndex!!, score)
        updateStudentsAt(currentStudentIndex!!, studentDataToUpdate)
        _studentDataListChangedAt.value = currentStudentIndex!!
    }

    private fun updateStudentsScoreListAt(currentStudentIndex: Int, score: Double?) {
        _studentsScoreList.value!![currentStudentIndex].studentScore = score
        updateNumberSat()

    }

    private fun updateStudentsAt(currentStudentIndex: Int, studentData: StudentData) {
        _students.value!![currentStudentIndex] = studentData
    }

    private fun updateNumberSat(){
        _studentsScoreList.value?.let {
            _numberSat.value = it.count { studentScore -> studentScore.studentScore != null }
            updateNumberPassed()
        }

    }

    private fun updateNumberPassed() {
        var tempNumPassed = 0
        _studentsScoreList.value?.let {
//            _numberPassed.value =
//                it.count { studentScore -> studentScore.studentScore!! >= SequenceScoreRecorderConstants.AVERAGE_SCORE }
            it.forEach { studentScore ->
                studentScore.studentScore?.let{ score ->
                    if(score >= SequenceScoreRecorderConstants.AVERAGE_SCORE){
                        tempNumPassed += 1
                    }
                }
            }
            _numberPassed.value = tempNumPassed
            updatePercentagePassed()
        }

    }

    private fun updatePercentagePassed() {
        _percentagePassed.value =
            ((_numberPassed.value!!.toDouble() / _numberSat.value!!.toDouble()) * 100)
        _percentagePassed.value = String.format("%.2f", _percentagePassed.value).toDouble()

    }

    fun updateDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            _studentsDataToUse.value?.let {
                _studentsDataToUse.value!!.forEach {
                    database.studentDataDao().updateStudent(it)
                }
            }

        }
    }

}