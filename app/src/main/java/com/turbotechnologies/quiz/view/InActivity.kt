package com.turbotechnologies.quiz.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.turbotechnologies.quiz.R
import com.turbotechnologies.quiz.viewModel.QuizViewModel


open class InActivity : AppCompatActivity() {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    lateinit var times: QuizViewModel
    private var logUserTime: DatabaseReference = database.reference.child("usersLogEntry")
    private lateinit var sharedPreferences: SharedPreferences
    private var interactedAt: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in)
        times = ViewModelProvider(this)[QuizViewModel::class.java]
        sendInteractedTime(interactedAt)

    }

    open fun loginTime(LogTime: String) {
        logUserTime.child(auth.currentUser?.uid.toString()).child("loggedInTime")
            .setValue(LogTime).addOnCompleteListener {
                Log.d("loggedInTime", LogTime)
            }
    }


    private fun sendInteractedTime(interactedTime: Long) {
        if (auth.currentUser != null) {
            sharedPreferences = this.getSharedPreferences(
                "interactionTime",
                Context.MODE_PRIVATE
            )
            val editor = sharedPreferences.edit()
            editor.putLong("interaction", interactedTime)
            editor.apply()
            Log.d("SendingData", interactedTime.toString())
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onUserInteraction() {
        val time = System.currentTimeMillis()
        sendInteractedTime(time)
        super.onUserInteraction()
    }

    fun validEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun hideKeyboard(view: View) {
        // view.windowToken -> The token of the window or view that currently has focus.
        // Passing 0 or InputMethodManager.HIDE_NOT_ALWAYS indicates that the keyboard should be hidden if it is currently visible
        val inputMethodManager =
            getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(
            view.windowToken,
            InputMethodManager.HIDE_NOT_ALWAYS
        )
    }

}
