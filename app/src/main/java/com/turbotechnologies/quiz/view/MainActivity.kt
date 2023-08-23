package com.turbotechnologies.quiz.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.snackbar.Snackbar
import com.turbotechnologies.quiz.R
import com.turbotechnologies.quiz.databinding.ActivityMainBinding
import com.turbotechnologies.quiz.services.DataSyncService
import com.turbotechnologies.quiz.services.LogOutService
import com.turbotechnologies.quiz.viewModel.QuizViewModel


class MainActivity : InActivity() {

    private lateinit var dataView: QuizViewModel
    private lateinit var mainBinding: ActivityMainBinding
    var timeReceived = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        dataView = ViewModelProvider(this)[QuizViewModel::class.java]

        startService(Intent(this@MainActivity, LogOutService::class.java))
        startService(Intent(this@MainActivity, DataSyncService::class.java))

        Snackbar.make(
            mainBinding.mainActivityLayout,
            "Choose the question timer from the vertical ellipsis option " + "⋮",
            Snackbar.LENGTH_LONG
        ).show()

        mainBinding.buttonStartQuiz.setOnClickListener {
            var totalTime = timerData(timeReceived).toInt()
            Log.d("KnowingValue", totalTime.toString())
            val intent = Intent(this@MainActivity, QuizActivity::class.java)
            if (totalTime == 0) {
                totalTime = 25
                intent.putExtra("timeValue", totalTime)
            }
            intent.putExtra("timeValue", totalTime)
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
            auth.signOut()
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
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
        if (item.itemId == R.id.qnTimer) {
            val fragmentManager: FragmentManager = supportFragmentManager
            val qnTimerFragment = QnTimerFragment()
            qnTimerFragment.isCancelable = false
            qnTimerFragment.show(
                fragmentManager,
                "QnTimerFragment"
            )
        }
        return super.onOptionsItemSelected(item)
    }

    fun timerData(selectedTime: Long): Long {
        timeReceived = selectedTime
        Log.d("timeReceived", timeReceived.toString())
        return timeReceived
    }

}