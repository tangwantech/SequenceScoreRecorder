package com.example.sequencescorerecorder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.SequenceScoreRecorderConstants
import com.example.sequencescorerecorder.dataModels.StudentScore

class ScoreSheetRecyclerAdapter(private val context: Context, private val studentsScoreList: ArrayList<StudentScore>, private val itemClickListener: OnScoreListItemClickListener) : RecyclerView.Adapter<ScoreSheetRecyclerAdapter.ViewHolder>(){


    inner class ViewHolder(view: View): RecyclerView.ViewHolder(view){
        private val layoutScoreListItem: LinearLayout
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvStudentNumber: TextView = view.findViewById(R.id.tvStudentNumber)
        val tvStudentScore: TextView = view.findViewById(R.id.tvStudentScore)
        init {
            layoutScoreListItem = view.findViewById(R.id.layoutScoreListItem)




            layoutScoreListItem.setOnClickListener {
                itemClickListener.onScoreListItemClicked(adapterPosition)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.score_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        println(studentsScoreList[position].classNumber)
        holder.tvStudentNumber.text = studentsScoreList[position].classNumber
//        holder.tvStudentNumber.text = "${position + 1}"
        holder.tvStudentName.text = studentsScoreList[position].studentName

        if(studentsScoreList[position].studentScore == null){
            holder.tvStudentScore.text = "NA"
            holder.tvStudentScore.setTextColor(context.resources.getColor(R.color.primary_text_color))
        }else{
            holder.tvStudentScore.text = studentsScoreList[position].studentScore.toString()
            studentsScoreList[position].studentScore?.let{
                if(studentsScoreList[position].studentScore!! >= SequenceScoreRecorderConstants.AVERAGE_SCORE){
                    holder.tvStudentScore.setTextColor(context.resources.getColor(R.color.color_pass))
                }else{
                    holder.tvStudentScore.setTextColor(context.resources.getColor(R.color.color_fail))
                }
            }
        }





    }

    override fun getItemCount(): Int {
        return studentsScoreList.size
    }

    interface OnScoreListItemClickListener{
        fun onScoreListItemClicked(position: Int)
    }
}