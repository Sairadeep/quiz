package com.turbotechnologies.quiz.view

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import com.turbotechnologies.quiz.databinding.ActivityForgotPasswordBinding
import java.text.SimpleDateFormat

class ForgotPasswordActivity : InActivity() {
    private lateinit var forgotPasswordBinding: ActivityForgotPasswordBinding
    private var time: Long = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = forgotPasswordBinding.root
        setContentView(view)
        forgotPasswordBinding.buttonResetPwd.setOnClickListener {
            val userEmail = forgotPasswordBinding.editTextforgotpwdEmail.text.toString()
            forgotPassword(userEmail)
        }
    }

    private fun forgotPassword(userEmail: String) {
        auth.sendPasswordResetEmail(userEmail).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    applicationContext,
                    "Reset Link has been sent to respected email",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            } else {
                Toast.makeText(
                    applicationContext,
                    task.exception?.localizedMessage,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    override fun onUserInteraction() {
        time = System.currentTimeMillis()
        val interactedTime : String = SimpleDateFormat("HH:mm:ss").format(time).toString()
        val interactedAtTime = currentTime(interactedTime)
        sendInteractedTime(interactedAtTime)
        super.onUserInteraction()
    }

}