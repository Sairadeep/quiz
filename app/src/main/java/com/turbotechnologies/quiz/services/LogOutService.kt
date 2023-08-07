package com.turbotechnologies.quiz.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.turbotechnologies.quiz.view.InActivity
import com.turbotechnologies.quiz.view.LoginActivity
import java.text.SimpleDateFormat


class LogOutService : Service() {

    private lateinit var timer: CountDownTimer
    private val logoutService: InActivity = InActivity()

    private lateinit var sharedPreferences: SharedPreferences

    var interacted: Int = 0

    @SuppressLint("SimpleDateFormat")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        sharedPreferences = this.getSharedPreferences("interactionTime", Context.MODE_PRIVATE)
        Toast.makeText(applicationContext, "Service Started", Toast.LENGTH_SHORT).show()

        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(p0: Long) {
                interacted = sharedPreferences.getInt("interaction", 1)
                Log.d("interactedService", interacted.toString())

                val currentTime =  System.currentTimeMillis()
                val formattedTime = SimpleDateFormat("HH:mm:ss").format(currentTime)
                val time = logoutService.currentTime(formattedTime)
                Log.d("xyz",time.toString())

                val timeDiff = time - interacted

                if(timeDiff == 0){
                    val auth = FirebaseAuth.getInstance()
                auth.signOut()
                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                        .build()
                val googleSignInClient = GoogleSignIn.getClient(this@LogOutService, gso)

                googleSignInClient.signOut().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val intents = Intent(this@LogOutService, LoginActivity::class.java)
                        intents.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intents)
                    } else {
                        Toast.makeText(
                            applicationContext,
                            task.exception?.localizedMessage,
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
                }
            }


            override fun onFinish() {
                timer.start()
            }
        }.start()

        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }

    override fun onDestroy() {
        stopSelf()
        Log.d("OnDestroyService", "Service Destroyed!")
        super.onDestroy()
    }
}