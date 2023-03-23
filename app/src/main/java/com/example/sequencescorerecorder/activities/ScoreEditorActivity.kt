package com.example.sequencescorerecorder.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
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
//    private lateinit var tvSubjectName: TextView
    private lateinit var tvStudentName: TextView
    private lateinit var inputStudentScore: TextInputEditText
    private lateinit var btnNext: Button
    private lateinit var btnPrevious: Button
    private lateinit var btnUpdate: Button
    private lateinit var rvScoreSheet: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_score_editor)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        schoolName = resources.getStringArray(R.array.schools)[intent.getIntExtra("schoolIndex", 0)]
        academicYearIndex = intent.getIntExtra("academicYearIndex", 0)
        className = resources.getStringArray(R.array.classes)[intent.getIntExtra("classIndex", 0)]
        sequenceIndex = intent.getIntExtra("sequenceIndex", 0)
        subjectIndex = intent.getIntExtra("subjectIndex", 0)

        initViewModel()
        initActivityViews()
        setupRecyclerView()
        setupActivityViewObservers()
        setupActivityViewListeners()
    }

    private fun initViewModel() {
        viewModel = ViewModelProvider(this)[ScoreEditorActivityViewModel::class.java]
        viewModel.initDatabase(this)
        viewModel.getStudentsWhereSchool(schoolName)
    }

    private fun initActivityViews() {
        val sequence = resources.getStringArray(R.array.sequences)[sequenceIndex!!]
        val subject = resources.getStringArray(R.array.subjects)[subjectIndex!!]
        tvSequenceName = findViewById(R.id.tvSequenceName)
        tvSequenceName.text = "$sequence, $subject"
//        tvSubjectName = findViewById(R.id.tvSubjectName)
//        tvSubjectName.text =
//            "Subject: ${resources.getStringArray(R.array.subjects)[subjectIndex!!]}"

        tvStudentName = findViewById(R.id.tvStudentName)
        inputStudentScore = findViewById(R.id.inputStudentScore)
        btnPrevious = findViewById(R.id.btnPrevious)
        btnNext = findViewById(R.id.btnNext)
        btnUpdate = findViewById(R.id.btnUpdate)
        rvScoreSheet = findViewById(R.id.rvScoreSheet)

    }

    private fun updateActivityViews(position: Int) {
        inputStudentScore.setText("")
        tvStudentName.text =
            "${position + 1}. ${viewModel.studentsScoreList.value!![position].studentName}"

        viewModel.studentsScoreList.value!![position].studentScore?.let {
            inputStudentScore.setText(viewModel.studentsScoreList.value!![position].studentScore.toString())
        }

    }

    private fun setupRecyclerView() {
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


    }

    private fun setupRecyclerAdapter() {

        val studentsScoreList = viewModel.studentsScoreList.value!!
        val adapter = ScoreSheetRecyclerAdapter(this, studentsScoreList, this)
        rvScoreSheet.adapter = adapter
    }

    private fun setupActivityViewObservers() {
        viewModel.areStudentsDataAvailable.observe(this, Observer {
            if (it) {
                viewModel.setStudentsScoreListAt(academicYearIndex!!, className, sequenceIndex!!, subjectIndex!!)
            }
        })

        viewModel.studentsScoreList.observe(this, Observer {
            if(it.isNotEmpty()){
                setupRecyclerAdapter()
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
