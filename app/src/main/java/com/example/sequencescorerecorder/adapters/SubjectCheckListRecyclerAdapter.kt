package com.example.sequencescorerecorder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.dataModels.StudentSubjectStateData

class SubjectCheckListRecyclerAdapter(
    private val context: Context,
    private val subjectStates: ArrayList<StudentSubjectStateData>,
    private val checkboxListener: CheckBoxListener
) : RecyclerView.Adapter<SubjectCheckListRecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStudentNumber: TextView = view.findViewById(R.id.tvStudentNumber)
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val checkboxSubject: CheckBox = view.findViewById(R.id.checkboxSubject)

        init {
            checkboxSubject.setOnCheckedChangeListener { _, b ->
                checkboxListener.onCheck(adapterPosition, b)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.subject_check_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(subjectStates.isNotEmpty()){
            holder.tvStudentNumber.text = "${subjectStates[position].studentClassNumber}"
            holder.tvStudentName.text = "${subjectStates[position].studentName}"
            holder.checkboxSubject.isChecked = subjectStates[position].offered
        }

    }

    override fun getItemCount(): Int {
        return subjectStates.size
    }

    interface CheckBoxListener {
        fun onCheck(position: Int, state: Boolean)
    }

}