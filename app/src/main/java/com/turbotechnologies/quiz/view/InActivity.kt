package com.turbotechnologies.quiz.view


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.turbotechnologies.quiz.R
import com.turbotechnologies.quiz.services.DataSyncService
import com.turbotechnologies.quiz.services.LogOutService
import com.turbotechnologies.quiz.viewModel.QuizViewModel


open class InActivity : AppCompatActivity() {
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    lateinit var times: QuizViewModel
    private var logUserTime: DatabaseReference = database.reference.child("usersLogEntry")
    private lateinit var sharedPreferences: SharedPreferences
    private var interactedAt: Int = 0
//    private var timeAtInteraction: String? = null
//    private var interactionTime: Int = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_in)
        times = ViewModelProvider(this)[QuizViewModel::class.java]
        sendInteractedTime(interactedAt)

        if (auth.currentUser != null) {
            startService(Intent(this@InActivity, LogOutService::class.java))
            startService(Intent(this, DataSyncService::class.java))
        }

    }

    open fun loginTime(LogTime: String) {
        logUserTime.child(auth.currentUser?.uid.toString()).child("loggedInTime")
            .setValue(LogTime).addOnCompleteListener {
                Log.d("loggedInTime", LogTime)
            }
    }

    fun sendInteractedTime(interactedTime: Int) {
        sharedPreferences = this.getSharedPreferences(
            "interactionTime",
            Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()
        editor.putInt("interaction", interactedTime)
        editor.apply()
        Log.d("SendingData", interactedTime.toString())
    }


    fun currentTime(timeValue: String): Int {
        val currentTimValue = timeValue.split(":")
        val currentTimeInHours = currentTimValue[0].toInt()
        val currentTimeInMin = currentTimValue[1].toInt()
        val currentTimeInSec = currentTimValue[2].toInt()
        return ((currentTimeInHours * 3600) + (currentTimeInMin * 60) + (currentTimeInSec)) / 60
    }
}


//    open fun userInteractedTime(userInteraction: String) {
//        logUserTime.child(auth.currentUser?.uid.toString()).child("lastInteractedAt")
//            .setValue(userInteraction).addOnCompleteListener {
//                Log.d("lastInteractedAt", userInteraction)
//            }
//    }

//    @SuppressLint("SimpleDateFormat")
//    private fun interactTime() {
//        val user = auth.currentUser
//        val userid = user?.uid
//        if (user != null) {
//            interactTime.addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    val interactedTime =
//                        snapshot.child(userid.toString()).child("lastInteractedAt").value.toString()
//                    val timeIna = interactedTime.split(":")
//                    val timeInaHour = timeIna[0].toInt()
//                    val timeInaMin = timeIna[1].toInt()
//                    val timeInaSec = timeIna[2].toInt()
//                    interactionTime = ((timeInaHour * 3600) + (timeInaMin * 60) + (timeInaSec)) / 60
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
//                }
//            })
//        }
//
//        val currentIme = System.currentTimeMillis()
//        val currentPattern = SimpleDateFormat("HH:mm:ss").format(currentIme)
//        val currentImeTime = currentPattern.split(":")
//        val currentImeInHour = ((currentImeTime[0].toInt()) * 3600)
//        val currentImeInMin = ((currentImeTime[1].toInt()) * 60)
//        val currentImeInSec = currentImeTime[2].toInt()
//
//        val currentValInMin = (currentImeInHour + currentImeInMin + currentImeInSec) / 60
//
//        Log.d("currrrrrentTime", currentValInMin.toString())
//        Log.d("interactedTiiiiiime", interactionTime.toString())
//    }
//}