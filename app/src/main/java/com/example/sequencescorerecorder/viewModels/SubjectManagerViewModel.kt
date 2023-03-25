package com.example.sequencescorerecorder.viewModels

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.dataModels.StudentSubjectStateData
import com.example.sequencescorerecorder.database.StudentDatabase
import kotlinx.coroutines.*


class SubjectManagerViewModel: ViewModel() {
    private lateinit var database: StudentDatabase
    private val _studentsData = MutableLiveData<ArrayList<StudentData>>()
    private val _studentSubjectStates = MutableLiveData<ArrayList<StudentSubjectStateData>>()
    val studentSubjectStates: LiveData<ArrayList<StudentSubjectStateData>> = _studentSubjectStates

    private val _isClassAndSubjectIndexSet = MutableLiveData<Boolean>()
    val isClassAndSubjectIndexSet: LiveData<Boolean> = _isClassAndSubjectIndexSet

    private val _allOffered = MutableLiveData<Boolean>()
    val allOffered: LiveData<Boolean> = _allOffered

    private var studentSubjectIndex: Int? = null
    private var studentClassName: String? = null

    fun initDatabase(context: Context){
        database = StudentDatabase.getStudentDatabase(context)
    }

    fun loadStudentsFromDatabaseWhere(schoolName: String, academicYearIndex: Int){
        viewModelScope.launch(Dispatchers.IO){
            val tempData = database.studentDataDao().getStudentsBySchool(schoolName)
            withContext(Dispatchers.Main){
                val tempStudentData = ArrayList<StudentData>()
                if(tempData.isNotEmpty()){
                    tempData.forEach {
                        if(studentClassName == it.currentClass){
                            tempStudentData.add(it)
                        }
                    }
                }
                tempStudentData.sortBy { studentData ->  studentData.studentName}
                _studentsData.value = tempStudentData
                setStudentSubjectStatesData(academicYearIndex)
            }
        }
    }

    private fun setStudentSubjectStatesData(academicYearIndex: Int){
        val tempStudentSubjectStates = ArrayList<StudentSubjectStateData>()
        _studentsData.value?.forEach { studentData ->
            val studentName = studentData.studentName
            val subject = studentData.academicYears[academicYearIndex].subjects!![studentSubjectIndex!!].subjectName
            val offered = studentData.academicYears[academicYearIndex].subjects!![studentSubjectIndex!!].doesSubject
            val studentClassNumber = studentData.academicYears[academicYearIndex].studentClassNumber!!
            tempStudentSubjectStates.add(StudentSubjectStateData(studentClassNumber, studentName!!, subject, offered))

        }
        _studentSubjectStates.value = tempStudentSubjectStates
    }

    fun updateStudentSubjectStateAt(academicYearIndex: Int, position: Int, offered: Boolean){
        _studentsData.value!![position].academicYears[academicYearIndex].subjects!![studentSubjectIndex!!].doesSubject = offered
    }

    fun updateDatabase(){
        CoroutineScope(Dispatchers.IO).launch{
            _studentsData.value!!.forEach {
                database.studentDataDao().updateStudent(it)
            }

        }
    }

    fun setStudentClassName(className: String){
        studentClassName = className
        checkIfClassAndSubjectIndexSet()
    }

    fun setStudentSubjectIndex(index: Int){
        studentSubjectIndex = index
        checkIfClassAndSubjectIndexSet()
    }

    private fun checkIfClassAndSubjectIndexSet(){
        _isClassAndSubjectIndexSet.value = studentClassName != null && studentSubjectIndex != null
    }

    fun updateAllOffered(academicYearIndex: Int, offered: Boolean) {
        _studentsData.value!!.forEachIndexed { index, studentData ->
            studentData.academicYears[academicYearIndex].subjects!![studentSubjectIndex!!].doesSubject = offered
            _studentSubjectStates.value!![index].offered = offered
        }
        _allOffered.value = offered

    }
}