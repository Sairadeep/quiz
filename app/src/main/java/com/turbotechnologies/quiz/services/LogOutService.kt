package com.turbotechnologies.quiz.services

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.CountDownTimer
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.turbotechnologies.quiz.view.LoginActivity


class LogOutService : Service() {

    private lateinit var timer: CountDownTimer
    private lateinit var sharedPreferences: SharedPreferences

    @SuppressLint("SimpleDateFormat")
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        sharedPreferences = this.getSharedPreferences("interactionTime", Context.MODE_PRIVATE)
        Toast.makeText(applicationContext, "Service Started", Toast.LENGTH_SHORT).show()

        timer = object : CountDownTimer(600000, 10000) {
            override fun onTick(p0: Long) {
                var interacted = sharedPreferences.getLong("interaction", 0L)
                Log.d("interactedService", interacted.toString())

                val currentTime = System.currentTimeMillis()
//                val formattedTime = SimpleDateFormat("HH:mm:ss").format(currentTime)
//                val time = logoutService.currentTime(formattedTime)
                Log.d("xyz", currentTime.toString())

                if (interacted == 0L) {
                    interacted = currentTime
                    Log.d("IfZero", interacted.toString())
                }
                val timeDiff = (currentTime - interacted) / (60000)
                Log.d("timeDiff", timeDiff.toString())

                if (interacted != 0L) {
                    if (timeDiff > 2) {
                        Toast.makeText(
                            applicationContext,
                            "You are about to logout, please perform any action on the device.",
                            Toast.LENGTH_SHORT
                        ).show()
                        val handler = Handler(Looper.getMainLooper())
                        handler.postDelayed({ LogOutService::class.java }, 5000)
                        val auth = FirebaseAuth.getInstance()
                        auth.signOut()
                        val gso =
                            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                                .requestEmail()
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
                        timer.cancel()
                    } else {
                        Log.d("yetToDo", "Yet To Perform a logout")
                    }
                    stopSelf()
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