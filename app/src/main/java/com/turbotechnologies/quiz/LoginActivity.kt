package com.turbotechnologies.quiz

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
import com.turbotechnologies.quiz.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    lateinit var loginBinding: ActivityLoginBinding
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Creating an object from google sign in client class
    lateinit var googleSignInClient: GoogleSignInClient

    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = ActivityLoginBinding.inflate(layoutInflater)
        val view = loginBinding.root
        setContentView(view)

        // As we cannot change the properties of the google button from the design side, those can be done from here.
        // To access the text view defined inside the button -> getChildAt()
        val textForGoogleSignInButton = loginBinding.buttonGoogleSignIn.getChildAt(0) as TextView
        // Changing the text of the google signIn button
        textForGoogleSignInButton.text = "Sign In with Google"
        textForGoogleSignInButton.setTextColor(Color.BLACK)
        textForGoogleSignInButton.textSize = 16F
        textForGoogleSignInButton.setBackgroundResource(R.drawable.button_shape)


        // register the activity
        registeringActivityForGoogleSignIn()

        loginBinding.progressBar4.visibility = View.INVISIBLE

        loginBinding.buttonSignIn.setOnClickListener {
            // Getting the email and password entered by the user
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

    // Sign In process
    private fun signInUser(userEmail: String, userPassword: String) {
        // signInWithEmailAndPassword() -> It compares the user entered values with the values available in the FireBase and does the further actions.
        auth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT)
                    .show()
                // After logging, opening the main activity
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

    // Method to make user logged in till the user logs out
    override fun onStart() {
        super.onStart()
        val user = auth.currentUser // Gets the current user's information.
        var asSignedIn = intent.getIntExtra("userJustSignedIn", 0)
        if (asSignedIn == 1) {
            //Toast.makeText(applicationContext, asSignedIn.toString(), Toast.LENGTH_SHORT).show()
        } else if (user != null) {
            // User already logged In
            Toast.makeText(applicationContext, "Welcome to Quiz Game", Toast.LENGTH_SHORT)
                .show()
            Log.d("current", "Main Activity")
            // After logging, opening the main activity
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    // Google SignIn process and it is a parameter list function
    private fun signInGoogle() {
        // Creating an object from google signIn options class
        // requestIdToken() -> It specifies an ID token for authenticated users is requested. Requesting an "ID token" requires that the server client ID ("WEB CLIENT ID") can be specified.
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail().build()
        googleSignInClient = GoogleSignIn.getClient(
            // Parameter 1 -> context
            // Parameter 2 -> Object created from google signIn options class
            this,
            gso
        )
        signIn()
    }

    // Function to start the intent Sign In process
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        // We can't start this intent with "startActivity()" as a result we will receive some data from the user in return
        // User will choose email address and We will reach some data with this email address
        activityResultLauncher.launch(signInIntent)

    }

    // Fn registering the activity result launcher
    private fun registeringActivityForGoogleSignIn() {
        activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->
                    // We can get the result of the intent using the keyword 'result'.
                    // Getting the result code and data
                    val resultCode = result.resultCode
                    val data = result.data
                    loginBinding.progressBarGoogleSign.visibility = View.VISIBLE
                    if (resultCode == RESULT_OK && data != null) {
                        // Create a thread using the task class and this thread will get the data from google in background.
                        val task: Task<GoogleSignInAccount> =
                            GoogleSignIn.getSignedInAccountFromIntent(data)
                        // Start the login process using the 'task' object.
                        firebaseSignInWithGoogle(task)
                    }
                })
    }


    private fun firebaseSignInWithGoogle(task: Task<GoogleSignInAccount>) {
        try {
            // Sign In process is done using the google API and if any error is noticed it is handled in the catch block.
            val account: GoogleSignInAccount = task.getResult(ApiException::class.java)
//            if (!task.isSuccessful){
//                Toast.makeText(applicationContext,task.exception?.localizedMessage,Toast.LENGTH_LONG).show()
//            }

            Toast.makeText(
                applicationContext,
                "Successfully logged in with Gmail account",
                Toast.LENGTH_SHORT
            ).show()
            // Opening the main activity
            val intent = Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            // Closing the login page as we opened main activity using the intent
            finish()
            firebaseGoogleAccount(account)
        } catch (e: ApiException) {
            Toast.makeText(applicationContext, e.localizedMessage, Toast.LENGTH_SHORT).show()
        }
    }

    private fun firebaseGoogleAccount(account: GoogleSignInAccount) {
        // creating an object from the auth credential class
        val authCredential = GoogleAuthProvider.getCredential(
            // Parameter 1 -> account.idToken -> It is unique for each device and can know which device is logged in.
            account.idToken, null
        )
        auth.signInWithCredential(authCredential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Retrieve the user data, if login is done
                //Toast.makeText(applicationContext, "Logged In", Toast.LENGTH_SHORT).show()
                // Creating an user object from the firebase user class
                //val user =
                //  auth.currentUser // Hence, logged in user is assigned to the user object i.e., When the user logs in, it gives us the opportunity to access this data in there google account.
                // user?.email
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