package com.example.sequencescorerecorder.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.dataModels.StudentScore
import com.example.sequencescorerecorder.database.StudentDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ScoreEditorActivityViewModel : ViewModel() {
    private lateinit var database: StudentDatabase

    private val _students = MutableLiveData<ArrayList<StudentData>>()
    val students: LiveData<ArrayList<StudentData>> = _students

    private val _studentsScoreList = MutableLiveData<ArrayList<StudentScore>>()
    val studentsScoreList: LiveData<ArrayList<StudentScore>> = _studentsScoreList

    private val _areStudentsDataAvailable = MutableLiveData<Boolean>()
    val areStudentsDataAvailable: LiveData<Boolean> = _areStudentsDataAvailable

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


    fun initDatabase(context: Context) {
        database = StudentDatabase.getStudentDatabase(context)
    }


    fun getStudentsWhereSchool(schoolName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val tempStudents = database.studentDataDao().getStudentsBySchool(schoolName)
            withContext(Dispatchers.Main) {
                val tempStudents2: ArrayList<StudentData> = ArrayList()

                tempStudents.forEach { studentData ->
                    tempStudents2.add(studentData)
                }
                _students.value = tempStudents2
                _areStudentsDataAvailable.value = tempStudents2.isNotEmpty()

            }
        }

    }
    fun setStudentsScoreListAt(
        academicYearIndex: Int,
        className: String,
        sequenceIndex: Int,
        subjectIndex: Int
    ){
        val tempStudentsScoreList = ArrayList<StudentScore>()
        val tempStudentDataList = ArrayList<StudentData>()
        _students.value?.forEach {
            val academicYearData = it.academicYears[academicYearIndex]
//
            if (className == academicYearData.className && academicYearData.subjects!![subjectIndex].doesSubject) {
                val score =
                    academicYearData.subjects[subjectIndex].sequenceScores[sequenceIndex].score
                val studentScore = StudentScore(it.studentId!!, it.studentName!!, score)
                tempStudentsScoreList.add(studentScore)
                tempStudentDataList.add(it)

            }


        }
        _studentsScoreList.value = tempStudentsScoreList
        _studentsDataToUse.value = tempStudentDataList
        initFirstCurrentAndLastStudentIndexes()
//        return tempStudentsScoreList : ArrayList<StudentScore>
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
        lastStudentIndex = _studentsScoreList.value!!.size - 1
        checkIfCurrentIndexEqualFirst()
        checkIfCurrentIndexEqualLast()
    }

    private fun checkIfCurrentIndexEqualFirst() {
        _isCurrentIndexEqualFirst.value = currentStudentIndex == firstStudentIndex
    }

    private fun checkIfCurrentIndexEqualLast() {
        _isCurrentIndexEqualLast.value = currentStudentIndex == lastStudentIndex
    }
    fun updateCurrentStudentIndex(position: Int){
        currentStudentIndex = position
        checkIfCurrentIndexEqualFirst()
        checkIfCurrentIndexEqualLast()
    }
    fun updateStudentScoreAt(
        academicYearIndex: Int,
        className: String,
        sequenceIndex: Int,
        subjectIndex: Int,
        score: Double
    ){
        val studentDataToUpdate = _studentsDataToUse.value!![currentStudentIndex!!]
        studentDataToUpdate.apply {
            academicYears[academicYearIndex].subjects!![subjectIndex].sequenceScores[sequenceIndex].score = score
        }
        updateStudentsScoreListAt(currentStudentIndex!!, score)
        updateStudentsAt(currentStudentIndex!!, studentDataToUpdate)
        _studentDataListChangedAt.value = currentStudentIndex!!
    }

    private fun updateStudentsScoreListAt(currentStudentIndex: Int, score: Double){
        _studentsScoreList.value!![currentStudentIndex].studentScore = score
    }

    private fun updateStudentsAt(currentStudentIndex: Int, studentData: StudentData){
        _students.value!![currentStudentIndex] = studentData
    }

    fun updateDatabase(){
        CoroutineScope(Dispatchers.IO).launch{
            _studentsDataToUse.value!!.forEach {
                database.studentDataDao().updateStudent(it)
            }
        }
    }

}