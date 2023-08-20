package com.turbotechnologies.quiz.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.google.android.material.snackbar.Snackbar
import com.turbotechnologies.quiz.databinding.ActivitySignupBinding

class SignupActivity : InActivity() {
    private lateinit var signupBinding: ActivitySignupBinding

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        val view = signupBinding.root
        setContentView(view)
        signupBinding.buttonSignUp.setOnClickListener {
            val email = signupBinding.editTextSignUpEmail.text.toString()
            val password = signupBinding.editTextSignUpPassword.text.toString()
            if (email.isNotEmpty()) {
                if (password.isNotEmpty()) {
                    signUpWithFireBase(email, password)
                } else {
                    Snackbar.make(
                        signupBinding.signUpLayout,
                        "Password is required while performing a signup.",
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }else {
                Snackbar.make(
                    signupBinding.signUpLayout,
                    "Username is required while performing a signup.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun signUpWithFireBase(email: String, password: String) {
        signupBinding.progressBarSignUp.visibility = View.VISIBLE
        signupBinding.buttonSignUp.isClickable = false
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val intent = Intent(this@SignupActivity, LoginActivity::class.java)
                intent.putExtra("userJustSignedIn", 1)
                startActivity(intent)
                finish()
                Toast.makeText(
                    applicationContext,
                    "Account created successfully!. please login again",
                    Toast.LENGTH_LONG
                ).show()
                signupBinding.progressBarSignUp.visibility = View.INVISIBLE
                signupBinding.buttonSignUp.isClickable = true
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

