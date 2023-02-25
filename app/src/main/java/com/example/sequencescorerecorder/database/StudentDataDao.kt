package com.example.sequencescorerecorder.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.sequencescorerecorder.dataModels.StudentData

@Dao
interface StudentDataDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStudent(studentData: StudentData)

//    updates a student in database at id
    @Update()
    fun updateStudent(studentData: StudentData): Int?

//    get a student from database with studentId
    @Query("SELECT * FROM student_data_table WHERE student_id LIKE :studentId")
    fun getStudentById(studentId: String?): StudentData?

    //    get all students from database with studentClass
    @Query("SELECT * FROM student_data_table WHERE student_class LIKE :studentClass")
    fun getStudentsByClass(studentClass: String?): List<StudentData>

//    removes a student with studentId from database
    @Query("DELETE FROM student_data_table WHERE student_id LIKE :studentId")
    fun deleteStudent(studentId: String?)

//    removes a student from database with id
    @Query("DELETE FROM student_data_table WHERE student_class LIKE :studentClass")
    fun deleteStudentByClass(studentClass: String?)

//    clears the entire database
    @Query("DELETE FROM student_data_table")
    fun deleteAllStudents()

    @Query("SELECT * FROM STUDENT_DATA_TABLE")
    fun getAllStudents(): List<StudentData>

    @Delete()
    fun deleteStudent(studentData: StudentData): Int?
}