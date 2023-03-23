package com.example.sequencescorerecorder.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sequencescorerecorder.R

class SequenceScoresManagerHomeFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sequence_scores_manager_home, container, false)
    }

    private fun initViews(){

    }

    private fun setUpViewAdapters(){

    }

    private fun setUpViewListeners(){

    }

    private fun setUpViewObservers(){

    }

    companion object {

        fun newInstance() = SequenceScoresManagerHomeFragment()
    }
}