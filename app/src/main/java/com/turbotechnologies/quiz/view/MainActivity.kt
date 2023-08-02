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
import com.turbotechnologies.quiz.R
import com.turbotechnologies.quiz.databinding.ActivityMainBinding
import com.turbotechnologies.quiz.services.DataSyncService
import com.turbotechnologies.quiz.viewModel.QuizViewModel
import java.text.SimpleDateFormat


class MainActivity : InActivity() {

    lateinit var dataView: QuizViewModel
    lateinit var mainBinding: ActivityMainBinding
    var time : Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        val view = mainBinding.root
        setContentView(view)
        dataView = ViewModelProvider(this)[QuizViewModel::class.java]

            //startService(Intent(this@MainActivity, LogOutService::class.java))
        startService(Intent(this, DataSyncService::class.java))

        mainBinding.buttonStartQuiz.setOnClickListener {
            val intent = Intent(this@MainActivity, QuizActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

//    override fun onUserInteraction() {
//        val main = BaseActivity()
//        time = System.currentTimeMillis()
//        main.lastUserInteractedAt(time)
//       Log.d("TimeInMainActivity",time.toString())
//        super.onUserInteraction()
//    }

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
        return super.onOptionsItemSelected(item)
    }

    @SuppressLint("SimpleDateFormat")
    override fun onUserInteraction() {
        time = System.currentTimeMillis()
        val interactedTime = SimpleDateFormat("HH:mm:ss").format(time)
        userInteractedTime(interactedTime.toString())
        super.onUserInteraction()
    }

}