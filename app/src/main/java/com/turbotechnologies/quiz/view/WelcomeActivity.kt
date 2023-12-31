package com.turbotechnologies.quiz.view

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.turbotechnologies.quiz.R
import com.turbotechnologies.quiz.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var splashBinding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        val view = splashBinding.root
        setContentView(view)
        val alphaAnimation = AnimationUtils.loadAnimation(
            applicationContext,
            R.anim.splash_anim
        )
        splashBinding.layout.startAnimation(alphaAnimation)
        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(
            {
                val intent = Intent(this@WelcomeActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            },
            1500
        )
    }
}