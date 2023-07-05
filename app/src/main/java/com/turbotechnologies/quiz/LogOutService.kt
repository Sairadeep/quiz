package com.turbotechnologies.quiz

import android.app.Service
import android.content.Intent
import android.os.CountDownTimer
import android.os.IBinder
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import kotlin.system.exitProcess


class LogOutService : Service() {
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Creating timer
        val timer = object : CountDownTimer(60000, 1000) {
            override fun onTick(p0: Long) {
                val toast = Toast.makeText(
                    applicationContext,
                    "Time remaining: ${p0 / 1000}",
                    Toast.LENGTH_SHORT
                )
                toast.show()
            }

            override fun onFinish() {
                val auth = FirebaseAuth.getInstance()
                auth.signOut()

                val gso =
                    GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                        .build()
                val googleSignInClient = GoogleSignIn.getClient(this@LogOutService, gso)

                googleSignInClient.signOut().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(
                            applicationContext,
                            "Successfully Signed Out",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this@LogOutService, LoginActivity::class.java)
                        startActivity(intent)
                        stopSelf()
                        //exitProcess(0)
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
        timer.start()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        TODO("Not yet implemented")
    }
}