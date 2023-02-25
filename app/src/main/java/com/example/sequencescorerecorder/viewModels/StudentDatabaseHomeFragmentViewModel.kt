package com.example.sequencescorerecorder.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.dataModels.StudentIdAndNameData
import com.example.sequencescorerecorder.database.StudentDatabase
import kotlinx.coroutines.*


class StudentDatabaseHomeFragmentViewModel : ViewModel() {
    private lateinit var database: StudentDatabase
    private val _allStudentData = MutableLiveData<ArrayList<StudentData>>()
    val allStudentData: LiveData<ArrayList<StudentData>> = _allStudentData
    private val _totalNumberOfStudents = MutableLiveData<Int>()
    val totalNumberOfStudents: LiveData<Int> = _totalNumberOfStudents

    private val _allStudentIdsAndNamesData = MutableLiveData<List<StudentIdAndNameData>>()
    val allStudentIdsAndNamesData: LiveData<List<StudentIdAndNameData>> = _allStudentIdsAndNamesData

    private val _isDeleteSuccessful = MutableLiveData<Boolean?>()
    val isDeleteSuccessful: LiveData<Boolean?> = _isDeleteSuccessful

    private val _isUpdateSuccessful = MutableLiveData<Boolean?>()
    val isUpdateSuccessful: LiveData<Boolean?> = _isUpdateSuccessful

    fun initDatabase(database: StudentDatabase){
        this.database = database

    }

    fun getAllStudentsFromDatabase(){
        viewModelScope.launch(Dispatchers.IO){
            val studentsData = database.studentDataDao().getAllStudents()
            val tempStudentsData = ArrayList<StudentData>()
            studentsData.forEachIndexed { _, studentData ->
                tempStudentsData.add(studentData)
            }
            _allStudentData.postValue(tempStudentsData)
            updateTotalNumberOfStudents(tempStudentsData)
            setAllStudentIdsAndNamesData(tempStudentsData)
            withContext(Dispatchers.Main){
                println(tempStudentsData)
            }
        }
    }

    private fun setAllStudentIdsAndNamesData(studentsData: ArrayList<StudentData>){
        val tempStudentIdsAndNames = ArrayList<StudentIdAndNameData>()
        studentsData.forEach {
            tempStudentIdsAndNames.add(StudentIdAndNameData(it.studentId!!, it.studentName!!))
        }
        _allStudentIdsAndNamesData.postValue(tempStudentIdsAndNames)
    }

    private fun updateTotalNumberOfStudents(allStudents: List<StudentData>){
        _totalNumberOfStudents.postValue(allStudents.size)
    }

    fun deleteStudentDataAt(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val studentDataToDelete = _allStudentData.value!![position]
            val id = database.studentDataDao().deleteStudent(studentDataToDelete)
            if (id != null){
                _isDeleteSuccessful.postValue(true)
            }else{
                _isDeleteSuccessful.postValue(false)
            }
        }
    }

    fun refreshDatabase(){
        getAllStudentsFromDatabase()
        _isDeleteSuccessful.value = null
        _isUpdateSuccessful.value = null
    }

    fun updateStudentData(studentDataToUpdate: StudentData) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = database.studentDataDao().updateStudent(studentDataToUpdate)
            if(id != null){
                _isUpdateSuccessful.postValue(true)
            }

        }
    }

    fun clearDatabase(){
        viewModelScope.launch(Dispatchers.IO){
            database.studentDataDao().deleteAllStudents()
            withContext(Dispatchers.Main){
                refreshDatabase()
            }

        }
    }


}