package com.example.sequencescorerecorder.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.viewModels.SequenceClassSubjectSelectorViewModel

class SequenceClassSubjectSelectorActivity : AppCompatActivity() {
    private lateinit var viewModel: SequenceClassSubjectSelectorViewModel
    private lateinit var autoSequence: AutoCompleteTextView
    private lateinit var autoClass: AutoCompleteTextView
    private lateinit var autoSubject: AutoCompleteTextView
    private lateinit var btnOk: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sequence_class_subject_selector)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setActivityTitle()
        initViewModel()
        initActivityViews()
        setupViewAdapters()
        setupActivityViewListeners()
        setupViewObservers()
    }
    private fun setActivityTitle(){
        title = resources.getString(R.string.score_sheets_manager)
    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(this)[SequenceClassSubjectSelectorViewModel::class.java]
    }

    private fun initActivityViews(){
        autoSequence = findViewById(R.id.autoCompleteSelectSequence)
        autoClass = findViewById(R.id.autoCompleteSelectClass)
        autoSubject = findViewById(R.id.autoCompleteSelectSubject)
        btnOk = findViewById(R.id.btnLoad)
    }

    private fun setupViewAdapters(){
        if(viewModel.getSequenceIndex() != null && viewModel.getClassIndex() != null && viewModel.getSubjectIndex() != null){
            autoSequence.setText(resources.getStringArray(R.array.sequences)[viewModel.getSequenceIndex()!!])
//            autoClass.setText(resources.getStringArray(R.array.classes)[viewModel.getClassIndex()!!])
//            autoSubject.setText(resources.getStringArray(R.array.subjects)[viewModel.getSubjectIndex()!!])
        }

        val autoSequenceAdapter = ArrayAdapter<String>(this, R.layout.drop_down_item, resources.getStringArray(R.array.sequences))
        autoSequence.setAdapter(autoSequenceAdapter)

        val autoClassAdapter = ArrayAdapter<String>(this, R.layout.drop_down_item, resources.getStringArray(R.array.classes))
        autoClass.setAdapter(autoClassAdapter)

        val autoSubjectAdapter = ArrayAdapter<String>(this, R.layout.drop_down_item, resources.getStringArray(R.array.subjects))
        autoSubject.setAdapter(autoSubjectAdapter)

    }

    private fun setupActivityViewListeners(){
        autoSequence.setOnItemClickListener { _, _, i, _ ->
            viewModel.setSequenceIndex(i)
        }

        autoClass.setOnItemClickListener { _, _, i, _ ->
            viewModel.setClassIndex(i)
        }

        autoSubject.setOnItemClickListener { _, _, i, _ ->
            viewModel.setSubjectIndex(i)
        }
        btnOk.setOnClickListener {
            gotoScoreEditorActivity()
        }
    }

    private fun setupViewObservers(){
        viewModel.allFieldsSelected.observe(this, Observer {
            btnOk.isEnabled = it
        })
    }

    override fun onResume() {
        super.onResume()

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

    private fun gotoScoreEditorActivity(){
        val scoreEditorIntent = Intent(this, ScoreEditorActivity::class.java)
        scoreEditorIntent.apply {
            putExtra("schoolIndex", intent.getIntExtra("schoolIndex", 0))
            putExtra("academicYearIndex", intent.getIntExtra("academicYearIndex", 0))
            putExtra("sequenceIndex", viewModel.getSequenceIndex())
            putExtra("classIndex", viewModel.getClassIndex())
            putExtra("subjectIndex", viewModel.getSubjectIndex())
        }
        startActivity(scoreEditorIntent)
    }

}