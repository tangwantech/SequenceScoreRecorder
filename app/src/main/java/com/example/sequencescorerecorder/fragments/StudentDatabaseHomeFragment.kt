package com.example.sequencescorerecorder.fragments

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.adapters.StudentHomeRecyclerAdapter
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.database.StudentDatabase
import com.example.sequencescorerecorder.viewModels.StudentDatabaseHomeFragmentViewModel
import com.google.android.material.textfield.TextInputEditText

class StudentDatabaseHomeFragment : Fragment(),
    StudentHomeRecyclerAdapter.OnHomeRecyclerItemButtonsClickListener {

    private lateinit var studentDatabaseHomeFragmentViewModel: StudentDatabaseHomeFragmentViewModel
    private lateinit var btnNewStudent: Button
    private lateinit var rvStudentDbHomeRecycler: RecyclerView
    private lateinit var tvNoDataAvailable: TextView
    private lateinit var tvTotalNumberOfStudents: TextView
//    private lateinit var studentClass: AutoCompleteTextView

    private lateinit var onRequestToNavigateToStudentDataBaseEditorListener: OnRequestToNavigateToStudentDataBaseEditorListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRequestToNavigateToStudentDataBaseEditorListener) {
            onRequestToNavigateToStudentDataBaseEditorListener = context
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_student_database_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        studentDatabaseHomeFragmentViewModel =
            ViewModelProvider(this)[StudentDatabaseHomeFragmentViewModel::class.java]
        studentDatabaseHomeFragmentViewModel.initDatabase(
            StudentDatabase.getStudentDatabase(
                requireContext()
            )
        )

        initViews(view)
        setupViewAdapters()
        setupViewListeners()
        setupViewObservers()
    }

    private fun initViews(view: View) {
        btnNewStudent = view.findViewById(R.id.btnNewStudent)
        rvStudentDbHomeRecycler = view.findViewById(R.id.rvStudentDbHome)
        tvNoDataAvailable = view.findViewById(R.id.tvNoDataAvailable)
        tvTotalNumberOfStudents = view.findViewById(R.id.tvTotalNumberOfStudents)
//        studentClass = view.findViewById(R.id.studentClass)
    }

    private fun setupViewListeners() {
        btnNewStudent.setOnClickListener {

            val classSelectionDialog = AlertDialog.Builder(requireContext())
            val classSelectionView =
                requireActivity().layoutInflater.inflate(R.layout.class_selection, null)
            val studentClass: AutoCompleteTextView =
                classSelectionView.findViewById(R.id.studentClassSelection)
            val studentClassesAdapter = ArrayAdapter<String>(
                requireContext(),
                R.layout.drop_down_item,
                resources.getStringArray(R.array.classes)
            )
            studentClass.setAdapter(studentClassesAdapter)

            classSelectionDialog.apply {
                setView(classSelectionView)
                setPositiveButton("OK") { _, _ ->
                    if (studentClass.text.isNotEmpty()) {
                        onRequestToNavigateToStudentDataBaseEditorListener.navigateToStudentDataBaseEditor(
                            studentClass.text.toString()
                        )
                    }
                }
                setNegativeButton("Cancel") { btn, _ ->

                }
            }.create().show()
        }


    }

    private fun setupViewAdapters() {

        val layoutMan = LinearLayoutManager(requireContext())
        layoutMan.orientation = LinearLayoutManager.VERTICAL
        rvStudentDbHomeRecycler.layoutManager = layoutMan
        rvStudentDbHomeRecycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
        studentDatabaseHomeFragmentViewModel.allStudentIdsAndNamesData.observe(
            viewLifecycleOwner,
            Observer { allStudentsIdsAndNamesData ->
                if (allStudentsIdsAndNamesData.isNotEmpty()) {
                    rvStudentDbHomeRecycler.visibility = View.VISIBLE
                    tvNoDataAvailable.visibility = View.GONE
                    val adapter = StudentHomeRecyclerAdapter(
                        requireContext(),
                        allStudentsIdsAndNamesData,
                        this
                    )
                    rvStudentDbHomeRecycler.adapter = adapter
                } else {
                    rvStudentDbHomeRecycler.visibility = View.GONE
                    tvNoDataAvailable.visibility = View.VISIBLE
                }

            })


    }

    private fun setupViewObservers() {
        studentDatabaseHomeFragmentViewModel.totalNumberOfStudents.observe(
            viewLifecycleOwner,
            Observer {
                tvTotalNumberOfStudents.text = it.toString()
            })

        studentDatabaseHomeFragmentViewModel.isDeleteSuccessful.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    if (it) {
                        Toast.makeText(
                            requireContext(),
                            "Data Deleted successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        studentDatabaseHomeFragmentViewModel.refreshDatabase()
                    }
                }

            })

        studentDatabaseHomeFragmentViewModel.isUpdateSuccessful.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    if (it) {
                        Toast.makeText(
                            requireContext(),
                            "Data updated successfully",
                            Toast.LENGTH_LONG
                        ).show()
                        studentDatabaseHomeFragmentViewModel.refreshDatabase()
                    }
                }
            })
    }


    override fun onResume() {
        super.onResume()
        studentDatabaseHomeFragmentViewModel.getAllStudentsFromDatabase()
    }


    companion object {

        fun newInstance() = StudentDatabaseHomeFragment()
    }

    override fun onModifyButtonClicked(position: Int) {
        displayStudentDataToModifyDialog(position)
    }


    override fun onDeleteButtonClicked(position: Int) {
        displayDeleteStudentDataDialog(position)
    }

    private fun displayDeleteStudentDataDialog(position: Int) {
        val view =
            requireActivity().layoutInflater.inflate(R.layout.item_to_delete_dialog_view, null)
        val tvIdToDelete: TextView = view.findViewById(R.id.tvIdToDelete)
        val tvNameToDelete: TextView = view.findViewById(R.id.tvNameToDelete)

        val studentIdAndNameData =
            studentDatabaseHomeFragmentViewModel.allStudentIdsAndNamesData.value!![position]
        tvIdToDelete.text = "ID: ${studentIdAndNameData.studentId}"
        tvNameToDelete.text = "Name: ${studentIdAndNameData.studentName}"


        val deleteStudentDataDialog = AlertDialog.Builder(requireContext())
        deleteStudentDataDialog.apply {
            setTitle("Delete student data")
            setView(view)
            setPositiveButton("Ok") { _, _ ->
                studentDatabaseHomeFragmentViewModel.deleteStudentDataAt(position)
            }
            setNegativeButton("Cancel") { _, _ ->

            }
        }.create().show()
    }

    private fun displayStudentDataToModifyDialog(position: Int) {
        val studentData = studentDatabaseHomeFragmentViewModel.allStudentData.value!![position]
        val view =
            requireActivity().layoutInflater.inflate(R.layout.item_to_modify_dialog_view, null)
        val idToModify: TextInputEditText = view.findViewById(R.id.studentId)
        val nameToModify: TextInputEditText = view.findViewById(R.id.studentName)
        val genderToModifyAutoText: AutoCompleteTextView = view.findViewById(R.id.studentGender)
        val classToModifyAutoText: AutoCompleteTextView = view.findViewById(R.id.studentClass)

        idToModify.setText(studentData.studentId)
        nameToModify.setText(studentData.studentName)

        val genderToModifyAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.drop_down_item,
            requireContext().resources.getStringArray(R.array.gender)
        )
        val classToModifyAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.drop_down_item,
            requireContext().resources.getStringArray(R.array.classes)
        )

        genderToModifyAutoText.setText(studentData.studentGender)
        genderToModifyAutoText.setAdapter(genderToModifyAdapter)

        classToModifyAutoText.setText(studentData.studentClass)
        classToModifyAutoText.setAdapter(classToModifyAdapter)

        val modifyStudentDataDialog = AlertDialog.Builder(requireContext())
        modifyStudentDataDialog.apply {
            setTitle("Modify student data")
            setView(view)
            setPositiveButton("Ok") { _, _ ->
                val studentDataToUpdate = StudentData(
                    studentData.id,
                    idToModify.text.toString(),
                    nameToModify.text.toString(),
                    genderToModifyAutoText.text.toString(),
                    classToModifyAutoText.text.toString()
                )
                if(studentData != studentDataToUpdate){
                    studentDatabaseHomeFragmentViewModel.updateStudentData(studentDataToUpdate)
                }

            }
            setNegativeButton("Cancel") { _, _ ->

            }
        }.create().show()
    }

}

interface OnRequestToNavigateToStudentDataBaseEditorListener {
    fun navigateToStudentDataBaseEditor(studentName: String)
}
