package com.turbotechnologies.quiz

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.turbotechnologies.quiz.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {
    lateinit var signupBinding: ActivitySignupBinding
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        signupBinding = ActivitySignupBinding.inflate(layoutInflater)
        val view = signupBinding.root
        setContentView(view)
        signupBinding.buttonSignUp.setOnClickListener {
            val email = signupBinding.editTextSignUpEmail.text.toString()
            val password = signupBinding.editTextSignUpPassword.text.toString()
            signUpWithFireBase(email, password)
            // Take the internet connection from the user as sending data to FireBase requires Internet.
        }
    }

    // Membership function
    private fun signUpWithFireBase(email: String, password: String) {
        //  Create a auth object from the Firebase auth class and using this auth object, we can do sign up.
        signupBinding.progressBarSignUp.visibility = View.VISIBLE
        // Sign Up button should be allowed to click only once otherwise membership will be created more than once.
        signupBinding.buttonSignUp.isClickable = false
        // Sign up process
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if(task.isSuccessful){
                val intent  = Intent(this@SignupActivity,LoginActivity::class.java)
                intent.putExtra("userJustSignedIn",1)
                startActivity(intent)
                finish()
                Toast.makeText(applicationContext,"Account created successfully!. please login again", Toast.LENGTH_LONG).show()
                signupBinding.progressBarSignUp.visibility = View.INVISIBLE
                signupBinding.buttonSignUp.isClickable = true
            }else{
                Toast.makeText(applicationContext,task.exception?.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    }

