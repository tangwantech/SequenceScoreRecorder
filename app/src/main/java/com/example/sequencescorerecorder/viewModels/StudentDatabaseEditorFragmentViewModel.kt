package com.example.sequencescorerecorder.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sequencescorerecorder.dataModels.AcademicYear
import com.example.sequencescorerecorder.dataModels.SequenceScore
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.dataModels.SubjectData
import com.example.sequencescorerecorder.database.StudentDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentDatabaseEditorFragmentViewModel: ViewModel() {
    private val _allStudentsData = MutableLiveData<ArrayList<StudentData>>(ArrayList())
    private lateinit var database: StudentDatabase
    private val tempStudentsData = ArrayList<StudentData>()
    private val _studentsDataToDisplay = MutableLiveData<List<StudentData>>()
    val studentsDataToDisplay: LiveData<List<StudentData>> = _studentsDataToDisplay

    private val _schoolName = MutableLiveData<String>()
//    val schoolName: LiveData<String> = _schoolName

    private val _academicYear = MutableLiveData<String>()
    val academicYear: LiveData<String> = _academicYear

    private val _academicYearIndex = MutableLiveData<Int>()
    val academicYearIndex: LiveData<Int> = _academicYearIndex

    private val _studentClass = MutableLiveData<String>()
    val studentClass: LiveData<String> = _studentClass

    private val _studentSubjects = MutableLiveData<List<String>>()

    private val _sequences = MutableLiveData<List<String>>()

    fun initDatabase(studentDatabase: StudentDatabase){
        database = studentDatabase
        getALlStudentDataFromDatabase()
    }

    fun setSchoolName(schoolName: String){
        this._schoolName.value = schoolName
    }

    fun setAcademicYear(academicYear: String){
        this._academicYear.value = academicYear
    }

    fun setAcademicYearIndex(index: Int){
        _academicYearIndex.value = index
    }

    fun setStudentClass(studentClass: String){
        this._studentClass.value = studentClass
    }

    fun setStudentSubjects(studentSubjects: List<String>){
        _studentSubjects.value = studentSubjects
    }

    fun setSequences(sequences: List<String>){
        _sequences.value = sequences
    }

    private fun getALlStudentDataFromDatabase() {
        viewModelScope.launch(Dispatchers.IO){
            val temp = database.studentDataDao().getAllStudents()
            if (temp.isNotEmpty()){
                temp.forEach { studentData ->

                    _allStudentsData.value!!.add(studentData)
                }
//                _allStudentsData.value!!.sortedBy { studentData -> studentData.studentName }
            }

        }
    }

    fun addStudentData(studentId: String, studentName: String, studentGender: String){

        val studentSubjects = ArrayList<SubjectData>()
        val sequencesScores = ArrayList<SequenceScore>()
        _sequences.value!!.forEach { sequenceName ->
            sequencesScores.add(SequenceScore(sequenceName, null, null))
        }

        _studentSubjects.value!!.forEachIndexed { _, subjectName ->
            studentSubjects.add(SubjectData(subjectName, true, sequencesScores))
        }

        val academicYears = ArrayList<AcademicYear>()
        academicYears.add(AcademicYear(academicYear = _academicYear.value, className = _studentClass.value, subjects = studentSubjects))


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
        tempStudentsData.add(0, studentData)

        _studentsDataToDisplay.value = tempStudentsData

    }

    fun isStudentIdInDatabase(studentId: String): Boolean{
        if (_allStudentsData.value!!.isNotEmpty()){
            _allStudentsData.value!!.forEach {
                if(it.studentId == studentId){
                    return true
                }
            }
            return false
        }

        return false
    }
    fun isStudentIdInTempStudentsData(studentId: String): Boolean{
        if(tempStudentsData.isNotEmpty()){
            tempStudentsData.forEach {
                return it.studentId == studentId
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