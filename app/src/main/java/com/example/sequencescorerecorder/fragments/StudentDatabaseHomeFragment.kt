package com.example.sequencescorerecorder.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.adapters.StudentDbHomeRecyclerAdapter
import com.example.sequencescorerecorder.database.StudentDatabase
import com.example.sequencescorerecorder.viewModels.StudentDatabaseHomeFragmentViewModel
import com.google.android.material.textfield.TextInputEditText

class StudentDatabaseHomeFragment : Fragment(),
    StudentDbHomeRecyclerAdapter.OnHomeRecyclerItemsClickListener{

    private lateinit var studentDatabaseHomeFragmentViewModel: StudentDatabaseHomeFragmentViewModel
    private lateinit var btnNewStudent: Button
    private lateinit var rvStudentDbHomeRecycler: RecyclerView
    private lateinit var rvLayout: LinearLayout
    private lateinit var tvNoDataAvailable: TextView
    private lateinit var tvTotalNumberOfStudents: TextView
    private lateinit var btnClearDatabase: Button
    private lateinit var btnRefresh: Button
    private lateinit var autoCompleteSort: AutoCompleteTextView

    private lateinit var onRequestToNavigateToStudentDataBaseEditorListener: OnRequestToNavigateToStudentDataBaseEditorListener
    private lateinit var onSortOrderChangeListener: OnSortOrderChangeListener

    private lateinit var pref: SharedPreferences

    companion object {

        fun newInstance(schoolIndex: Int, academicYearIndex: Int, sortIndex: Int): Fragment {
            val studentDatabaseHomeFragment = StudentDatabaseHomeFragment()

            val bundle = Bundle().apply {
                putInt("schoolIndex", schoolIndex)
                putInt("academicYearIndex", academicYearIndex)
                putInt("sortIndex", sortIndex)
            }
            studentDatabaseHomeFragment.arguments = bundle
            return studentDatabaseHomeFragment
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnRequestToNavigateToStudentDataBaseEditorListener) {
            onRequestToNavigateToStudentDataBaseEditorListener = context
        }
        if (context is OnSortOrderChangeListener){
            onSortOrderChangeListener = context
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
        pref = requireContext().getSharedPreferences("StudentDatabaseManagerActivity",
            AppCompatActivity.MODE_PRIVATE
        )
        initViewModel()
        initViews(view)
        setupRecyclerView()
        setupViewAdapters()
        setupViewListeners()
        setupViewObservers()
    }

    private fun initViewModel() {
        studentDatabaseHomeFragmentViewModel =
            ViewModelProvider(this)[StudentDatabaseHomeFragmentViewModel::class.java]
        studentDatabaseHomeFragmentViewModel.initDatabase(
            StudentDatabase.getStudentDatabase(
                requireContext()
            )
        )
        val schoolName =
            requireContext().resources.getStringArray(R.array.schools)[requireArguments().getInt("schoolIndex")]
        studentDatabaseHomeFragmentViewModel.setSchoolName(schoolName)
        studentDatabaseHomeFragmentViewModel.setAcademicYearIndex(requireArguments().getInt("academicYearIndex"))
        studentDatabaseHomeFragmentViewModel.setSortOptionIndex(requireArguments().getInt("sortIndex"))
        studentDatabaseHomeFragmentViewModel.setSelectedClass(pref.getString("lastSelectedClass", null))

    }

    private fun initViews(view: View) {
        btnNewStudent = view.findViewById(R.id.btnNewStudent)
        rvStudentDbHomeRecycler = view.findViewById(R.id.rvStudentDbHome)
        tvNoDataAvailable = view.findViewById(R.id.tvNoDataAvailable)
        tvTotalNumberOfStudents = view.findViewById(R.id.tvTotalNumberOfStudents)
        btnClearDatabase = view.findViewById(R.id.btnClearDatabase)
        rvLayout = view.findViewById(R.id.rvLayout)
        btnRefresh = view.findViewById(R.id.btnRefresh)
        autoCompleteSort = view.findViewById(R.id.autoCompleteSort)

    }

    private fun setupViewListeners() {

        btnRefresh.setOnClickListener {
            studentDatabaseHomeFragmentViewModel.refreshDatabase()
        }
        btnClearDatabase.setOnClickListener {
            displayMessageToClearDatabase()
        }

        autoCompleteSort.setOnItemClickListener { _, _, i, _ ->
//            println("index: $i")
            studentDatabaseHomeFragmentViewModel.setSortOptionIndex(i)
            studentDatabaseHomeFragmentViewModel.refreshDatabase()
            onSortOrderChangeListener.onSortOrderChanged(i)

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
            studentClass.setOnItemClickListener { _, _, _, _ ->

                studentDatabaseHomeFragmentViewModel.setSelectedClass(studentClass.text.toString())
                val editor = pref.edit()
                editor.apply {
                    putString("lastSelectedClass", studentClass.text.toString())
                    apply()
                }
            }

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

    private fun setupRecyclerView() {

        val layoutMan = LinearLayoutManager(requireContext())
        layoutMan.orientation = LinearLayoutManager.VERTICAL
        rvStudentDbHomeRecycler.layoutManager = layoutMan
        rvStudentDbHomeRecycler.addItemDecoration(
            DividerItemDecoration(
                requireContext(),
                LinearLayoutManager.VERTICAL
            )
        )
//

    }

    private fun setupViewAdapters() {
        val autoSortAdapter = ArrayAdapter<String>(
            requireContext(),
            R.layout.drop_down_item,
            requireContext().resources.getStringArray(R.array.sort_options)
        )

        autoCompleteSort.setText(requireContext().resources.getStringArray(R.array.sort_options)[studentDatabaseHomeFragmentViewModel.sortOptionIndex.value!!])
        autoCompleteSort.setAdapter(autoSortAdapter)

    }

    private fun setupViewObservers() {

        studentDatabaseHomeFragmentViewModel.totalNumberOfStudents.observe(
            viewLifecycleOwner,
            Observer {
                tvTotalNumberOfStudents.text = it.toString()
            })

        studentDatabaseHomeFragmentViewModel.allStudentData.observe(
            viewLifecycleOwner,
            Observer { allStudentsIdsAndNamesData ->
//                println("allStudentsIdsAndNamesData: $allStudentsIdsAndNamesData")
                allStudentsIdsAndNamesData?.let {
                    if (it.isNotEmpty()) {
                        rvLayout.visibility = View.VISIBLE
                        tvNoDataAvailable.visibility = View.GONE
                        btnClearDatabase.isEnabled = true
                        val adapter = StudentDbHomeRecyclerAdapter(
                            requireContext(),
                            it,
                            this
                        )
                        rvStudentDbHomeRecycler.adapter = adapter
                        rvStudentDbHomeRecycler.adapter?.notifyDataSetChanged()
                    } else {
                        rvLayout.visibility = View.GONE
                        tvNoDataAvailable.visibility = View.VISIBLE
                        btnClearDatabase.isEnabled = false
                    }
                }


            })

        studentDatabaseHomeFragmentViewModel.deletedItemPosition.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    Toast.makeText(
                        requireContext(),
                        requireContext().resources.getString(R.string.data_deleted_successfully),
                        Toast.LENGTH_LONG
                    ).show()
                    rvStudentDbHomeRecycler.adapter?.notifyItemRemoved(it)
                }

            })

        studentDatabaseHomeFragmentViewModel.updatedItemPosition.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    Toast.makeText(
                        requireContext(),
                        requireContext().resources.getString(R.string.data_updated_successfully),
                        Toast.LENGTH_LONG
                    ).show()
                    rvStudentDbHomeRecycler.adapter?.notifyItemChanged(it)
                }
            })

        studentDatabaseHomeFragmentViewModel.isClearDatabaseSuccessful.observe(
            viewLifecycleOwner,
            Observer {
                it?.let {
                    Toast.makeText(
                        requireContext(),
                        requireContext().resources.getString(R.string.database_erased_successfully),
                        Toast.LENGTH_LONG
                    ).show()
                    rvStudentDbHomeRecycler.adapter?.notifyDataSetChanged()
                }
            })
    }


    override fun onResume() {
        super.onResume()
        requireActivity().title = requireContext().resources.getString(R.string.students_database)
        studentDatabaseHomeFragmentViewModel.refreshDatabase()
    }

    override fun onDestroy() {
        studentDatabaseHomeFragmentViewModel.updateDatabase()
        super.onDestroy()
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

    private fun displayStudentDetailsDialog(position: Int) {
        val studentData = studentDatabaseHomeFragmentViewModel.allStudentData.value!![position]
        val view = requireActivity().layoutInflater.inflate(R.layout.student_details, null)

        val studentId: TextView = view.findViewById(R.id.tvIdDetails)
        val studentName: TextView = view.findViewById(R.id.tvNameDetails)
        val studentGender: TextView = view.findViewById(R.id.tvGenderDetails)
        val studentClass: TextView = view.findViewById(R.id.tvClassDetails)

        studentId.text =
            "${requireContext().resources.getString(R.string.id)} ${studentData.studentId}"
        studentName.text =
            "${requireContext().resources.getString(R.string.name)} ${studentData.studentName}"
        studentName.visibility = View.GONE
        studentGender.text =
            "${requireContext().resources.getString(R.string.gender)} ${studentData.studentGender}"
        studentClass.text =
            "${requireContext().resources.getString(R.string._class)} ${studentData.currentClass}"

        val studentDetailsDialog = AlertDialog.Builder(requireContext())
        studentDetailsDialog.apply {
            setView(view)
            setTitle(studentData.studentName?.uppercase())
            setPositiveButton(requireContext().resources.getString(R.string.ok)) { _, _ -> }
        }.create().show()
    }

    private fun displayDeleteStudentDataDialog(position: Int) {
        val view =
            requireActivity().layoutInflater.inflate(R.layout.item_to_delete_dialog_view, null)
        val tvIdToDelete: TextView = view.findViewById(R.id.tvIdToDelete)
        val tvNameToDelete: TextView = view.findViewById(R.id.tvNameToDelete)

        val studentId =
            studentDatabaseHomeFragmentViewModel.allStudentData.value!![position].studentId
        val studentName =
            studentDatabaseHomeFragmentViewModel.allStudentData.value!![position].studentName

        tvIdToDelete.text = "${requireContext().resources.getString(R.string.id)} $studentId"
        tvNameToDelete.text = "${requireContext().resources.getString(R.string.name)} $studentName"


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
        val oldStudentData = studentDatabaseHomeFragmentViewModel.allStudentData.value!![position]
//        println("Student data to update ${studentDatabaseHomeFragmentViewModel.allStudentData.value!![position]}")
        val view =
            requireActivity().layoutInflater.inflate(R.layout.item_to_modify_dialog_view, null)
        val idToModify: TextInputEditText = view.findViewById(R.id.studentId)
        val nameToModify: TextInputEditText = view.findViewById(R.id.studentName)
        val genderToModifyAutoText: AutoCompleteTextView = view.findViewById(R.id.studentGender)
        val classToModifyAutoText: AutoCompleteTextView = view.findViewById(R.id.studentClass)

        idToModify.setText(oldStudentData.studentId)
        nameToModify.setText(oldStudentData.studentName)

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

        genderToModifyAutoText.setText(oldStudentData.studentGender)
        genderToModifyAutoText.setAdapter(genderToModifyAdapter)

        classToModifyAutoText.setText(oldStudentData.currentClass)
        classToModifyAutoText.setAdapter(classToModifyAdapter)

        val modifyStudentDataDialog = AlertDialog.Builder(requireContext())
        modifyStudentDataDialog.apply {
            setTitle(requireContext().resources.getString(R.string.modify_data))
            setView(view)
            setPositiveButton(requireContext().resources.getString(R.string.ok)) { _, _ ->

                studentDatabaseHomeFragmentViewModel.updateStudentData(
                    position,
                    idToModify.text.toString(),
                    nameToModify.text.toString(),
                    genderToModifyAutoText.text.toString(),
                    classToModifyAutoText.text.toString()
                )

            }
            setNegativeButton(requireContext().resources.getString(R.string.cancel)) { _, _ ->

            }
        }.create().show()
    }

    private fun displayMessageToClearDatabase() {
        val alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
        alertDialog.apply {
            setTitle(requireContext().resources.getString(R.string.clear_database))
            setMessage(requireContext().resources.getString(R.string.message_to_clear_database))
            setPositiveButton(requireContext().resources.getString(R.string._continue)) { _, _ ->
                studentDatabaseHomeFragmentViewModel.clearDatabase()
            }
            setNegativeButton(requireContext().resources.getString(R.string.cancel)) { _, _ -> }
        }.create().show()
    }

}

interface OnRequestToNavigateToStudentDataBaseEditorListener {
    fun navigateToStudentDataBaseEditor(studentClass: String)
}

interface OnSortOrderChangeListener{
    fun onSortOrderChanged(index: Int)
}
