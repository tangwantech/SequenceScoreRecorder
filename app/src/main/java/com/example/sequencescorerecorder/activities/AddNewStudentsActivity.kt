package com.example.sequencescorerecorder.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.SequenceScoreRecorderConstants
import com.example.sequencescorerecorder.adapters.StudentDatabaseRecyclerAdapter
import com.example.sequencescorerecorder.database.StudentDatabase
import com.example.sequencescorerecorder.viewModels.AddNewStudentsActivityViewModel
import com.google.android.material.textfield.TextInputEditText

private const val STUDENT_CLASS = "studentClass"
class AddNewStudentsActivity : AppCompatActivity() {
    private lateinit var viewModel: AddNewStudentsActivityViewModel
    private lateinit var studentId: TextInputEditText
    private lateinit var studentName: TextInputEditText
    private lateinit var studentGender: AutoCompleteTextView
    //    private lateinit var btnSave: Button
    private lateinit var btnAdd: Button
    private lateinit var rvStudents: RecyclerView
    private lateinit var btnClearCurrentList: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_new_students)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        title = resources.getString(R.string.add_students)
        initViewModel()
        initViews()
        setupViewAdapters()
        setupViewListeners()
        setupViewObservers()
    }

    private fun initViewModel(){
        val schoolName = resources.getStringArray(R.array.schools)[intent.getIntExtra("schoolIndex", 0)]
        val academicYear = resources.getStringArray(R.array.academic_years)[intent.getIntExtra("academicYearIndex", 0)]
        viewModel = ViewModelProvider(this)[AddNewStudentsActivityViewModel::class.java]
        viewModel.initDatabase(com.example.sequencescorerecorder.database.StudentDatabase.getStudentDatabase(this))
        viewModel.setSchoolName(schoolName)
        viewModel.setAcademicYear(academicYear)
        viewModel.setAcademicYearIndex(intent.getIntExtra("academicYearIndex", 0))
        viewModel.setAcademicYearsCount(resources.getStringArray(R.array.academic_years).size)
        viewModel.setStudentClass(resources.getStringArray(R.array.classes)[intent.getIntExtra(SequenceScoreRecorderConstants.SELECTED_CLASS_INDEX, 0)])
        viewModel.setStudentSubjects(resources.getStringArray(R.array.subjects).toList())
        viewModel.setSequences(resources.getStringArray(R.array.sequences).toList())

    }

    private fun initViews() {
        studentId = findViewById(R.id.studentId)
        studentName = findViewById(R.id.studentName)
        studentGender = findViewById(R.id.studentGender)
//        btnSave = view.findViewById(R.id.btnSave)
        btnAdd = findViewById(R.id.btnAdd)
        rvStudents = findViewById(R.id.rvStudentDatabase)
        btnClearCurrentList = findViewById(R.id.btnClearCurrentList)



    }

    private fun setupViewAdapters() {
        val genderAdapter = ArrayAdapter<String>(
            this,
            R.layout.drop_down_item,
            resources.getStringArray(R.array.gender)
        )
        studentGender.setAdapter(genderAdapter)

        val layoutMan = LinearLayoutManager(this)
        layoutMan.orientation = LinearLayoutManager.VERTICAL
        rvStudents.layoutManager = layoutMan
        rvStudents.addItemDecoration(DividerItemDecoration(this, LinearLayoutManager.VERTICAL))

    }

    private fun setupViewObservers() {

        viewModel.studentsDataToDisplay.observe(this, Observer {
            val adapter = StudentDatabaseRecyclerAdapter(this, it)
            rvStudents.adapter = adapter
        })
        viewModel.studentNameAndGender.observe(this, Observer{
            studentName.setText(it.name)
            studentGender.setText(it.gender)
        })

        viewModel.idInExistingListDisplayed.observe(this, Observer {
            if(it != null){
                btnAdd.isEnabled = false
                displayStudentDataExistDialog(it)

            }else{
                btnAdd.isEnabled = true
            }
        })

    }

    private fun setupViewListeners() {

        studentId.doOnTextChanged { text, _, _, _ ->
            viewModel.checkIdInDatabase(text.toString())
        }

        btnClearCurrentList.setOnClickListener {
            viewModel.clearCurrentStudentDataList()
        }

//        btnSave.setOnClickListener {
//            viewModel.writeStudentsDataToDatabase()
//
//        }

        btnAdd.setOnClickListener {
            if (studentId.text!!.isNotEmpty() && studentName.text!!.isNotEmpty() && studentGender.text.isNotEmpty()) {

                if(viewModel.indexOfStudentId.value != null){
                    viewModel.updateOldStudentDataInDatabase(
                        studentName.text.toString(),
                        studentGender.text.toString()
                    )
                }else{
                    viewModel.addNewStudentData(
                        studentId.text.toString(),
                        studentName.text.toString(),
                        studentGender.text.toString())
                }

                clearInputFields()
            }

        }
    }

    private fun displayStudentDataExistDialog(studentId: String) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setMessage("${resources.getString(R.string.id)} $studentId ${resources.getString(R.string.already_exists)}")
            setPositiveButton(resources.getString(R.string.ok)){_, _ ->
            }
        }.create().show()
    }

    private fun clearInputFields() {
        studentId.apply {
            text = null
        }

        studentName.apply {
            text = null

        }

        studentGender.apply {
            text = null

        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

        }
        return super.onOptionsItemSelected(item)
    }


    override fun onPause() {
        super.onPause()
        viewModel.writeStudentsDataToDatabase()
    }
}