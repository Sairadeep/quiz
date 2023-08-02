package com.turbotechnologies.quiz.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.turbotechnologies.quiz.R

open class InActivity : AppCompatActivity() {
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val database : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val logUserTime : DatabaseReference = database.reference.child("usersLogEntry")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in)
    }

    open fun loginTime(LogTime : String){
        logUserTime.child(auth.currentUser?.uid.toString()).child("LoggedInTime")
            .setValue(LogTime).addOnCompleteListener {
                Log.d("loggedInTime", LogTime)
            }
    }

    open fun userInteractedTime(userInteraction : String){
        logUserTime.child(auth.currentUser?.uid.toString()).child("lastInteractedAt")
            .setValue(userInteraction).addOnCompleteListener {
                Log.d("lastInteractedAt", userInteraction)
            }
    }
}