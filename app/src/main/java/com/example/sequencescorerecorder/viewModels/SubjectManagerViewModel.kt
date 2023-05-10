package com.example.sequencescorerecorder.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.dataModels.StudentSubjectStateData
import com.example.sequencescorerecorder.database.StudentDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class SubjectManagerViewModel : ViewModel() {
    private lateinit var database: com.example.sequencescorerecorder.database.StudentDatabase
    private val _studentsData = MutableLiveData<ArrayList<StudentData>>()
    private val _studentSubjectStates = MutableLiveData<ArrayList<StudentSubjectStateData>>()
    val studentSubjectStates: LiveData<ArrayList<StudentSubjectStateData>> = _studentSubjectStates

    private val _schoolName = MutableLiveData<String>()
    private val _academicYearIndex = MutableLiveData<Int>()

    private val _isClassAndSubjectIndexSet = MutableLiveData<Boolean>()
    val isClassAndSubjectIndexSet: LiveData<Boolean> = _isClassAndSubjectIndexSet

    private val _allOffered = MutableLiveData<Boolean>()
    val allOffered: LiveData<Boolean> = _allOffered

    private var studentSubjectIndex: Int? = null
    private var studentClassName: String? = null

    private val _numberOfStudentsTakingSubject = MutableLiveData<Int>(0)
    val numberOfStudentsTakingSubject: LiveData<Int> = _numberOfStudentsTakingSubject

    private val _areAllOfStudentsSittingSubject = MutableLiveData<Boolean>()
    val areAllOfStudentsSittingSubject: LiveData<Boolean> = _areAllOfStudentsSittingSubject

//    private val _subjectCheckedPosition = MutableLiveData<Int>()
//    val subjectCheckedPosition: LiveData<Int> = _subjectCheckedPosition

    fun initDatabase(context: Context) {
        database = com.example.sequencescorerecorder.database.StudentDatabase.getStudentDatabase(context)
    }

    fun setSchoolName(schoolName: String){
        _schoolName.value = schoolName
    }

    fun setAcademicYearIndex(index: Int){
        _academicYearIndex.value = index
    }

    fun loadStudentsFromDatabaseWhere() {
        viewModelScope.launch(Dispatchers.IO) {
            val tempData = database.studentDataDao().getStudentsBySchool(_schoolName.value)
            withContext(Dispatchers.Main) {
                val tempStudentData = ArrayList<StudentData>()
                if (tempData.isNotEmpty()) {
                    tempData.forEach {
                        if (studentClassName == it.academicYears[_academicYearIndex.value!!].className) {
                            tempStudentData.add(it)
                        }
                    }
                    tempStudentData.sortBy { studentData -> studentData.studentName }
                    _studentsData.value = tempStudentData
//                println(tempStudentData)

                }else{
                    _studentsData.value = ArrayList()
                }
                setStudentSubjectStatesData(_academicYearIndex.value!!)
            }
        }
    }

    private fun setStudentSubjectStatesData(academicYearIndex: Int) {
        val tempStudentSubjectStates = ArrayList<StudentSubjectStateData>()
        _studentsData.value?.forEach { studentData ->
            val studentName = studentData.studentName
            val subject =
                studentData.academicYears[academicYearIndex].subjects!![studentSubjectIndex!!].subjectName
            val offered =
                studentData.academicYears[academicYearIndex].subjects!![studentSubjectIndex!!].doesSubject
            val studentClassNumber =
                studentData.academicYears[academicYearIndex].studentClassNumber!!
            tempStudentSubjectStates.add(
                StudentSubjectStateData(
                    studentClassNumber,
                    studentName!!,
                    subject,
                    offered
                )
            )

        }
        _studentSubjectStates.value = tempStudentSubjectStates
        setStudentsSittingSubjectCount()
    }

    fun updateStudentSubjectStateAt(academicYearIndex: Int, position: Int, offered: Boolean) {
        _studentsData.value!![position].academicYears[academicYearIndex].subjects!![studentSubjectIndex!!].doesSubject =
            offered
        setStudentsSittingSubjectCount()
//

    }

    fun updateDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            _studentsData.value!!.forEach {
                database.studentDataDao().updateStudent(it)
            }

        }
    }

    fun setStudentClassName(className: String) {
        studentClassName = className
        checkIfClassAndSubjectIndexSet()
    }

    fun setStudentSubjectIndex(index: Int) {
        studentSubjectIndex = index
        checkIfClassAndSubjectIndexSet()
    }

    private fun checkIfClassAndSubjectIndexSet() {
        _isClassAndSubjectIndexSet.value = studentClassName != null && studentSubjectIndex != null
    }

    fun updateAllOffered(offered: Boolean) {
        _studentsData.value!!.forEachIndexed { index, studentData ->
            studentData.academicYears[_academicYearIndex.value!!].subjects!![studentSubjectIndex!!].doesSubject =
                offered
            _studentSubjectStates.value!![index].offered = offered
        }
        _allOffered.value = offered
        setStudentsSittingSubjectCount()

    }

    private fun setStudentsSittingSubjectCount(){
        var count = 0
        _studentsData.value?.forEach {
            if(it.academicYears[_academicYearIndex.value!!].subjects!![studentSubjectIndex!!].doesSubject){
                count += 1
            }
        }
        _numberOfStudentsTakingSubject.value = count
    }
}