package com.turbotechnologies.quiz.model

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class Repository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val qnsReference: DatabaseReference = database.reference.child("questions")
    private val scoreReference: DatabaseReference = database.reference.child("scores")
    var correctScore = 0
    var wrongScore = 0
    var availableQuestions = 0

    fun qns(qnDatas: (questions: List<Map<String, String>>) -> Unit) {
        qnsReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val qns = mutableListOf<Map<String, String>>()
                availableQuestions = snapshot.childrenCount.toInt()
                for (qnNo in 1..availableQuestions) {
                    val qnData = mutableMapOf<String, String>()
                    qnData["qn"] = snapshot.child("qn$qnNo").child("qn").value.toString()
                    qnData["optA"] = snapshot.child("qn$qnNo").child("optA").value.toString()
                    qnData["optB"] = snapshot.child("qn$qnNo").child("optB").value.toString()
                    qnData["optC"] = snapshot.child("qn$qnNo").child("optC").value.toString()
                    qnData["optD"] = snapshot.child("qn$qnNo").child("optD").value.toString()
                    qnData["correctAnswer"] =
                        snapshot.child("qn$qnNo").child("answer").value.toString()
                    qns.add(qnData)
                }
                Log.d("qns",qns.toString())
                Log.d("qnsCount",qns.size.toString())
                qnDatas(qns)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }

    fun score(scoreInfo: (Correct: Int, Wrong: Int) -> Unit) {
        val user = auth.currentUser
        val userId = user?.uid
        scoreReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                correctScore =
                    snapshot.child(userId.toString()).child("correctAnswers").value.toString()
                        .toInt()
                wrongScore =
                    snapshot.child(userId.toString()).child("wrongAnswers").value.toString().toInt()
                scoreInfo(correctScore, wrongScore)
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })


    }
}