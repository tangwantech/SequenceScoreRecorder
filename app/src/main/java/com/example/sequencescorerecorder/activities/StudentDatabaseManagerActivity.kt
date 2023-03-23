package com.example.sequencescorerecorder.activities

import android.content.SharedPreferences
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.fragments.OnRequestToNavigateToStudentDataBaseEditorListener
import com.example.sequencescorerecorder.fragments.OnRequestToNavigateToStudentDataBaseHomeListener
import com.example.sequencescorerecorder.fragments.StudentDatabaseEditorFragment
import com.example.sequencescorerecorder.fragments.StudentDatabaseHomeFragment
import com.example.sequencescorerecorder.viewModels.StudentDatabaseActivityViewModel

// Container for and manages navigation to
// StudentDatabaseHomeFragment
// StudentDatabaseEditorFragment

class StudentDatabaseManagerActivity : AppCompatActivity(),
    OnRequestToNavigateToStudentDataBaseEditorListener,
    OnRequestToNavigateToStudentDataBaseHomeListener{
    private lateinit var pref: SharedPreferences
    private lateinit var studentDatabaseActivityViewModel: StudentDatabaseActivityViewModel
    private var currentFragmentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_database_manager)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        initViewModel()
        gotoHomeFragment()

    }

    private fun initViewModel() {
        studentDatabaseActivityViewModel =
            ViewModelProvider(this)[StudentDatabaseActivityViewModel::class.java]
        studentDatabaseActivityViewModel.setSchoolIndex(intent.getIntExtra("schoolIndex", 0))
        studentDatabaseActivityViewModel.setAcademicYearIndex(
            intent.getIntExtra(
                "academicYearIndex",
                0
            )
        )
    }


    private fun gotoHomeFragment() {
        val studentDatabaseHomeFragment = StudentDatabaseHomeFragment.newInstance(
            studentDatabaseActivityViewModel.getSchoolIndex(),
            studentDatabaseActivityViewModel.getAcademicYearIndex()
        )
        replaceFragment(studentDatabaseHomeFragment, 0)
    }

    private fun replaceFragment(fragment: Fragment, fragmentIndex: Int) {
        currentFragmentIndex = fragmentIndex
        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            replace(R.id.studentDbFragmentHolder, fragment)
            commit()
        }
    }

    private fun gotoStudentDatabaseEditorFragment(studentClass: String) {
        val studentDatabaseEditorFragment = StudentDatabaseEditorFragment.newInstance(
            studentDatabaseActivityViewModel.getSchoolIndex(),
            studentDatabaseActivityViewModel.getAcademicYearIndex(),
            studentClass)
        replaceFragment(studentDatabaseEditorFragment, 1)
    }


    override fun onResume() {
        super.onResume()
        title = resources.getString(R.string.students_database)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (currentFragmentIndex == 0) {
            this.finish()
        } else {
            gotoHomeFragment()
        }
    }

    override fun navigateToStudentDataBaseEditor(studentClass: String) {
        gotoStudentDatabaseEditorFragment(studentClass)
    }

    override fun onRequestToNavigateToStudentDataBaseHome() {
        gotoHomeFragment()
    }

//    override fun onRequestSchoolAndAcademicYearBundle(): LiveData<Bundle> {
//        return studentDatabaseActivityViewModel.schoolAndAcademicYearBundle
//    }

}