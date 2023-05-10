package com.example.sequencescorerecorder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.dataModels.StudentIdAndNameData

class StudentDbHomeRecyclerAdapter(
    private val context: Context,
    private var studentIdAndNameData: ArrayList<StudentIdAndNameData>?,
    private val homeRecyclerItemsClickListener: OnHomeRecyclerItemsClickListener
) : RecyclerView.Adapter<StudentDbHomeRecyclerAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvStudentId: TextView = view.findViewById(R.id.tvStudentId)
        private val btnModify: Button = view.findViewById(R.id.btnModify)
        private val btnDelete: Button = view.findViewById(R.id.btnDelete)
        private val rootLayout: ConstraintLayout = view.findViewById(R.id.home_recycler_item_root_layout)

        init {
            btnDelete.setOnClickListener {
                homeRecyclerItemsClickListener.onDeleteButtonClicked(adapterPosition)
            }

            btnModify.setOnClickListener {
                homeRecyclerItemsClickListener.onModifyButtonClicked(adapterPosition)
            }

            rootLayout.setOnClickListener {
                homeRecyclerItemsClickListener.onItemClicked(adapterPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context)
            .inflate(R.layout.student_db_home_recycler_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        studentIdAndNameData?.let{
            holder.tvStudentId.text = "ID: ${it[position].studentId}"
            holder.tvStudentName.text = "Name: ${it[position].studentName}"
        }
//        if(studentIdAndNameData.isNotEmpty()){
//            holder.tvStudentId.text = "ID: ${studentIdAndNameData[position].studentId}"
//            holder.tvStudentName.text = "Name: ${studentIdAndNameData[position].studentName}"
//        }

    }

    override fun getItemCount(): Int {
        if(studentIdAndNameData == null){
            return 0
        }
        return studentIdAndNameData!!.size
    }

    fun updateData(studentIdAndNameData: ArrayList<StudentIdAndNameData>){
        this.studentIdAndNameData = studentIdAndNameData
    }


    interface OnHomeRecyclerItemsClickListener {
        fun onModifyButtonClicked(position: Int)
        fun onDeleteButtonClicked(position: Int)
        fun onItemClicked(position: Int)
    }
}