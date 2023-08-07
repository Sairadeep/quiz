package com.turbotechnologies.quiz.view

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.turbotechnologies.quiz.R
import com.turbotechnologies.quiz.databinding.ActivityResultBinding
import com.turbotechnologies.quiz.viewModel.QuizViewModel
import java.text.SimpleDateFormat

class ResultActivity : InActivity() {

    lateinit var resultBinding: ActivityResultBinding
    lateinit var dataResult: QuizViewModel
    private var time: Long = 0L
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultBinding = ActivityResultBinding.inflate(layoutInflater)
        val view = resultBinding.root
        setContentView(view)
        dataResult = ViewModelProvider(this)[QuizViewModel::class.java]

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
            FirebaseAuth.getInstance()
                .signOut()
            val gso =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                    .build()
            val googleSignInClient = GoogleSignIn.getClient(
                this,
                gso
            )
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
            val intent = Intent(this@ResultActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    private fun dataRetrieve() {
        dataResult.ca.observe(this) { correctInput ->
            resultBinding.textViewCorrectScore.text = correctInput.toString()
        }
        dataResult.wa.observe(this) { wrongInput ->
            resultBinding.textViewWrongScore.text = wrongInput.toString()
        }

        dataResult.scoreData()
    }

    @SuppressLint("SimpleDateFormat")
    override fun onUserInteraction() {
        time = System.currentTimeMillis()
        val interactedTime: String = SimpleDateFormat("HH:mm:ss").format(time).toString()
        val interactedAtTime = currentTime(interactedTime)
        sendInteractedTime(interactedAtTime)
        super.onUserInteraction()
    }
}