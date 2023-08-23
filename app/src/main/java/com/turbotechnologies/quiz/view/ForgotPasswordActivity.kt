package com.turbotechnologies.quiz.view

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.turbotechnologies.quiz.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : InActivity() {
    private lateinit var forgotPasswordBinding: ActivityForgotPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forgotPasswordBinding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        val view = forgotPasswordBinding.root
        setContentView(view)
        forgotPasswordBinding.forgotPwdLayout.setOnClickListener {
            hideKeyboard(it)
        }
        forgotPasswordBinding.buttonResetPwd.setOnClickListener {
            val userEmail = forgotPasswordBinding.editTextforgotpwdEmail.text.toString()
            if (userEmail.isNotEmpty()) {
                if (!validEmail(userEmail)) {
                    Toast.makeText(this, "Please enter a valid email format", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.d("ValidEmail", "Valid Email entered")
                    forgotPassword(userEmail)
                }
            } else {
                Snackbar.make(
                    forgotPasswordBinding.forgotPwdLayout,
                    "User Email is mandatory for resetting a password.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
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