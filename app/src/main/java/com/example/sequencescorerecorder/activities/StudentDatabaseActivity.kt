package com.example.sequencescorerecorder.activities

import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.adapters.StudentDatabaseRecyclerAdapter
import com.example.sequencescorerecorder.dataModels.StudentData
import com.example.sequencescorerecorder.fragments.OnRequestToNavigateToStudentDataBaseEditorListener
import com.example.sequencescorerecorder.fragments.OnRequestToNavigateToStudentDataBaseHomeListener
import com.example.sequencescorerecorder.fragments.StudentDatabaseEditorFragment
import com.example.sequencescorerecorder.fragments.StudentDatabaseHomeFragment
import com.example.sequencescorerecorder.viewModels.StudentDatabaseActivityViewModel
import com.google.android.material.textfield.TextInputEditText

class StudentDatabaseActivity : AppCompatActivity(), OnRequestToNavigateToStudentDataBaseEditorListener,
    OnRequestToNavigateToStudentDataBaseHomeListener {

    private lateinit var studentDatabaseActivityViewModel: StudentDatabaseActivityViewModel
    private var currentFragmentIndex = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sequence_database)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        studentDatabaseActivityViewModel =
            ViewModelProvider(this)[StudentDatabaseActivityViewModel::class.java]
        gotoHome()

    }

    private fun gotoHome(){
        val studentDatabaseHomeFragment = StudentDatabaseHomeFragment.newInstance()
        replaceFragment(studentDatabaseHomeFragment, 0)
    }

    private fun replaceFragment(fragment: Fragment, fragmentIndex: Int){
        currentFragmentIndex = fragmentIndex
        val transaction = supportFragmentManager.beginTransaction()
        transaction.apply {
            replace(R.id.studentDbFragmentHolder, fragment)
            commit()
        }
    }

    private fun gotoStudentDatabaseEditorFragment(studentClass: String){
        val studentDatabaseEditorFragment = StudentDatabaseEditorFragment.newInstance(studentClass)
        replaceFragment(studentDatabaseEditorFragment, 1)
    }


    override fun onResume() {
        super.onResume()
        title = intent.getStringExtra("title")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home -> {
                onBackPressed()
            }

        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if(currentFragmentIndex == 0){
            this.finish()
        }else{
            gotoHome()
        }
    }

    override fun navigateToStudentDataBaseEditor(studentClass: String) {
        gotoStudentDatabaseEditorFragment(studentClass)
    }

    override fun onRequestToNavigateToStudentDataBaseHome() {
        gotoHome()
    }

}