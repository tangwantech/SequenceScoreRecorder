package com.example.sequencescorerecorder.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequencescorerecorder.dataModels.*
import com.example.sequencescorerecorder.database.StudentDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AddNewStudentsActivityViewModel: ViewModel() {
    private val _studentDataAllAcademicYears = MutableLiveData<ArrayList<StudentData>>(ArrayList())

    private lateinit var database: StudentDatabase

    private val _tempStudentsData = ArrayList<StudentData>()

    private val _studentsDataToDisplay = MutableLiveData<List<StudentData>>()
    val studentsDataToDisplay: LiveData<List<StudentData>> = _studentsDataToDisplay

    private val _schoolName = MutableLiveData<String>()

    private val _academicYear = MutableLiveData<String>()
    val academicYear: LiveData<String> = _academicYear

    private val _academicYearIndex = MutableLiveData<Int>()
    val academicYearIndex: LiveData<Int> = _academicYearIndex

    private val _studentClass = MutableLiveData<String>()
    val studentClass: LiveData<String> = _studentClass

    private val _studentSubjects = MutableLiveData<List<String>>()

    private val _sequences = MutableLiveData<List<String>>()

    private val _studentIdsInSchoolDatabase = MutableLiveData<ArrayList<String>>(ArrayList())

    private val _studentIdsBeingUsedInCurrentAcademicYear =
        MutableLiveData<ArrayList<String>>(ArrayList())

    private val _tempStudentIdsForUseInCurrentAcademicYear =
        MutableLiveData<ArrayList<String>>(ArrayList())

    private val _indexOfStudentId = MutableLiveData<Int?>()
    val indexOfStudentId: LiveData<Int?> = _indexOfStudentId

    private val _studentNameAndGender = MutableLiveData<StudentNameAndGender>()
    val studentNameAndGender: LiveData<StudentNameAndGender> = _studentNameAndGender

    private val _academicYearsCount = MutableLiveData<Int>()

    private val _idInExistingListDisplayed = MutableLiveData<String?>()
    val idInExistingListDisplayed: LiveData<String?> = _idInExistingListDisplayed

    fun initDatabase(studentDatabase: StudentDatabase) {
        database = studentDatabase
        getALlStudentDataFromDatabase()
    }

    fun setSchoolName(schoolName: String) {
        this._schoolName.value = schoolName
    }

    fun setAcademicYear(academicYear: String) {
        this._academicYear.value = academicYear
    }

    fun setAcademicYearIndex(index: Int) {
        _academicYearIndex.value = index
    }

    fun setAcademicYearsCount(count: Int) {
        _academicYearsCount.value = count
    }

    fun setStudentClass(studentClass: String) {
        this._studentClass.value = studentClass
    }

    fun setStudentSubjects(studentSubjects: List<String>) {
        _studentSubjects.value = studentSubjects
    }

    fun setSequences(sequences: List<String>) {
        _sequences.value = sequences
    }

    private fun getALlStudentDataFromDatabase() {
        _tempStudentsData.clear()
        _studentIdsInSchoolDatabase.value?.clear()
        viewModelScope.launch(Dispatchers.IO) {
            val temp = database.studentDataDao().getAllStudents()
            if (temp.isNotEmpty()) {
                temp.forEach { studentData ->
                    _studentIdsInSchoolDatabase.value?.add(studentData.studentId!!)
                    _studentDataAllAcademicYears.value?.add(studentData)
                    if(studentData.academicYears[_academicYearIndex.value!!].academicYear == _academicYear.value!!){
                        _studentIdsBeingUsedInCurrentAcademicYear.value?.add(studentData.studentId!!)
                    }

                }

            }

        }
    }

    fun checkIdInDatabase(studentId: String) {
        when (studentId) {
            in _studentIdsBeingUsedInCurrentAcademicYear.value!! -> {
                _idInExistingListDisplayed.value = studentId
                val studentIndex = _studentIdsInSchoolDatabase.value!!.indexOf(studentId)
                val studentName = _studentDataAllAcademicYears.value!![studentIndex].studentName!!
                val studentGender = _studentDataAllAcademicYears.value!![studentIndex].studentGender!!
                _studentNameAndGender.value = StudentNameAndGender(studentName, studentGender)
            }
            in _studentIdsInSchoolDatabase.value!! -> {
                _idInExistingListDisplayed.value = null
                val studentIndex = _studentIdsInSchoolDatabase.value!!.indexOf(studentId)
                val studentName = _studentDataAllAcademicYears.value!![studentIndex].studentName!!
                val studentGender = _studentDataAllAcademicYears.value!![studentIndex].studentGender!!
                _studentNameAndGender.value = StudentNameAndGender(studentName, studentGender)
                _indexOfStudentId.value = studentIndex

            }
            else -> {
                _idInExistingListDisplayed.value = null
                _studentNameAndGender.value = StudentNameAndGender()
                _indexOfStudentId.value = null
            }
        }

    }

    fun updateOldStudentDataInDatabase(
        studentName: String,
        studentGender: String
    ) {
        val studentData = _studentDataAllAcademicYears.value!![_indexOfStudentId.value!!]
        studentData.apply {
            this.studentName = studentName
            this.studentGender = studentGender
            this.academicYears[_academicYearIndex.value!!].academicYear = _academicYear.value
            this.academicYears[_academicYearIndex.value!!].className = _studentClass.value
            this.academicYears[_academicYearIndex.value!!].subjects = getNewSubjects()
        }
        _tempStudentsData.add(0, studentData)
        _studentsDataToDisplay.value = _tempStudentsData
    }

    private fun getNewSubjects(): ArrayList<SubjectData> {
        val studentSubjects = ArrayList<SubjectData>()
        _studentSubjects.value!!.forEachIndexed { _, subjectName ->
            studentSubjects.add(SubjectData(subjectName, true, getNewSequences()))
        }
        return studentSubjects
    }

    private fun getNewSequences(): ArrayList<SequenceScore> {
        val sequencesScores = ArrayList<SequenceScore>()

        _sequences.value!!.forEach { sequenceName ->
            sequencesScores.add(SequenceScore(sequenceName, null, null))
        }
        return sequencesScores
    }

    private fun getAcademicYears(): ArrayList<AcademicYear> {
        val academicYears = ArrayList<AcademicYear>()
        for (index in 0 until _academicYearsCount.value!!) {
            if (index == _academicYearIndex.value) {
                academicYears.add(
                    AcademicYear(
                        academicYear = _academicYear.value,
                        className = _studentClass.value,
                        subjects = getNewSubjects()
                    )
                )
            } else {
                academicYears.add(AcademicYear())
            }

        }
        return academicYears
    }

    fun addNewStudentData(studentId: String, studentName: String, studentGender: String) {

        val academicYears = getAcademicYears()
        val studentData = StudentData(
            null,
            _schoolName.value,
            studentId,
            studentName,
            studentGender,
            _studentClass.value,
            _academicYear.value,
            academicYears
        )
        _tempStudentIdsForUseInCurrentAcademicYear.value!!.add(0, studentId)
        _tempStudentsData.add(0, studentData)
        _studentsDataToDisplay.value = _tempStudentsData


    }

    fun writeStudentsDataToDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            if (_tempStudentsData.isNotEmpty()) {
                _tempStudentsData.forEach { studentData ->
                    database.studentDataDao().insertStudent(studentData)
                }
            }
        }

    }

    fun clearCurrentStudentDataList() {
        _tempStudentsData.clear()
        _studentsDataToDisplay.value = _tempStudentsData
    }
}