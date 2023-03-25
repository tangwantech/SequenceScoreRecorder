package com.example.sequencescorerecorder.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.database.StudentDatabase
import kotlinx.coroutines.*


class StudentDatabaseHomeFragmentViewModel : ViewModel() {
    private lateinit var database: StudentDatabase
    private val _schoolName = MutableLiveData<String>()
//    val schoolName: LiveData<String> = _schoolName

    private val _academicYearIndex = MutableLiveData<Int>()
    val academicYearIndex: LiveData<Int> = _academicYearIndex

    private val _allStudentData = MutableLiveData<ArrayList<StudentData>?>()
    val allStudentData: LiveData<ArrayList<StudentData>?> = _allStudentData
    private val _totalNumberOfStudents = MutableLiveData<Int>()
    val totalNumberOfStudents: LiveData<Int> = _totalNumberOfStudents

    private val _deletedItemPosition = MutableLiveData<Int?>()
    val deletedItemPosition: LiveData<Int?> = _deletedItemPosition

    private val _updatedItemPosition = MutableLiveData<Int?>()
    val updatedItemPosition: LiveData<Int?> = _updatedItemPosition

    private val _isClearDatabaseSuccessful = MutableLiveData<Boolean?>()
    val isClearDatabaseSuccessful: LiveData<Boolean?> = _isClearDatabaseSuccessful

    private val _sortOptions = MutableLiveData<List<String>>()
    val sortOptions: LiveData<List<String>> = _sortOptions

    private val _sortOptionIndex = MutableLiveData<Int>()
    val sortOptionIndex: LiveData<Int> = _sortOptionIndex

    private val _selectedClass = MutableLiveData<String?>()
//    private var _selectedClass:String? = null


    fun initDatabase(database: StudentDatabase){
        this.database = database
    }

    fun setSchoolName(name: String){
        this._schoolName.value = name
    }

    fun setAcademicYearIndex(index: Int){
        this._academicYearIndex.value = index
    }

    fun setSelectedClass(selectedClass: String?){
        this._selectedClass.value = selectedClass
//        println(_selectedClass.value)
    }
    private fun getAllStudentsFromDatabaseInCurrentSchool(){
        viewModelScope.launch(Dispatchers.IO){
            val studentsData = database.studentDataDao().getStudentsBySchool(_schoolName.value)
            val tempStudentsData = ArrayList<StudentData>()

            studentsData.forEachIndexed { _, studentData ->
                tempStudentsData.add(studentData)
            }
            tempStudentsData.sortBy { studentData -> studentData.studentName }

            withContext(Dispatchers.Main){
                tempStudentsData.forEachIndexed { index, studentData ->
//                    println(_selectedClass.value)
//                    println(studentData.academicYears[_academicYearIndex.value!!].className)
                    if(studentData.academicYears[_academicYearIndex.value!!].className == _selectedClass.value){

                        studentData.academicYears[_academicYearIndex.value!!].studentClassNumber = (index + 1).toString()
                        tempStudentsData[index] = studentData
                    }
                }


                if(_sortOptionIndex.value!! == 0){
                    tempStudentsData.sortBy { studentData ->  studentData.studentId}
                }else{
                    tempStudentsData.sortBy { studentData -> studentData.studentName }
                }

                _allStudentData.value = tempStudentsData
                println(_allStudentData.value)
                updateTotalNumberOfStudents(tempStudentsData)
            }
        }
    }

    private fun updateTotalNumberOfStudents(allStudents: List<StudentData>){
        _totalNumberOfStudents.value = allStudents.size
    }

    fun deleteStudentDataAt(position: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = database.studentDataDao().deleteStudent(_allStudentData.value!![position])
            if (id != null){
                refreshDatabase()
            }
        }
    }

    fun refreshDatabase(){
        getAllStudentsFromDatabaseInCurrentSchool()

    }

    fun updateStudentData(position: Int, stdId: String, name: String, gender: String, _class: String) {
//

        _allStudentData.value!![position].apply {
            studentId = stdId
            studentName = name
            studentGender = gender
            currentClass = _class
        }
        CoroutineScope(Dispatchers.IO).launch {
            val id = database.studentDataDao().updateStudent(_allStudentData.value!![position])
            withContext(Dispatchers.Main){
                if(id != null){
                    _updatedItemPosition.value = position
                }
            }
        }
    }

    fun clearDatabase(){
        viewModelScope.launch(Dispatchers.IO){
            database.studentDataDao().deleteAllStudents()
            refreshDatabase()
            withContext(Dispatchers.Main){

                _isClearDatabaseSuccessful.value = true

            }

        }
    }

    fun setSortOptionIndex(index: Int){
        _sortOptionIndex.value = index
    }

    fun updateDatabase(){
        CoroutineScope(Dispatchers.IO).launch{
            _allStudentData.value?.forEach {
                database.studentDataDao().updateStudent(it)
            }
        }

    }


}