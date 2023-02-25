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
                println("All student data: $allStudentsData")
            }
        }
    }

    fun addStudentDataToTempList(studentData: StudentData){
        tempStudentsData.add(studentData)
        _studentsDataToDisplay.value = tempStudentsData
    }

    fun checkIfStudentIdInDatabase(studentData: StudentData): Boolean{
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
    fun checkIfStudentIdInTempStudentsData(studentData: StudentData): Boolean{
        if(tempStudentsData.isNotEmpty()){
            tempStudentsData.forEach {
//                println(it.studentId == studentData.studentId)
                return it.studentId == studentData.studentId
            }
        }
        return false
    }



    fun clearDatabase(){
        allStudentsData.clear()
        viewModelScope.launch(Dispatchers.IO){
            database.studentDataDao().deleteAllStudents()
        }
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

}