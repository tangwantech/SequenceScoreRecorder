package com.example.sequencescorerecorder.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.adapters.SubjectCheckListRecyclerAdapter
import com.example.sequencescorerecorder.dataModels.StudentSubjectStateData
import com.example.sequencescorerecorder.viewModels.SubjectManagerViewModel

class SubjectManagerActivity : AppCompatActivity(), SubjectCheckListRecyclerAdapter.CheckBoxListener {
    private lateinit var schoolName: String
    private var academicYearIndex: Int? = null
    private lateinit var className: String
    private var subjectIndex: Int? = null
    private lateinit var viewModel: SubjectManagerViewModel
    private lateinit var autoStudentClass: AutoCompleteTextView
    private lateinit var autoStudentSubject: AutoCompleteTextView
    private lateinit var rvSubjectCheckList: RecyclerView
    private lateinit var btnLoad: Button
    private lateinit var btnSave: Button
    private lateinit var checkboxAll: CheckBox
    private lateinit var tvNumberOfStudents: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_manager)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)



        initViewModel()
        initActivityViews()
        setupActivityViews()
        setupViewObservers()
        setupViewListeners()

    }
    private fun initViewModel(){
        schoolName = resources.getStringArray(R.array.schools)[intent.getIntExtra("schoolIndex", 0)]
        academicYearIndex = intent.getIntExtra("academicYearIndex", 0)
        className = resources.getStringArray(R.array.classes)[intent.getIntExtra("classIndex", 0)]
        subjectIndex = intent.getIntExtra("subjectIndex", 0)
        viewModel = ViewModelProvider(this)[SubjectManagerViewModel::class.java]

        viewModel.initDatabase(this)
        viewModel.setSchoolName(schoolName)
        viewModel.setAcademicYearIndex(academicYearIndex!!)
    }

    private fun initActivityViews(){
        autoStudentClass = findViewById(R.id.autoStudentClass)
        autoStudentSubject = findViewById(R.id.autoStudentSubject)
        rvSubjectCheckList = findViewById(R.id.rvSubjectCheckList)
        btnLoad = findViewById(R.id.btnLoad)
        btnSave = findViewById(R.id.btnSave)
        checkboxAll = findViewById(R.id.checkboxAll)
        tvNumberOfStudents = findViewById(R.id.tvNumberOfStudents)
    }

    private fun setupActivityViews(){
        val autoStudentClassAdapter = ArrayAdapter<String>(this, R.layout.drop_down_item, resources.getStringArray(R.array.classes))
        autoStudentClass.setAdapter(autoStudentClassAdapter)

        val autoStudentSubjectAdapter = ArrayAdapter<String>(this, R.layout.drop_down_item, resources.getStringArray(R.array.subjects))
        autoStudentSubject.setAdapter(autoStudentSubjectAdapter)
    }

    private fun setupActivityRecyclerView(studentSubjectStates: ArrayList<StudentSubjectStateData>){
        val rvLayoutMan = LinearLayoutManager(this)
        rvLayoutMan.orientation = LinearLayoutManager.VERTICAL
        rvSubjectCheckList.apply {
            layoutManager = rvLayoutMan
            addItemDecoration(
                DividerItemDecoration(
                    this@SubjectManagerActivity,
                    LinearLayoutManager.VERTICAL
                )
            )
        }
        val adapter = SubjectCheckListRecyclerAdapter(this, studentSubjectStates, this)
        rvSubjectCheckList.adapter = adapter
    }

    private fun setupViewObservers(){
        viewModel.isClassAndSubjectIndexSet.observe(this, Observer {
            btnLoad.isEnabled = it
        })

        viewModel.studentSubjectStates.observe(this, Observer {
            if(it.isEmpty()){
                Toast.makeText(this, "No data available", Toast.LENGTH_LONG).show()
                btnSave.isEnabled = false
                checkboxAll.apply {
                    isChecked = false
                    isEnabled = false
                }
            }else{
                btnSave.isEnabled = true
                checkboxAll.isEnabled = true
            }
            setupActivityRecyclerView(it)

        })

        viewModel.allOffered.observe(this, Observer {
            rvSubjectCheckList.adapter?.notifyDataSetChanged()
        })

        viewModel.numberOfStudentsTakingSubject.observe(this, Observer{
            tvNumberOfStudents.text = it.toString()

        })

//        viewModel.subjectCheckedPosition.observe(this, Observer {
//            rvSubjectCheckList.adapter?.notifyItemChanged(it)
//        })


//        viewModel.areAllOfStudentsSittingSubject.observe(this, Observer {
//            checkboxAll.isChecked = it
//        })


    }

    private fun setupViewListeners(){
        autoStudentSubject.setOnItemClickListener { _, _, i, _ ->
            viewModel.setStudentSubjectIndex(i)
        }
        autoStudentClass.setOnItemClickListener { _, _, i, _ ->
            viewModel.setStudentClassName(resources.getStringArray(R.array.classes)[i])
        }
        btnLoad.setOnClickListener {
            viewModel.loadStudentsFromDatabaseWhere()
        }
        btnSave.setOnClickListener {
            viewModel.updateDatabase()
        }
        checkboxAll.setOnCheckedChangeListener { _, checkState ->
            viewModel.updateAllOffered(checkState)
        }
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


    override fun onResume() {
        super.onResume()
        title = resources.getString(R.string.subjects_manager)
    }

    override fun onCheck(position: Int, state: Boolean) {
        viewModel.updateStudentSubjectStateAt(academicYearIndex!!, position, state)

    }
}