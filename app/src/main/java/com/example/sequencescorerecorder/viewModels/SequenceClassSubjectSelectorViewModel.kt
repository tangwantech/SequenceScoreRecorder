package com.example.sequencescorerecorder.viewModels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SequenceClassSubjectSelectorViewModel: ViewModel() {
    private val _allFieldsSelected = MutableLiveData<Boolean>()
    val allFieldsSelected: LiveData<Boolean> = _allFieldsSelected
    private var sequenceIndex: Int? = null
    private var classIndex: Int? = null
    private var subjectIndex: Int? = null

    fun setSequenceIndex(index: Int){
        sequenceIndex = index
        updateAllFieldsSelected()
    }

    fun setClassIndex(index: Int){
        classIndex = index
        updateAllFieldsSelected()
    }

    fun setSubjectIndex(index: Int){
        subjectIndex = index
        updateAllFieldsSelected()
    }

    fun getSequenceIndex(): Int? = sequenceIndex

    fun getClassIndex(): Int? = classIndex

    fun getSubjectIndex(): Int? = subjectIndex

    private fun updateAllFieldsSelected(){
        _allFieldsSelected.value = (sequenceIndex != null && classIndex != null && subjectIndex != null)
    }

}