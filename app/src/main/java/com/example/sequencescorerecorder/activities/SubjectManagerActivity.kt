package com.example.sequencescorerecorder.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
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
    private lateinit var btnOk: Button
    private lateinit var btnSave: Button
    private lateinit var checkboxAll: CheckBox

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_manager)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        schoolName = resources.getStringArray(R.array.schools)[intent.getIntExtra("schoolIndex", 0)]
        academicYearIndex = intent.getIntExtra("academicYearIndex", 0)
        className = resources.getStringArray(R.array.classes)[intent.getIntExtra("classIndex", 0)]
        subjectIndex = intent.getIntExtra("subjectIndex", 0)

        initViewModel()
        initActivityViews()
        setupActivityViews()
        setupViewObservers()
        setupViewListeners()

    }
    private fun initViewModel(){
        viewModel = ViewModelProvider(this)[SubjectManagerViewModel::class.java]
        viewModel.initDatabase(this)
    }

    private fun initActivityViews(){
        autoStudentClass = findViewById(R.id.autoStudentClass)
        autoStudentSubject = findViewById(R.id.autoStudentSubject)
        rvSubjectCheckList = findViewById(R.id.rvSubjectCheckList)
        btnOk = findViewById(R.id.btnOk)
        btnSave = findViewById(R.id.btnSave)
        checkboxAll = findViewById(R.id.checkboxAll)
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
            btnOk.isEnabled = it
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
    }

    private fun setupViewListeners(){
        autoStudentSubject.setOnItemClickListener { _, _, i, _ ->
            viewModel.setStudentSubjectIndex(i)
        }
        autoStudentClass.setOnItemClickListener { _, _, i, _ ->
            viewModel.setStudentClassName(resources.getStringArray(R.array.classes)[i])
        }
        btnOk.setOnClickListener {
            viewModel.loadStudentsFromDatabaseWhere(schoolName, academicYearIndex!!)
        }
        btnSave.setOnClickListener {
            viewModel.updateDatabase()
        }
        checkboxAll.setOnCheckedChangeListener { _, checkState ->
            viewModel.updateAllOffered(academicYearIndex!!, checkState)
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
//        println("${position}, ${state}")
        viewModel.updateStudentSubjectStateAt(academicYearIndex!!, position, state)
    }
}