package com.turbotechnologies.quiz.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.turbotechnologies.quiz.R
import com.turbotechnologies.quiz.R.*

class QnTimerFragment : DialogFragment(), AdapterView.OnItemSelectedListener {
    private lateinit var textQnTimer: TextView
    private lateinit var dropDown: Spinner
    private var timerValue: Long = 0L
    private lateinit var savedTimeValue: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(layout.fragment_qn_timer, container, false)

        textQnTimer = view.findViewById(R.id.textView4)
        dropDown = view.findViewById(R.id.dropdown)
        savedTimeValue = view.findViewById(R.id.saveTheTime)

        dialog!!.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        dropDown.onItemSelectedListener = this

        savedTimeValue.setOnClickListener {
            Log.d("savedTime","Save button has been clicked.")
            dialog!!.dismiss()
            Toast.makeText(requireContext(),"Successfully updated the question timer, please start the quiz!", Toast.LENGTH_LONG).show()
        }

        val arrayAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            array.timer_values,
            android.R.layout.simple_spinner_item
        )
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        dropDown.adapter = arrayAdapter
        return view
    }



    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        Log.d("ItemSelected", "Item has been Selected.")
        if (parent != null) {
            Log.d("onItemSelected", parent.getItemAtPosition(position).toString())
            timerValue = parent.getItemAtPosition(position).toString().toLong()
            val mainActivity: MainActivity = activity as MainActivity
            mainActivity.timerData(timerValue)
        } else {
            Log.d("Fail", "Fail")
        }
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.d("onNothingSelected", "Nothing selected")
    }

}