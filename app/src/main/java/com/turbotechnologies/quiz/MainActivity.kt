package com.turbotechnologies.quiz

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.telephony.ServiceState
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.turbotechnologies.quiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding
    val auth:FirebaseAuth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)

        startService(Intent(this@MainActivity,LogOutService::class.java))

        mainBinding.buttonStartQuiz.setOnClickListener {
            // Clicking on start quiz button must open the page with questions
            val intent = Intent(this@MainActivity, QuizActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(
            R.menu.menu_signout,
            menu
        )
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.item_logout) {
            // Log out the user from the app who has logged in with "Email" and "Password"
            FirebaseAuth.getInstance()
                .signOut()// Now the user will exit from FireBase and also from the app
            // Log out process for the users who have logged in with there google account
            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                    .build()
            // Create an object from the google Signing client class using this 'gso' object
            val googleSignInClient = GoogleSignIn.getClient(
                this,
                gso
            )
            // Now using the 'googleSignInClient' object, we can sign out from the account
            googleSignInClient.signOut().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        applicationContext,
                        "Successfully logged out",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        applicationContext,
                        task.exception?.localizedMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            // Navigating the user to the login page after sign out
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish() // To close the current activity i.e., the main activity.
        }
        return super.onOptionsItemSelected(item)
    }


}