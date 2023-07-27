package com.turbotechnologies.quiz.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.turbotechnologies.quiz.R
import com.turbotechnologies.quiz.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var loginBinding: ActivityLoginBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)
        val textForGoogleSignInButton = loginBinding.buttonGoogleSignIn.getChildAt(0) as TextView
        textForGoogleSignInButton.text = "Sign In with Google"
        textForGoogleSignInButton.setTextColor(Color.BLACK)
        textForGoogleSignInButton.textSize = 16F
        textForGoogleSignInButton.setBackgroundResource(R.drawable.button_shape)
        registeringActivityForGoogleSignIn()

        loginBinding.progressBar4.visibility = View.INVISIBLE

        loginBinding.buttonSignIn.setOnClickListener {
            val userEmail = loginBinding.editTextLoginEmail.text.toString()
            val userPassword = loginBinding.editTextLoginPassword.text.toString()
            loginBinding.progressBar4.visibility = View.VISIBLE
            signInUser(userEmail, userPassword)
        }
        loginBinding.buttonGoogleSignIn.setOnClickListener {
            signInGoogle()

        }
        loginBinding.textViewSignUp.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)
        }
        loginBinding.textViewForgetPassword.setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }


    private fun signInUser(userEmail: String, userPassword: String) {
        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT)
                    .show()
                val intent = Intent(this@LoginActivity, MainActivity::class.java)
                startActivity(intent)
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

    override fun onStart() {
        super.onStart()
        val user = auth.currentUser // Gets the current user's information.
        val asSignedIn = intent.getIntExtra("userJustSignedIn", 0)
        if (asSignedIn == 1) {
        } else if (user != null) {
            Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT)
                .show()
            Log.d("current", "Main Activity")
            // After logging, opening the main activity
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun signInGoogle() {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(
            this,
            gso
        )
        signIn()
    }

    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        activityResultLauncher.launch(signInIntent)

    }

    private fun registeringActivityForGoogleSignIn() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    val resultCode = result.resultCode
                    val data = result.data
                    loginBinding.progressBarGoogleSign.visibility = View.VISIBLE
                    if (resultCode == RESULT_OK && data != null) {
                        val task: Task<GoogleSignInAccount> =
                            GoogleSignIn.getSignedInAccountFromIntent(data)
                        firebaseSignInWithGoogle(task)
                    }
                })
    }


    private fun firebaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
            Toast.makeText(
                applicationContext,
                "Successfully logged in with Gmail account",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
            firebaseGoogleAccount(account)
        } catch (e: ApiException) {
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseGoogleAccount(account: GoogleSignInAccount) {
        val authCredential = GoogleAuthProvider.getCredential(
            account.idToken, null
        )
        auth.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                //Toast.makeText(applicationContext, "Logged In", Toast.LENGTH_SHORT).show()
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