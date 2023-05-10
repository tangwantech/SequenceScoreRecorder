package com.example.sequencescorerecorder.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.SequenceScoreRecorderConstants
import com.example.sequencescorerecorder.adapters.ScoreSheetRecyclerAdapter
import com.example.sequencescorerecorder.dataModels.StudentScore
import com.example.sequencescorerecorder.viewModels.ScoreEditorActivityViewModel
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class ScoreEditorActivity : AppCompatActivity(),
    ScoreSheetRecyclerAdapter.OnScoreListItemClickListener {
    private lateinit var schoolName: String
    private var academicYearIndex: Int? = null
    private lateinit var className: String
    private var sequenceIndex: Int? = null
    private var subjectIndex: Int? = null

    private lateinit var viewModel: ScoreEditorActivityViewModel

    private lateinit var tvClassName: TextView
    private lateinit var tvSubject: TextView
    private lateinit var tvStudentName: TextView
    private lateinit var textInputLayout: TextInputLayout
    private lateinit var inputStudentScore: TextInputEditText
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var btnUpdate: Button
    private lateinit var btnStatistics: Button
    private lateinit var rvScoreSheet: RecyclerView
    private lateinit var tvNumberOfStudents: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_editor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        schoolName = resources.getStringArray(R.array.schools)[intent.getIntExtra("schoolIndex", 0)]


        initViewModel()
        initActivityViews()
        setupActivityViewObservers()
        setupActivityViewListeners()
    }

    private fun initViewModel() {
        academicYearIndex = intent.getIntExtra("academicYearIndex", 0)
        className = resources.getStringArray(R.array.classes)[intent.getIntExtra("classIndex", 0)]
        sequenceIndex = intent.getIntExtra("sequenceIndex", 0)
        subjectIndex = intent.getIntExtra("subjectIndex", 0)

        viewModel = ViewModelProvider(this)[ScoreEditorActivityViewModel::class.java]
        viewModel.initDatabase(this)
        viewModel.setAcademicYearIndex(academicYearIndex!!)
        viewModel.setClassName(className)
        viewModel.setSequenceIndex(sequenceIndex!!)
        viewModel.setSubjectIndex(subjectIndex!!)
        viewModel.loadStudentsWhereSchool(schoolName)

    }

    private fun initActivityViews() {
        val subject = "Subject: ${resources.getStringArray(R.array.subjects)[subjectIndex!!]}"
        val className = "Class: ${resources.getStringArray(R.array.classes)[intent.getIntExtra(" classIndex ", 0)]}"
        tvClassName = findViewById(R.id.tvClassName)
        tvClassName.text = className
        tvSubject = findViewById(R.id.tvSubject)
        tvSubject.text = subject
        textInputLayout = findViewById(R.id.textInputLayout)
        tvStudentName = findViewById(R.id.tvStudentName)
        inputStudentScore = findViewById(R.id.inputStudentScore)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        btnUpdate = findViewById(R.id.btnUpdate)
        btnStatistics = findViewById(R.id.btnStatistics)
        rvScoreSheet = findViewById(R.id.rvScoreSheet)
        tvNumberOfStudents = findViewById(R.id.tvNumberOfStudents)

    }

    private fun updateActivityViews(position: Int) {
        inputStudentScore.setText("")
        tvStudentName.text =
            "${position + 1}. ${viewModel.studentsScoreList.value!![position].studentName}"

        viewModel.studentsScoreList.value!![position].studentScore?.let {
            inputStudentScore.setText(viewModel.studentsScoreList.value!![position].studentScore.toString())
        }

    }

    private fun setupRecyclerView(studentsScoreList: ArrayList<StudentScore>) {
        val rvLayoutMan = LinearLayoutManager(this).apply {
            orientation = LinearLayoutManager.VERTICAL
        }
        rvScoreSheet.apply {
            layoutManager = rvLayoutMan
            addItemDecoration(
                DividerItemDecoration(
                    this@ScoreEditorActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
        val adapter = ScoreSheetRecyclerAdapter(this, studentsScoreList, this)
        rvScoreSheet.adapter = adapter


    }

    private fun setupActivityViewObservers() {

        viewModel.studentsScoreList.observe(this, Observer {
            if(it.isNotEmpty()){
                setupRecyclerView(it)
                updateActivityViews(0)
            }
        })

        viewModel.isCurrentIndexEqualFirst.observe(this, Observer {
            btnPrevious.isEnabled = !it
        })

        viewModel.isCurrentIndexEqualLast.observe(this, Observer {
            btnNext.isEnabled = !it
        })

        viewModel.studentDataListChangedAt.observe(this, Observer{
            rvScoreSheet.adapter?.notifyItemChanged(it)
        })

        viewModel.numberOfStudentsRegisteredSubject.observe(this, Observer {
            tvNumberOfStudents.text = it.toString()
            inputStudentScore.apply {
                isEnabled = it != 0
                isFocusable = it != 0
            }

            btnUpdate.isEnabled = it != 0

        })

        viewModel.numberSat.observe(this, Observer {
            btnStatistics.isEnabled = it > 0
        })


    }

    private fun setupActivityViewListeners() {
        inputStudentScore.doOnTextChanged { text, _, _, _ ->
            if(text.toString().isNotEmpty()){
                if(text.toString().toDouble() > 20.0){
                    btnUpdate.isEnabled = false
                    textInputLayout.error = "Invalid score"

                }else{
                    textInputLayout.error = null
                    btnUpdate.isEnabled = true
                }
            }
//            else{
//                btnUpdate.isEnabled = false
//            }

        }
        btnNext.setOnClickListener {
            val currentIndex = viewModel.incrementCurrentStudentIndex()
            updateActivityViews(currentIndex)


        }
        btnPrevious.setOnClickListener {
            val currentIndex = viewModel.decrementCurrentStudentIndex()
            updateActivityViews(currentIndex)

        }

        btnUpdate.setOnClickListener {
            if(inputStudentScore.text.toString().isNotEmpty()){
                updateStudentScoreAt(academicYearIndex!!, sequenceIndex!!, subjectIndex!!, inputStudentScore.text.toString().toDouble())

            }else{
                updateStudentScoreAt(academicYearIndex!!, sequenceIndex!!, subjectIndex!!, null)

            }

        }

        btnStatistics.setOnClickListener {
            displayStatisticsDialog()
        }

//        inputStudentScore.doOnTextChanged { text, _, _, _ ->
//            btnUpdate.isEnabled = text!!.isNotEmpty()
//        }

    }

    override fun onScoreListItemClicked(position: Int) {
//        Toast.makeText(this, "Item position: $position", Toast.LENGTH_LONG).show()
        viewModel.updateCurrentStudentIndex(position)
        updateActivityViews(position)
    }

    override fun onResume() {
        super.onResume()
        val sequence = resources.getStringArray(R.array.sequences)[sequenceIndex!!]
        title = sequence
//        title = "${resources.getStringArray(R.array.classes)[intent.getIntExtra(" classIndex ", 0)]} ${resources.getString(R.string.score_sheet)}"

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                exitActivity()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun exitActivity() {
        finish()
    }

    private fun updateStudentScoreAt(
        academicYearIndex: Int,
        sequenceIndex: Int,
        subjectIndex: Int,
        score: Double?
    ) {
        viewModel.updateStudentScoreAt(academicYearIndex, sequenceIndex, subjectIndex, score)
    }

    private fun displayStatisticsDialog(){
        val view = layoutInflater.inflate(R.layout.statistics_dialog, null)
        val tvNumberRegistered: TextView = view.findViewById(R.id.tvNumberRegistered)
        val tvNumberSat: TextView = view.findViewById(R.id.tvNumberSat)
        val tvNumberPassed: TextView = view.findViewById(R.id.tvNumberPassed)
        val tvPercentagePassed: TextView = view.findViewById(R.id.tvPercentagePassed)


        tvNumberRegistered.text =  "Number Registered: ${viewModel.numberOfStudentsRegisteredSubject.value}"
        tvNumberSat.text = "Number sat: ${viewModel.numberSat.value}"
        tvNumberPassed.text = "Number passed: ${viewModel.numberPassed.value}"
        tvPercentagePassed.text = "Percentage passed: ${viewModel.percentagePassed.value}"

        if(viewModel.percentagePassed.value!! >= SequenceScoreRecorderConstants.AVERAGE_PERCENTAGE ){
            tvPercentagePassed.setTextColor(resources.getColor(R.color.color_pass))
        }else{
            tvPercentagePassed.setTextColor(resources.getColor(R.color.color_fail))
        }


        val statDialog = AlertDialog.Builder(this).apply{
            setTitle(resources.getString(R.string.statistics))
            setView(view)
            setPositiveButton(resources.getString(R.string.ok)){_, _ ->

            }
        }.create()
        statDialog.show()
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateDatabase()
    }
}
