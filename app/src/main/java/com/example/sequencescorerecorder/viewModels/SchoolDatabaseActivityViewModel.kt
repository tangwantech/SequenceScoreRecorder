package com.example.sequencescorerecorder.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequencescorerecorder.dataModels.StudentAcademicYearData
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.dataModels.StudentIdAndNameData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SchoolDatabaseActivityViewModel: ViewModel() {
    private lateinit var database: com.example.sequencescorerecorder.database.StudentDatabase

    private val _schoolName = MutableLiveData<String>()
    val schoolName: LiveData<String> = _schoolName

    private val _schoolIndex = MutableLiveData<Int>()
    val schoolIndex: LiveData<Int> = _schoolIndex

    private val _academicYearIndex = MutableLiveData<Int>()
    val academicYearIndex: LiveData<Int> = _academicYearIndex

    private val _academicYear = MutableLiveData<String>()
    val academicYear: LiveData<String> = _academicYear

    //    MutableLiveData of ArrayList to hold students data for all academic years in database
    private val _studentsDataAllAcademicYears =
        MutableLiveData<ArrayList<StudentData>?>(null)
    val studentsDataAllAcademicYears: LiveData<ArrayList<StudentData>?> =
        _studentsDataAllAcademicYears

    private val _studentsDataCurrentAcademicYear =
        MutableLiveData<ArrayList<StudentAcademicYearData>?>(
            null
        )
    val studentsDataCurrentAcademicYear: LiveData<ArrayList<StudentAcademicYearData>?> =
        _studentsDataCurrentAcademicYear

    //    MutableLiveData of ArrayList to hold student name and id only
    private val _studentsIdAndNameData = MutableLiveData<ArrayList<StudentIdAndNameData>?>(null)
    val studentsIdAndNameData: LiveData<ArrayList<StudentIdAndNameData>?> = _studentsIdAndNameData

    //    MutableLiveData of Int to hold the total number of students for current academic year
    private val _totalNumberOfStudents = MutableLiveData<Int>()
    val totalNumberOfStudents: LiveData<Int> = _totalNumberOfStudents


    private val _deletedItemPosition = MutableLiveData<Int?>()
    val deletedItemPosition: LiveData<Int?> = _deletedItemPosition

    private val _updatedItemPosition = MutableLiveData<Int?>()
    val updatedItemPosition: LiveData<Int?> = _updatedItemPosition

    //    MutableLiveData of Boolean to hold track of whether studentCurrentAcademicYearData has been cleared or not
    private val _isClearDatabaseSuccessful = MutableLiveData<Boolean>()
    val isClearDatabaseSuccessful: LiveData<Boolean> = _isClearDatabaseSuccessful


    private val _sortOptions = MutableLiveData<List<String>>()
//    val sortOptions: LiveData<List<String>> = _sortOptions

    private val _sortOptionIndex = MutableLiveData<Int>()
    val sortOptionIndex: LiveData<Int> = _sortOptionIndex

    private val _selectedClass = MutableLiveData<String?>()

    private val _selectedClassIndex = MutableLiveData<Int>()
    val selectedClassIndex: LiveData<Int> = _selectedClassIndex

    fun initDatabase(database: com.example.sequencescorerecorder.database.StudentDatabase) {
        this.database = database
    }

    fun setSchoolName(name: String) {
        this._schoolName.value = name
    }

    fun setSchoolIndex(index: Int){
        _schoolIndex.value = index
    }

    fun setAcademicYearIndex(index: Int) {
        this._academicYearIndex.value = index
    }

    fun setAcademicYear(year: String) {
        _academicYear.value = year
    }

    fun setSelectedClass(selectedClass: String?) {
        this._selectedClass.value = selectedClass
    }

    fun setSelectedClassIndex(selectedClassIndex: Int){
        _selectedClassIndex.value = selectedClassIndex
    }

    private fun getAllStudentsFromDatabaseInCurrentSchool() {

        viewModelScope.launch(Dispatchers.IO) {
            nullifyData()
            val studentsDataBySchool =
                database.studentDataDao().getStudentsBySchool(_schoolName.value)
//
            var tempStudentsDataBySchool = ArrayList<StudentData>()

            studentsDataBySchool.forEachIndexed { _, studentData ->

                tempStudentsDataBySchool.add(studentData)

            }

            withContext(Dispatchers.Main) {
//                println(tempStudentsDataBySchool)
                if (tempStudentsDataBySchool.isNotEmpty()) {


                    tempStudentsDataBySchool = setStudentsClassNumbersForSelectedClass(tempStudentsDataBySchool)

                    if (_sortOptionIndex.value!! == 0) {
                        tempStudentsDataBySchool.sortBy { studentData -> studentData.studentId?.toInt() }
                    } else {
                        tempStudentsDataBySchool.sortBy { studentData -> studentData.studentName }
                    }
                    _studentsDataAllAcademicYears.value = tempStudentsDataBySchool

                } else {
                    _studentsDataAllAcademicYears.value = ArrayList()
                }
                setStudentsIdsAndNamesDataList()

            }
        }
    }

    private fun setStudentsIdsAndNamesDataList() {
        val tempIdsAndNamesList = ArrayList<StudentIdAndNameData>()
        _studentsDataCurrentAcademicYear.value = ArrayList()
        _studentsDataAllAcademicYears.value?.forEachIndexed { index, studentData ->
            if (studentData.academicYears[_academicYearIndex.value!!].academicYear == _academicYear.value!!) {
                tempIdsAndNamesList.add(
                    StudentIdAndNameData(
                        studentData.studentId!!,
                        studentData.studentName!!
                    )
                )

                val studentAcademicYearData = StudentAcademicYearData(
                    studentId = studentData.studentId,
                    studentName = studentData.studentName,
                    studentGender = studentData.studentGender,
                    academicYear = studentData.academicYears[_academicYearIndex.value!!]
                )

                _studentsDataCurrentAcademicYear.value?.add(studentAcademicYearData)
            }

        }

        _studentsIdAndNameData.value = tempIdsAndNamesList
        updateTotalNumberOfStudents()

    }


    private fun setStudentsClassNumbersForSelectedClass(tempData: ArrayList<StudentData>): ArrayList<StudentData>{
//        println(tempData)
        var studentClassNumber = 0
        tempData.sortBy{studentData -> studentData.studentName }
        tempData.forEachIndexed { index, studentData ->
            if (studentData.academicYears[_academicYearIndex.value!!].academicYear == _academicYear.value && studentData.academicYears[_academicYearIndex.value!!].className == _selectedClass.value) {
                studentClassNumber += 1
                tempData[index].academicYears[_academicYearIndex.value!!].studentClassNumber =
                    studentClassNumber.toString()
                println(studentClassNumber)
            }
        }
//        println(tempData)
        return tempData
    }

    private fun updateTotalNumberOfStudents() {
        _studentsIdAndNameData.value?.let{
            _totalNumberOfStudents.value = it.size
        }

    }

    fun deleteStudentDataAt(position: Int) {

        CoroutineScope(Dispatchers.IO).launch {
            var validAcademicYearsCount = 0
            validAcademicYearsCount =
                _studentsDataAllAcademicYears.value!![position].academicYears.count { it.academicYear != null }
//
            if (validAcademicYearsCount > 1) {
                _studentsDataAllAcademicYears.value!![position].academicYears[_academicYearIndex.value!!].apply {
                    academicYear = null
                    className = null
                    studentClassNumber = null
                    subjects = null
                }
//                println("count: $validAcademicYearsCount temporary delete ${_studentsDataAllAcademicYears.value}")
                database.studentDataDao()
                    .updateStudent(_studentsDataAllAcademicYears.value!![position])
            } else {
//                println("count: $validAcademicYearsCount permanent delete ${_studentsDataAllAcademicYears.value}")
                database.studentDataDao()
                    .deleteStudent(_studentsDataAllAcademicYears.value!![position].studentId)
            }


            refreshDatabase()
            withContext(Dispatchers.Main) {
                _deletedItemPosition.value = position
            }
        }
    }

    fun refreshDatabase() {
        getAllStudentsFromDatabaseInCurrentSchool()

    }

    fun updateStudentData(
        position: Int,
        stdId: String,
        name: String,
        gender: String,
        studentClass: String
    ) {
//

        _studentsDataAllAcademicYears.value!![position].apply {
            studentId = stdId
            studentName = name
            studentGender = gender
            academicYears[_academicYearIndex.value!!].className = studentClass
        }

        _studentsIdAndNameData.value!![position].apply {
            studentId = stdId
            studentName = name

        }

        CoroutineScope(Dispatchers.IO).launch {
            database.studentDataDao().updateStudent(_studentsDataAllAcademicYears.value!![position])
            refreshDatabase()
            withContext(Dispatchers.Main) {
                _updatedItemPosition.value = position

            }
        }
    }

    fun clearDatabase() {
        viewModelScope.launch(Dispatchers.IO) {

            database.studentDataDao().deleteAllStudents()
            refreshDatabase()
            withContext(Dispatchers.Main) {
                _isClearDatabaseSuccessful.value = _studentsDataAllAcademicYears.value!!.isNullOrEmpty()
//                refreshDatabase()
            }

        }
    }

    fun setSortOptionIndex(index: Int) {
        _sortOptionIndex.value = index
    }

    fun getStudentClass(position: Int): String {
        return _studentsDataAllAcademicYears.value!![position].academicYears[_academicYearIndex.value!!].className!!
    }

    fun resetClearDatabaseSuccessful() {
        _isClearDatabaseSuccessful.value = false
    }

    fun updateDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            _studentsDataAllAcademicYears.value?.let {
                it.forEach { studentData ->
                    database.studentDataDao().updateStudent(studentData)
                }
            }
        }
    }

    fun resetDeletedItemPosition() {
        _deletedItemPosition.value = null
    }

    private fun nullifyData(){
        _studentsDataAllAcademicYears.postValue(null)
        _studentsDataCurrentAcademicYear.postValue(null)
        _studentsIdAndNameData.postValue(null)
    }

//    fun getStudentDataAtPosition(position: Int): StudentAcademicYearData{
//        return
//    }
}