package com.turbotechnologies.quiz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.turbotechnologies.quiz.databinding.ActivityResultBinding

class ResultActivity : AppCompatActivity() {
    lateinit var resultBinding: ActivityResultBinding

    // Create an object from the database class to access it and retrieve data from it.
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference = database.reference.child("scores")

    // Create a object from the firebase auth class to know the person logged in
    private val auth = FirebaseAuth.getInstance()

    // Create a user object
    var user = auth.currentUser

    var userCorrect = ""
    var userWrong = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultBinding = ActivityResultBinding.inflate(layoutInflater)
        val view = resultBinding.root
        setContentView(view)

        // Retrieving the data from the database
        dataRetrieve()

        resultBinding.buttonPlayAgain.setOnClickListener {
            val intent = Intent(this@ResultActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        resultBinding.buttonExit.setOnClickListener {
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
                .signOut() // Now the user will exit from FireBase and also from the app

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
            val intent = Intent(this@ResultActivity, LoginActivity::class.java)
            startActivity(intent)
            finish() // To close the current activity i.e., the main activity.
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dataRetrieve() {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                user?.let {
                    // 'it' represents the non null user object.
                    val userUID = it.uid
                    // Data is retrieved from the 'snapshot' object created from the "DataSnapshot" class
                    // As the 'databaseReference' object reaches the parent 'scores' use snapshot to reach its child.
                    userCorrect = snapshot.child(userUID).child("correctAnswers").value.toString()
                    userWrong = snapshot.child(userUID).child("wrongAnswers").value.toString()

                    // Setting the values retrieved to the text views.
                    resultBinding.textViewCorrectScore.text = userCorrect
                    resultBinding.textViewWrongScore.text = userWrong
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}