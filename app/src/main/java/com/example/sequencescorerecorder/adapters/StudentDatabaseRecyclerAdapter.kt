package com.example.sequencescorerecorder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.dataModels.StudentData

class StudentDatabaseRecyclerAdapter(private val context: Context, private val listStudentData: List<StudentData>): RecyclerView.Adapter<StudentDatabaseRecyclerAdapter.ViewHolder>() {
//    private var listStudentData = ArrayList<StudentData>()
//    init {
//        for (index in 0..10){
//            listStudentData.add(StudentData(null, index.toString()))
//        }
//
//    }
    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        val studentId: TextView = view.findViewById(R.id.studentId)
        val studentName: TextView = view.findViewById(R.id.studentName)
        val studentGender: TextView = view.findViewById(R.id.studentGender)
        val studentClass: TextView = view.findViewById(R.id.studentClass)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.student_db_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(position >= 0){
            holder.studentId.text = "ID: ${listStudentData[position].studentId}"
            holder.studentName.text = "Name: ${listStudentData[position].studentName}"
            holder.studentGender.text = "Gender: ${ listStudentData[position].studentGender }"
            holder.studentClass.text = "Class: ${listStudentData[position].currentClass}"
        }

    }

    override fun getItemCount(): Int {
        return listStudentData.size
    }

//    fun setStudentData(studentData: ArrayList<StudentData>){
//        listStudentData = studentData
//        print(listStudentData)
//    }
}