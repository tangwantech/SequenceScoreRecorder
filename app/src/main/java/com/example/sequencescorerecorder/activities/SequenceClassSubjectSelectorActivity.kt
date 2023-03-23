package com.example.sequencescorerecorder.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sequencescorerecorder.R
import com.example.sequencescorerecorder.viewModels.SequenceClassSubjectSelectorViewModel

class SequenceClassSubjectSelectorActivity : AppCompatActivity() {
    private lateinit var viewModel: SequenceClassSubjectSelectorViewModel
    private lateinit var positiveButton: Button
    private lateinit var autoSequence: AutoCompleteTextView
    private lateinit var autoClass: AutoCompleteTextView
    private lateinit var autoSubject: AutoCompleteTextView
//    private lateinit var tvAcademicYear: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sequence_class_subject_selector)
        setActivityTitle()
        initViewModel()
//        initActivityViews()
    }
    private fun setActivityTitle(){
        title = resources.getString(R.string.score_sheets_manager)
    }

    private fun initViewModel(){
        viewModel = ViewModelProvider(this)[SequenceClassSubjectSelectorViewModel::class.java]
    }

//    private fun initActivityViews(){
//        tvAcademicYear = findViewById(R.id.tvAcademicYear)
//        val academicYear = resources.getStringArray(R.array.academic_years)[intent.getIntExtra("academicYearIndex", 0)]
//        tvAcademicYear.text = "Academic year: $academicYear"
//    }
    private fun initDialogViews(view: View){
        autoSequence = view.findViewById(R.id.autoCompleteSelectSequence)
        autoClass = view.findViewById(R.id.autoCompleteSelectClass)
        autoSubject = view.findViewById(R.id.autoCompleteSelectSubject)
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

    private fun setupDialogViewListeners(){
        autoSequence.setOnItemClickListener { _, _, i, _ ->
            viewModel.setSequenceIndex(i)
        }

        autoClass.setOnItemClickListener { _, _, i, _ ->
            viewModel.setClassIndex(i)
        }

        autoSubject.setOnItemClickListener { _, _, i, _ ->
            viewModel.setSubjectIndex(i)
        }
    }

    private fun setupViewObservers(){
        viewModel.allFieldsSelected.observe(this, Observer {
            positiveButton.isEnabled = it
        })
    }

    private fun showSelectSequenceDialog(){
        val view = layoutInflater.inflate(R.layout.sequence_class_subject_selector_dialog, null)
        initDialogViews(view)
        setupViewAdapters()
        setupDialogViewListeners()
        setupViewObservers()

        val alertDialog = AlertDialog.Builder(this).apply {
            setView(view)
            setPositiveButton("OK"){_, _ ->
                gotoScoreEditorActivity()
            }
            setNegativeButton(resources.getString(R.string.exit)){_, _ ->
                exit()
            }
            setCancelable(false)
        }.create()
        alertDialog.show()
        positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        positiveButton.isEnabled = false
    }
    private fun exit(){
        finish()
    }

    override fun onResume() {
        super.onResume()
        showSelectSequenceDialog()

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