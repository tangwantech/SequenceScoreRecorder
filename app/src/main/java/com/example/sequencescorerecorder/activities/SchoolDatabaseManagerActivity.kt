package com.example.sequencescorerecorder.activities

import android.app.AlertDialog
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.SequenceScoreRecorderConstants
import com.example.sequencescorerecorder.adapters.StudentDbHomeRecyclerAdapter
import com.example.sequencescorerecorder.dataModels.StudentIdAndNameData
import com.example.sequencescorerecorder.database.StudentDatabase
import com.example.sequencescorerecorder.viewModels.SchoolDatabaseActivityViewModel
import com.google.android.material.textfield.TextInputEditText

private const val SCHOOL_DATABASE_ACTIVITY = "SchoolDatabaseActivity"
class SchoolDatabaseManagerActivity : AppCompatActivity(), StudentDbHomeRecyclerAdapter.OnHomeRecyclerItemsClickListener {

    private lateinit var viewModel: SchoolDatabaseActivityViewModel
    private lateinit var btnNewStudent: Button
    private lateinit var rvStudentDbHomeRecycler: RecyclerView
    private lateinit var adapter: StudentDbHomeRecyclerAdapter
    private lateinit var rvLayout: LinearLayout
    private lateinit var tvNoDataAvailable: TextView
    private lateinit var tvTotalNumberOfStudents: TextView
    private lateinit var btnClearDatabase: Button
    private lateinit var btnRefresh: Button
    private lateinit var autoCompleteSort: AutoCompleteTextView
    private lateinit var tvSchool: TextView
    private lateinit var tvAcademicYear: TextView
    private lateinit var pref: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_school_database_manager)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        pref = getSharedPreferences(SCHOOL_DATABASE_ACTIVITY, MODE_PRIVATE)

        initialize()
    }

    private fun initialize(){
        initViewModel()
        initViews()
        setupRecyclerView()
        setupViewListeners()
        setupViewObservers()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }

        }
        return true
    }

    private fun initViewModel() {
        viewModel =
            ViewModelProvider(this)[SchoolDatabaseActivityViewModel::class.java]
        viewModel.initDatabase(
            com.example.sequencescorerecorder.database.StudentDatabase.getStudentDatabase(
                this
            )
        )
        val schoolName = resources.getStringArray(R.array.schools)[intent.getIntExtra(SequenceScoreRecorderConstants.SCHOOL_INDEX, 0)]
        val schoolIndex = intent.getIntExtra(SequenceScoreRecorderConstants.SCHOOL_INDEX, 0)
        val academicYear = resources.getStringArray(R.array.academic_years)[intent.getIntExtra(SequenceScoreRecorderConstants.ACADEMIC_YEAR_INDEX, 0)]
        val academicYearIndex = intent.getIntExtra(SequenceScoreRecorderConstants.ACADEMIC_YEAR_INDEX, 0)
        viewModel.setSchoolName(schoolName)
        viewModel.setSchoolIndex(schoolIndex)
        viewModel.setAcademicYear(academicYear)
        viewModel.setAcademicYearIndex(academicYearIndex)
        viewModel.setSortOptionIndex(pref.getInt(SequenceScoreRecorderConstants.SORT_INDEX, 0))

//        viewModel.setSelectedClass(pref.getString("lastSelectedClass", null))

    }

    private fun initViews() {
        btnNewStudent = findViewById(R.id.btnNewStudent)
        rvStudentDbHomeRecycler = findViewById(R.id.rvStudentDbHome)
        tvNoDataAvailable = findViewById(R.id.tvNoDataAvailable)
        tvTotalNumberOfStudents = findViewById(R.id.tvTotalNumberOfStudents)
        btnClearDatabase = findViewById(R.id.btnClearDatabase)
        rvLayout = findViewById(R.id.rvLayout)
        btnRefresh = findViewById(R.id.btnRefresh)
        autoCompleteSort = findViewById(R.id.autoCompleteSort)
        tvSchool = findViewById(R.id.tvSchool)
        tvSchool.text = "${resources.getString(R.string.school)}: ${viewModel.schoolName.value}"
        tvAcademicYear = findViewById(R.id.tvAcademicYear)
        tvAcademicYear.text = "${resources.getString(R.string.academic_year)}: ${viewModel.academicYear.value}"

    }

    private fun setupViewListeners() {

        btnRefresh.setOnClickListener {
            viewModel.refreshDatabase()
//            initialize()
        }
        btnClearDatabase.setOnClickListener {
            displayMessageToClearDatabase()
        }

        autoCompleteSort.setOnItemClickListener { _, _, i, _ ->
//            println("index: $i")
            viewModel.setSortOptionIndex(i)
            viewModel.refreshDatabase()
            saveSortIndexToSharedPreference(i)

        }

        btnNewStudent.setOnClickListener {

            val classSelectionDialog = AlertDialog.Builder(this)
            val classSelectionView = layoutInflater.inflate(R.layout.class_selection, null)
            val studentClassAutoComplete: AutoCompleteTextView =
                classSelectionView.findViewById(R.id.studentClassSelection)
            val studentClassesAdapter = ArrayAdapter<String>(
                this,
                R.layout.drop_down_item,
                resources.getStringArray(R.array.classes)
            )
            studentClassAutoComplete.setAdapter(studentClassesAdapter)
            studentClassAutoComplete.setOnItemClickListener { _, _, i, _ ->

                viewModel.setSelectedClass(studentClassAutoComplete.text.toString())
                viewModel.setSelectedClassIndex(i)
            }

            classSelectionDialog.apply {
                setView(classSelectionView)
                setPositiveButton(resources.getString(R.string.ok)) { _, _ ->
                    if (studentClassAutoComplete.text.isNotEmpty()) {
                        gotoAddNewStudentActivity()
                    }
                }
                setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

                }
            }.create().show()


        }


    }

    private fun setupViewObservers() {

        viewModel.totalNumberOfStudents.observe(
            this,
            Observer {
                tvTotalNumberOfStudents.text = it.toString()
            })

        viewModel.studentsIdAndNameData.observe(
            this,
            Observer { studentsIdAndNameDataList ->
//                println("studentsIdAndNameDataList: $studentsIdAndNameDataList")
                studentsIdAndNameDataList?.let {
                    if (it.isNotEmpty()) {
                        showRecyclerView()

                    } else {
                        hideRecyclerView()
                    }
                    adapter.updateData(it)
                    rvStudentDbHomeRecycler.adapter?.notifyDataSetChanged()
                }


            })

        viewModel.deletedItemPosition.observe(
            this,
            Observer {
                it?.let {
                    Toast.makeText(
                        this,
                        resources.getString(R.string.data_deleted_successfully),
                        Toast.LENGTH_LONG
                    ).show()
                    rvStudentDbHomeRecycler.adapter?.notifyItemRemoved(it)
                    viewModel.resetDeletedItemPosition()
                }

            })

        viewModel.updatedItemPosition.observe(this, Observer {
            Toast.makeText(
                this,
                resources.getString(R.string.data_updated_successfully),
                Toast.LENGTH_LONG
            ).show()
            rvStudentDbHomeRecycler.adapter?.notifyItemChanged(it!!)
        })

        viewModel.isClearDatabaseSuccessful.observe(
            this,
            Observer {
                if(it){

                    hideRecyclerView()
//                    println("Data cleared...")

                    Toast.makeText(
                        this,
                        resources.getString(R.string.database_erased_successfully),
                        Toast.LENGTH_LONG
                    ).show()
                    viewModel.resetClearDatabaseSuccessful()

                }
            })

    }

    private fun setupRecyclerView() {
//        studentIdAndNameDataList: ArrayList<StudentIdAndNameData>
        val layoutMan = LinearLayoutManager(this)
        layoutMan.orientation = LinearLayoutManager.VERTICAL
        rvStudentDbHomeRecycler.layoutManager = layoutMan
        rvStudentDbHomeRecycler.addItemDecoration(
            DividerItemDecoration(
                this,
                LinearLayoutManager.VERTICAL
            )
        )
        adapter = StudentDbHomeRecyclerAdapter(
            this,
            viewModel.studentsIdAndNameData.value,
            this
        )
        rvStudentDbHomeRecycler.adapter = adapter
        rvStudentDbHomeRecycler.setHasFixedSize(true)
//        rvStudentDbHomeRecycler.adapter?.notifyDataSetChanged()
//

    }
    private fun setupAutoCompleteViews() {
        autoCompleteSort.setText(resources.getStringArray(R.array.sort_options)[viewModel.sortOptionIndex.value!!])
        val autoSortAdapter = ArrayAdapter<String>(
            this,
            R.layout.drop_down_item,
            resources.getStringArray(R.array.sort_options)
        )
        autoCompleteSort.setAdapter(autoSortAdapter)

    }

    private fun gotoAddNewStudentActivity() {
        val intent = Intent(this, AddNewStudentsActivity::class.java)
        intent.apply {
            putExtra(SequenceScoreRecorderConstants.SCHOOL_INDEX, viewModel.schoolIndex.value)
            putExtra(SequenceScoreRecorderConstants.ACADEMIC_YEAR_INDEX, viewModel.academicYearIndex.value)
            putExtra(SequenceScoreRecorderConstants.SELECTED_CLASS_INDEX, viewModel.selectedClassIndex.value)
        }
        startActivity(intent)
    }

    private fun saveSortIndexToSharedPreference(sortIndex: Int){
        val editor = pref.edit()
        editor.apply {
            putInt(SequenceScoreRecorderConstants.SORT_INDEX, sortIndex)
            apply()
        }
    }
    private fun showRecyclerView(){
        rvLayout.visibility = View.VISIBLE
        tvNoDataAvailable.visibility = View.GONE
        btnClearDatabase.isEnabled = true
    }
    private fun hideRecyclerView(){
        rvLayout.visibility = View.GONE
        tvNoDataAvailable.visibility = View.VISIBLE
        btnClearDatabase.isEnabled = false
    }

    private fun displayMessageToClearDatabase() {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.apply {
            setTitle(resources.getString(R.string.clear_database))
            setMessage(resources.getString(R.string.message_to_clear_database))
            setPositiveButton(resources.getString(R.string._continue)) { _, _ ->
                viewModel.clearDatabase()
            }
            setNegativeButton(resources.getString(R.string.cancel)) { _, _ -> }
        }.create().show()
    }

    private fun displayStudentDataToModifyDialog(position: Int) {
        val oldStudentData = viewModel.studentsDataAllAcademicYears.value!![position]
//        println("Student data to update ${studentDatabaseHomeFragmentViewModel.allStudentData.value!![position]}")
        val view = layoutInflater.inflate(R.layout.item_to_modify_dialog_view, null)
        val idToModify: TextInputEditText = view.findViewById(R.id.studentId)
        val nameToModify: TextInputEditText = view.findViewById(R.id.studentName)
        val genderToModifyAutoText: AutoCompleteTextView = view.findViewById(R.id.studentGender)
        val classToModifyAutoText: AutoCompleteTextView = view.findViewById(R.id.studentClass)

        idToModify.setText(oldStudentData.studentId)
        nameToModify.setText(oldStudentData.studentName)

        val genderToModifyAdapter = ArrayAdapter<String>(
            this,
            R.layout.drop_down_item,
            resources.getStringArray(R.array.gender)
        )
        val classToModifyAdapter = ArrayAdapter<String>(
            this,
            R.layout.drop_down_item,
            resources.getStringArray(R.array.classes)
        )

        genderToModifyAutoText.setText(oldStudentData.studentGender)
        genderToModifyAutoText.setAdapter(genderToModifyAdapter)

        classToModifyAutoText.setText(oldStudentData.currentClass)
        classToModifyAutoText.setAdapter(classToModifyAdapter)

        val modifyStudentDataDialog = AlertDialog.Builder(this)
        modifyStudentDataDialog.apply {
            setTitle(resources.getString(R.string.modify_data))
            setView(view)
            setPositiveButton(resources.getString(R.string.ok)) { _, _ ->

                viewModel.updateStudentData(
                    position,
                    idToModify.text.toString(),
                    nameToModify.text.toString(),
                    genderToModifyAutoText.text.toString(),
                    classToModifyAutoText.text.toString()
                )

            }
            setNegativeButton(resources.getString(R.string.cancel)) { _, _ ->

            }
        }.create().show()
    }

    private fun displayDeleteStudentDataDialog(position: Int) {
        val view = layoutInflater.inflate(R.layout.item_to_delete_dialog_view, null)
        val tvIdToDelete: TextView = view.findViewById(R.id.tvIdToDelete)
        val tvNameToDelete: TextView = view.findViewById(R.id.tvNameToDelete)

        val studentId =
            viewModel.studentsDataAllAcademicYears.value!![position].studentId
        val studentName =
            viewModel.studentsDataAllAcademicYears.value!![position].studentName

        tvIdToDelete.text = "${resources.getString(R.string.id)} $studentId"
        tvNameToDelete.text = "${resources.getString(R.string.name)} $studentName"


        val deleteStudentDataDialog = AlertDialog.Builder(this)
        deleteStudentDataDialog.apply {
            setView(view)
            setTitle(resources.getString(R.string.delete))
            setPositiveButton(resources.getString(R.string.delete)) { _, _ ->
                viewModel.deleteStudentDataAt(position)
            }
            setNegativeButton(resources.getString(R.string.ok)) { _, _ ->

            }
        }.create().show()
    }

    private fun displayStudentDetailsDialog(position: Int) {
//        println(viewModel.studentsDataCurrentAcademicYear.value)
        viewModel.studentsDataCurrentAcademicYear.value?.let{
            val studentData = it[position]

            val view = layoutInflater.inflate(R.layout.student_details, null)

            val studentId: TextView = view.findViewById(R.id.tvIdDetails)
            val studentName: TextView = view.findViewById(R.id.tvNameDetails)
            val studentGender: TextView = view.findViewById(R.id.tvGenderDetails)
            val studentClass: TextView = view.findViewById(R.id.tvClassDetails)

            studentId.text =
                "${resources.getString(R.string.id)} ${studentData.studentId}"
            studentName.text =
                "${resources.getString(R.string.name)} ${studentData.studentName}"
            studentName.visibility = View.GONE
            studentGender.text =
                "${resources.getString(R.string.gender)} ${studentData.studentGender}"
            studentClass.text =
                "${resources.getString(R.string._class)} ${studentData.academicYear?.className}"

            val studentDetailsDialog = AlertDialog.Builder(this)
            studentDetailsDialog.apply {
                setView(view)
                setTitle(studentData.studentName?.uppercase())
                setPositiveButton(resources.getString(R.string.ok)) { _, _ -> }
            }.create().show()
        }


    }


    override fun onModifyButtonClicked(position: Int) {
        displayStudentDataToModifyDialog(position)
    }


    override fun onDeleteButtonClicked(position: Int) {
        displayDeleteStudentDataDialog(position)
    }

    override fun onItemClicked(position: Int) {
        displayStudentDetailsDialog(position)
    }

    override fun onResume() {
        super.onResume()
        title = resources.getString(R.string.students_database)
//        initialize()
        setupAutoCompleteViews()
        println("OnResume")
        viewModel.refreshDatabase()
    }

    override fun onPause() {
        super.onPause()
        viewModel.updateDatabase()
    }

}