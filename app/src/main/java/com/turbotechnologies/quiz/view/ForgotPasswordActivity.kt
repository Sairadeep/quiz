package com.turbotechnologies.quiz.view

import android.os.Bundle
import android.widget.Toast
import com.turbotechnologies.quiz.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : InActivity() {
    private lateinit var forgotPasswordBinding: ActivityForgotPasswordBinding
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
}