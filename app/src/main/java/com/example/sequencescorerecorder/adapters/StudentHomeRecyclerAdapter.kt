package com.example.sequencescorerecorder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.dataModels.StudentIdAndNameData

class StudentHomeRecyclerAdapter(
    private val context: Context,
    private val studentIdAndNameData: List<StudentIdAndNameData>,
    private val homeRecyclerItemButtonsClickListener: OnHomeRecyclerItemButtonsClickListener
) : RecyclerView.Adapter<StudentHomeRecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvStudentId: TextView = view.findViewById(R.id.tvStudentId)
        private val btnModify: Button = view.findViewById(R.id.btnModify)
        private val btnDelete: Button = view.findViewById(R.id.btnDelete)

        init {
            btnDelete.setOnClickListener {
                homeRecyclerItemButtonsClickListener.onDeleteButtonClicked(adapterPosition)
            }

            btnModify.setOnClickListener {
                homeRecyclerItemButtonsClickListener.onModifyButtonClicked(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.student_db_home_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvStudentId.text = "ID: ${studentIdAndNameData[position].studentId}"
        holder.tvStudentName.text = "Name: ${studentIdAndNameData[position].studentName}"
    }

    override fun getItemCount(): Int {
        return studentIdAndNameData.size
    }

    interface OnHomeRecyclerItemButtonsClickListener {
        fun onModifyButtonClicked(position: Int)
        fun onDeleteButtonClicked(position: Int)
    }
}