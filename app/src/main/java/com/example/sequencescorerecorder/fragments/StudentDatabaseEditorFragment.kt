package com.example.sequencescorerecorder.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import com.example.sequencescorerecorder.adapters.StudentDatabaseRecyclerAdapter
import com.example.sequencescorerecorder.database.StudentDatabase
import com.example.sequencescorerecorder.viewModels.StudentDatabaseEditorFragmentViewModel
import com.google.android.material.textfield.TextInputEditText

private const val STUDENT_CLASS = "studentClass"
class StudentDatabaseEditorFragment : Fragment() {

    private lateinit var viewModel: StudentDatabaseEditorFragmentViewModel
    private lateinit var studentId: TextInputEditText
    private lateinit var studentName: TextInputEditText
    private lateinit var studentGender: AutoCompleteTextView
//    private lateinit var btnSave: Button
    private lateinit var btnAdd: Button
    private lateinit var rvStudents: RecyclerView
    private lateinit var btnClearCurrentList: Button

    companion object {

        fun newInstance(schoolIndex: Int, academicYearIndex: Int, studentClass: String) = StudentDatabaseEditorFragment().apply {
            val bundle = Bundle().apply {
                putInt("schoolIndex", schoolIndex)
                putInt("academicYearIndex", academicYearIndex)
                putString(STUDENT_CLASS, studentClass)
            }

            arguments = bundle
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_database_editor, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViewModel()
        initViews(view)
        setupViewAdapters()
        setupViewListeners()
        setupViewObservers()

    }

    private fun initViewModel(){
        val schoolName = requireContext().resources.getStringArray(R.array.schools)[requireArguments().getInt("schoolIndex")]
        val academicYear = requireContext().resources.getStringArray(R.array.academic_years)[requireArguments().getInt("academicYearIndex")]
        viewModel = ViewModelProvider(this)[StudentDatabaseEditorFragmentViewModel::class.java]
        viewModel.initDatabase(StudentDatabase.getStudentDatabase(requireContext()))
        viewModel.setSchoolName(schoolName)
        viewModel.setAcademicYear(academicYear)
        viewModel.setAcademicYearIndex(requireArguments().getInt("academicYearIndex"))
        viewModel.setAcademicYearsCount(resources.getStringArray(R.array.academic_years).size)
        viewModel.setStudentClass(requireArguments().getString(STUDENT_CLASS)!!)
        viewModel.setStudentSubjects(requireContext().resources.getStringArray(R.array.subjects).toList())
        viewModel.setSequences(requireContext().resources.getStringArray(R.array.sequences).toList())

    }

    private fun initViews(view: View) {
        studentId = view.findViewById(R.id.studentId)
        studentName = view.findViewById(R.id.studentName)
        studentGender = view.findViewById(R.id.studentGender)
//        btnSave = view.findViewById(R.id.btnSave)
        btnAdd = view.findViewById(R.id.btnAdd)
        rvStudents = view.findViewById(R.id.rvStudentDatabase)
        btnClearCurrentList = view.findViewById(R.id.btnClearCurrentList)



    }

    private fun setupViewAdapters() {
        val genderAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.drop_down_item,
            resources.getStringArray(R.array.gender)
        )
        studentGender.setAdapter(genderAdapter)

        val layoutMan = LinearLayoutManager(requireContext())
        layoutMan.orientation = LinearLayoutManager.VERTICAL
        rvStudents.layoutManager = layoutMan
        rvStudents.addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))

    }

    private fun setupViewObservers() {

        viewModel.studentsDataToDisplay.observe(viewLifecycleOwner, Observer {
            val adapter = StudentDatabaseRecyclerAdapter(requireContext(), it)
            rvStudents.adapter = adapter
        })
        viewModel.studentNameAndGender.observe(viewLifecycleOwner, Observer{
            studentName.setText(it.name)
            studentGender.setText(it.gender)
        })

        viewModel.idInExistingListDisplayed.observe(viewLifecycleOwner, Observer {
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
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setMessage("${requireContext().resources.getString(R.string.id)} $studentId ${requireContext().resources.getString(R.string.already_exists)}")
            setPositiveButton(requireContext().resources.getString(R.string.ok)){_, _ ->
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

    override fun onPause() {
        super.onPause()
        viewModel.writeStudentsDataToDatabase()
    }

}
