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
import com.example.sequencescorerecorder.adapters.StudentDbHomeRecyclerAdapter
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.database.StudentDatabase
import com.example.sequencescorerecorder.viewModels.StudentDatabaseHomeFragmentViewModel
import com.google.android.material.textfield.TextInputEditText

class StudentDatabaseHomeFragment : Fragment(),
    StudentDbHomeRecyclerAdapter.OnHomeRecyclerItemsClickListener {

    private lateinit var studentDatabaseHomeFragmentViewModel: StudentDatabaseHomeFragmentViewModel
    private lateinit var btnNewStudent: Button
    private lateinit var rvStudentDbHomeRecycler: RecyclerView
    private lateinit var rvLayout: LinearLayout
    private lateinit var tvNoDataAvailable: TextView
    private lateinit var tvTotalNumberOfStudents: TextView
    private lateinit var btnClearDatabase: Button

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
        btnClearDatabase = view.findViewById(R.id.btnClearDatabase)
        rvLayout = view.findViewById(R.id.rvLayout)

    }

    private fun setupViewListeners() {

        btnClearDatabase.setOnClickListener {
            displayMessageToClearDatabase()
        }

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
                setPositiveButton(requireContext().resources.getString(R.string.ok)) { _, _ ->
                    if (studentClass.text.isNotEmpty()) {
                        onRequestToNavigateToStudentDataBaseEditorListener.navigateToStudentDataBaseEditor(
                            studentClass.text.toString()
                        )
                    }
                }
                setNegativeButton(requireContext().resources.getString(R.string.cancel)) { _, _ ->

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
                    rvLayout.visibility = View.VISIBLE
                    tvNoDataAvailable.visibility = View.GONE
                    btnClearDatabase.isEnabled = true
                    val adapter = StudentDbHomeRecyclerAdapter(
                        requireContext(),
                        allStudentsIdsAndNamesData,
                        this
                    )
                    rvStudentDbHomeRecycler.adapter = adapter
                } else {
                    rvLayout.visibility = View.GONE
                    tvNoDataAvailable.visibility = View.VISIBLE
                    btnClearDatabase.isEnabled = false
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
                            requireContext().resources.getString(R.string.data_deleted_successfully),
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
                            requireContext().resources.getString(R.string.data_updated_successfully),
                            Toast.LENGTH_LONG
                        ).show()
                        studentDatabaseHomeFragmentViewModel.refreshDatabase()
                    }
                }
            })
    }


    override fun onResume() {
        super.onResume()
        requireActivity().title = requireContext().resources.getString(R.string.student_database_manager)
        studentDatabaseHomeFragmentViewModel.refreshDatabase()
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

    override fun onItemClicked(position: Int) {
        displayStudentDetailsDialog(position)
    }

    private fun displayStudentDetailsDialog(position: Int){
        val studentData = studentDatabaseHomeFragmentViewModel.allStudentData.value!![position]
        val view = requireActivity().layoutInflater.inflate(R.layout.student_details, null)

        val studentId: TextView = view.findViewById(R.id.tvIdDetails)
        val studentName: TextView = view.findViewById(R.id.tvNameDetails)
        val studentGender: TextView = view.findViewById(R.id.tvGenderDetails)
        val studentClass: TextView = view.findViewById(R.id.tvClassDetails)

        studentId.text = "${requireContext().resources.getString(R.string.id)} ${studentData.studentId}"
        studentName.text = "${requireContext().resources.getString(R.string.name)} ${studentData.studentName}"
        studentName.visibility = View.GONE
        studentGender.text = "${requireContext().resources.getString(R.string.gender)} ${studentData.studentGender}"
        studentClass.text = "${requireContext().resources.getString(R.string._class)} ${studentData.studentClass}"

        val studentDetailsDialog = AlertDialog.Builder(requireContext())
        studentDetailsDialog.apply {
            setView(view)
            setTitle(studentData.studentName?.uppercase())
            setPositiveButton(requireContext().resources.getString(R.string.ok)){_, _ -> }
        }.create().show()
    }

    private fun displayDeleteStudentDataDialog(position: Int) {
        val view =
            requireActivity().layoutInflater.inflate(R.layout.item_to_delete_dialog_view, null)
        val tvIdToDelete: TextView = view.findViewById(R.id.tvIdToDelete)
        val tvNameToDelete: TextView = view.findViewById(R.id.tvNameToDelete)

        val studentIdAndNameData =
            studentDatabaseHomeFragmentViewModel.allStudentIdsAndNamesData.value!![position]
        tvIdToDelete.text = "${requireContext().resources.getString(R.string.id)} ${studentIdAndNameData.studentId}"
        tvNameToDelete.text = "${requireContext().resources.getString(R.string.name)} ${studentIdAndNameData.studentName}"


        val deleteStudentDataDialog = AlertDialog.Builder(requireContext())
        deleteStudentDataDialog.apply {
            setView(view)
            setPositiveButton(requireContext().resources.getString(R.string.delete)) { _, _ ->
                studentDatabaseHomeFragmentViewModel.deleteStudentDataAt(position)
            }
            setNegativeButton(requireContext().resources.getString(R.string.cancel)) { _, _ ->

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
            setTitle(requireContext().resources.getString(R.string.modify_data))
            setView(view)
            setPositiveButton(requireContext().resources.getString(R.string.ok)) { _, _ ->
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
            setNegativeButton(requireContext().resources.getString(R.string.cancel)) { _, _ ->

            }
        }.create().show()
    }

    private fun displayMessageToClearDatabase(){
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setTitle(requireContext().resources.getString(R.string.clear_database))
            setMessage(requireContext().resources.getString(R.string.message_to_clear_database))
            setPositiveButton(requireContext().resources.getString(R.string._continue)){_, _ ->
                studentDatabaseHomeFragmentViewModel.clearDatabase()
            }
            setNegativeButton(requireContext().resources.getString(R.string.cancel)){_, _ -> }
        }.create().show()
    }

}

interface OnRequestToNavigateToStudentDataBaseEditorListener {
    fun navigateToStudentDataBaseEditor(name: String)
}
