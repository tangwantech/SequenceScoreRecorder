package com.example.sequencescorerecorder.fragments

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.adapters.StudentDatabaseRecyclerAdapter
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.database.StudentDatabase
import com.example.sequencescorerecorder.viewModels.StudentDatabaseEditorFragmentViewModel
import com.google.android.material.textfield.TextInputEditText

private const val STUDENT_CLASS = "studentClass"
class StudentDatabaseEditorFragment : Fragment() {

    private lateinit var studentDatabaseEditorFragmentViewModel: StudentDatabaseEditorFragmentViewModel
    private lateinit var studentId: TextInputEditText
    private lateinit var studentName: TextInputEditText
    private lateinit var studentGender: AutoCompleteTextView
    private lateinit var btnFinish: Button
    private lateinit var btnAdd: Button
    private lateinit var rvStudents: RecyclerView
    private lateinit var btnClearCurrentList: Button
    private lateinit var onRequestToNavigateToStudentDataBaseHomeListener: OnRequestToNavigateToStudentDataBaseHomeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        studentDatabaseEditorFragmentViewModel = ViewModelProvider(this)[StudentDatabaseEditorFragmentViewModel::class.java]
        studentDatabaseEditorFragmentViewModel.initDatabase(StudentDatabase.getStudentDatabase(requireContext()))
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is OnRequestToNavigateToStudentDataBaseHomeListener){
            onRequestToNavigateToStudentDataBaseHomeListener = context
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
        initViews(view)
        setupViewAdapters()
        setupViewListeners()
        setupViewObservers()

    }

    private fun initViews(view: View) {
        studentId = view.findViewById(R.id.studentId)
        studentName = view.findViewById(R.id.studentName)
        studentGender = view.findViewById(R.id.studentGender)
        btnFinish = view.findViewById(R.id.btnFinish)
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
        studentDatabaseEditorFragmentViewModel.studentsDataToDisplay.observe(viewLifecycleOwner, Observer {
            val adapter = StudentDatabaseRecyclerAdapter(requireContext(), it)
            rvStudents.adapter = adapter
        })
    }

    private fun setupViewListeners() {

        btnClearCurrentList.setOnClickListener {
            studentDatabaseEditorFragmentViewModel.clearCurrentStudentDataList()
        }

        btnFinish.setOnClickListener {
            onDestroy()
            onRequestToNavigateToStudentDataBaseHomeListener.onRequestToNavigateToStudentDataBaseHome()
        }

        btnAdd.setOnClickListener {
            if (studentId.text!!.isNotEmpty() && studentName.text!!.isNotEmpty() && studentGender.text.isNotEmpty()) {
                val studentData = StudentData(
                    null,
                    studentId.text.toString(),
                    studentName.text.toString(),
                    studentGender.text.toString(),
                    requireArguments().getString(STUDENT_CLASS)
                )
                val isIdInDatabase = studentDatabaseEditorFragmentViewModel.isStudentIdInDatabase(studentData)
                val isIdInTempData = studentDatabaseEditorFragmentViewModel.isStudentIdInTempStudentsData(studentData)
                if(isIdInDatabase || isIdInTempData){
                    displayStudentDataExistDialog(studentData)
                }else{
                    studentDatabaseEditorFragmentViewModel.addStudentDataToTempList(studentData)
                    clearInputFields()
                }
            }

        }
    }

    private fun displayStudentDataExistDialog(studentData: StudentData) {
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setMessage("${requireContext().resources.getString(R.string.id)} ${studentData.studentId} ${requireContext().resources.getString(R.string.already_exists)}")
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

    override fun onResume() {
        super.onResume()
    }

    override fun onStop() {
        studentDatabaseEditorFragmentViewModel.writeStudentsDataToDatabase()
        super.onStop()
    }

    companion object {

        fun newInstance(studentClass: String) = StudentDatabaseEditorFragment().apply {
            val bundle = Bundle()
            bundle.putString(STUDENT_CLASS, studentClass)
            arguments = bundle
        }

    }
}
interface OnRequestToNavigateToStudentDataBaseHomeListener{
    fun onRequestToNavigateToStudentDataBaseHome()
}