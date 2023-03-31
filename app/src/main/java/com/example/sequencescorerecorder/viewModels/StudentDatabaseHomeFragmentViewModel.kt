package com.example.sequencescorerecorder.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequencescorerecorder.dataModels.StudentAcademicYearData
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.dataModels.StudentIdAndNameData
import com.example.sequencescorerecorder.database.StudentDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class StudentDatabaseHomeFragmentViewModel : ViewModel() {
    private lateinit var database: StudentDatabase
    private val _schoolName = MutableLiveData<String>()
    val schoolName: LiveData<String> = _schoolName

    private val _academicYearIndex = MutableLiveData<Int>()
    val academicYearIndex: LiveData<Int> = _academicYearIndex

    private val _academicYear = MutableLiveData<String>()
    val academicYear: LiveData<String> = _academicYear

    //    MutableLiveData of ArrayList to hold students data for all academic years in database
    private val _studentsDataAllAcademicYears =
        MutableLiveData<ArrayList<StudentData>?>(ArrayList())
    val studentsDataAllAcademicYears: LiveData<ArrayList<StudentData>?> =
        _studentsDataAllAcademicYears

    private val _studentsDataCurrentAcademicYear =
        MutableLiveData<ArrayList<StudentAcademicYearData>>(
            ArrayList()
        )
    val studentsDataCurrentAcademicYear: LiveData<ArrayList<StudentAcademicYearData>> =
        _studentsDataCurrentAcademicYear

    //    MutableLiveData of ArrayList to hold student name and id only
    private val _studentsIdAndNameData = MutableLiveData<ArrayList<StudentIdAndNameData>>()
    val studentsIdAndNameData: LiveData<ArrayList<StudentIdAndNameData>> = _studentsIdAndNameData

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

    fun initDatabase(database: StudentDatabase) {
        this.database = database
    }

    fun setSchoolName(name: String) {
        this._schoolName.value = name
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

    private fun getAllStudentsFromDatabaseInCurrentSchool() {
        _studentsDataAllAcademicYears.value?.clear()
        _studentsDataCurrentAcademicYear.value?.clear()
        _studentsIdAndNameData.value?.clear()
        viewModelScope.launch(Dispatchers.IO) {
            val studentsDataBySchool =
                database.studentDataDao().getStudentsBySchool(_schoolName.value)
            var tempStudentsDataBySchool = ArrayList<StudentData>()

            studentsDataBySchool.forEachIndexed { _, studentData ->

                tempStudentsDataBySchool.add(studentData)

            }
            withContext(Dispatchers.Main) {
                if (tempStudentsDataBySchool.isNotEmpty()) {
                    tempStudentsDataBySchool = setStudentsClassNumbersForSelectedClass(tempStudentsDataBySchool)
                    if (_sortOptionIndex.value!! == 0) {
                        tempStudentsDataBySchool.sortBy { studentData -> studentData.studentId }
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
        var studentClassNumber = 0
        tempData.sortBy{studentData -> studentData.studentName }
        tempData.forEachIndexed { index, studentData ->
            if (studentData.academicYears[_academicYearIndex.value!!].academicYear == _academicYear.value!! && studentData.academicYears[_academicYearIndex.value!!].className == _selectedClass.value) {
                studentClassNumber += 1
                tempData[index].academicYears[_academicYearIndex.value!!].studentClassNumber =
                    studentClassNumber.toString()
            }
        }
        return tempData
    }

    private fun updateTotalNumberOfStudents() {
        _totalNumberOfStudents.value = _studentsIdAndNameData.value!!.size
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
                _isClearDatabaseSuccessful.value = _studentsDataAllAcademicYears.value!!.isEmpty()
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

}