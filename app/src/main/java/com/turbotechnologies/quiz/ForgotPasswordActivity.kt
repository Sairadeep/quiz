package com.turbotechnologies.quiz

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.turbotechnologies.quiz.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {
    lateinit var forgotPasswordBinding: ActivityForgotPasswordBinding
    val auth : FirebaseAuth = FirebaseAuth.getInstance()
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

    // Reset password
    private fun forgotPassword(userEmail:String){
        // Password reset link is sent to the user email address with the help of the method "sendPasswordResetEmail()"
        auth.sendPasswordResetEmail(userEmail).addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(applicationContext,"Reset Link has been sent to respected email",Toast.LENGTH_SHORT).show()
                finish()
            }else{
                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_SHORT).show()
            }
        }
    }
}