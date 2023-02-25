package com.example.sequencescorerecorder.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.database.StudentDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StudentDatabaseEditorFragmentViewModel: ViewModel() {
    private val allStudentsData = ArrayList<StudentData>()
    private lateinit var database: StudentDatabase
    private val tempStudentsData = ArrayList<StudentData>()
    private val _studentsDataToDisplay = MutableLiveData<List<StudentData>>()
    val studentsDataToDisplay: LiveData<List<StudentData>> = _studentsDataToDisplay

    fun initDatabase(studentDatabase: StudentDatabase){
        database = studentDatabase
        getALlStudentDataFrmDatabase()
    }

    private fun getALlStudentDataFrmDatabase() {
        viewModelScope.launch(Dispatchers.IO){
            val temp = database.studentDataDao().getAllStudents()
            withContext(Dispatchers.Main){
                if (temp.isNotEmpty()){
                    temp.forEach { studentData ->
                        allStudentsData.add(studentData)

                    }
                }
            }
        }
    }

    fun addStudentDataToTempList(studentData: StudentData){
        tempStudentsData.add(0, studentData)
        _studentsDataToDisplay.value = tempStudentsData
    }

    fun isStudentIdInDatabase(studentData: StudentData): Boolean{
        if (allStudentsData.isNotEmpty()){
            allStudentsData.forEach {
                if(it.studentId == studentData.studentId){
                    return true
                }
            }
            return false
        }

        return false
    }
    fun isStudentIdInTempStudentsData(studentData: StudentData): Boolean{
        if(tempStudentsData.isNotEmpty()){
            tempStudentsData.forEach {
                return it.studentId == studentData.studentId
            }
        }
        return false
    }

    fun writeStudentsDataToDatabase() {
        CoroutineScope(Dispatchers.IO).launch{
            if(tempStudentsData.isNotEmpty()){
                tempStudentsData.forEach {studentData ->
                    database.studentDataDao().insertStudent(studentData)
                }
            }
        }

    }

    fun clearCurrentStudentDataList() {
        tempStudentsData.clear()
        _studentsDataToDisplay.value = tempStudentsData
    }

}