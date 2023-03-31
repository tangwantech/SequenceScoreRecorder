package com.example.sequencescorerecorder.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.adapters.ScoreSheetRecyclerAdapter
import com.example.sequencescorerecorder.dataModels.StudentScore
import com.example.sequencescorerecorder.viewModels.ScoreEditorActivityViewModel
import com.google.android.material.textfield.TextInputEditText

class ScoreEditorActivity : AppCompatActivity(),
    ScoreSheetRecyclerAdapter.OnScoreListItemClickListener {
    private lateinit var schoolName: String
    private var academicYearIndex: Int? = null
    private lateinit var className: String
    private var sequenceIndex: Int? = null
    private var subjectIndex: Int? = null

    private lateinit var viewModel: ScoreEditorActivityViewModel

    private lateinit var tvSequenceName: TextView
    private lateinit var tvStudentName: TextView
    private lateinit var inputStudentScore: TextInputEditText
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var btnUpdate: Button
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
        val sequence = resources.getStringArray(R.array.sequences)[sequenceIndex!!]
        val subject = resources.getStringArray(R.array.subjects)[subjectIndex!!]
        tvSequenceName = findViewById(R.id.tvSequenceName)
        tvSequenceName.text = "$sequence, $subject"

        tvStudentName = findViewById(R.id.tvStudentName)
        inputStudentScore = findViewById(R.id.inputStudentScore)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        btnUpdate = findViewById(R.id.btnUpdate)
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

    }

    private fun setupActivityViewListeners() {
        btnNext.setOnClickListener {
            val currentIndex = viewModel.incrementCurrentStudentIndex()
            updateActivityViews(currentIndex)


        }
        btnPrevious.setOnClickListener {
            val currentIndex = viewModel.decrementCurrentStudentIndex()
            updateActivityViews(currentIndex)

        }

        btnUpdate.setOnClickListener {
            updateStudentScoreAt(academicYearIndex!!, className, sequenceIndex!!, subjectIndex!!, inputStudentScore.text.toString().toDouble())

        }

        inputStudentScore.doOnTextChanged { text, _, _, _ ->
            btnUpdate.isEnabled = text!!.isNotEmpty()
        }

    }

    override fun onScoreListItemClicked(position: Int) {
//        Toast.makeText(this, "Item position: $position", Toast.LENGTH_LONG).show()
        viewModel.updateCurrentStudentIndex(position)
        updateActivityViews(position)
    }

    override fun onResume() {
        super.onResume()
        title = "${resources.getStringArray(R.array.classes)[intent.getIntExtra(" classIndex ", 0)]} ${resources.getString(R.string.score_sheet)}"

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
        className: String,
        sequenceIndex: Int,
        subjectIndex: Int,
        score: Double
    ) {
        viewModel.updateStudentScoreAt(academicYearIndex, className, sequenceIndex, subjectIndex, score)
    }

    override fun onStop() {
        viewModel.updateDatabase()
        super.onStop()
    }
}
