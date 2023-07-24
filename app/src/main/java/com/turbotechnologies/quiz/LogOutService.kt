package com.turbotechnologies.quiz

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth


class LogOutService : Service() {

    private lateinit var timer: CountDownTimer

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(p0: Long) {
                val x = p0 / 1000
                if (x <= 30) {
                    val toast = Toast.makeText(
                        applicationContext,
                        "You are about to logout in:  $x",
                        Toast.LENGTH_SHORT
                    )
                    toast.show()
                }
            }

            override fun onFinish() {
                timer.cancel()
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