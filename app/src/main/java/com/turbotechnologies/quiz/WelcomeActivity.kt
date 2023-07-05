package com.turbotechnologies.quiz

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.turbotechnologies.quiz.databinding.ActivityWelcomeBinding

class WelcomeActivity : AppCompatActivity() {
    private lateinit var splashBinding: ActivityWelcomeBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        splashBinding = ActivityWelcomeBinding.inflate(layoutInflater)
        val view = splashBinding.root
        setContentView(view) // Now we can access the components in the Welcome activity using the "splashBinding" object.
        // Create an object from the animation class
        val alphaAnimation = AnimationUtils.loadAnimation(
            // Parameter 2 -> Path of the alpha animation which we created
            applicationContext,
            R.anim.splash_anim
        )
        splashBinding.imageViewSplash.startAnimation(alphaAnimation)
        // After 4 seconds, the welcome activity should automatically close and the main activity should open -> This can be done using the "HANDLER" class.
        // "Handler" allows us to prepare a schedule for the process to be made
        // As the parameter list constructor of the handler class is deprecated, we need to define a constructor parameter and it can be the object of the "Looper" class as "Looper.getMainLooper()
        val handler = Handler(Looper.getMainLooper())
        // Using the "handler" object, we can the post delayed function.
        handler.postDelayed(
            // Parameter 1 -> object of the Runnable class as it is an interface and we can't create an object from the interface.....Use the object from the anonymous class
            // Parameter 2 -> Duration of the delay => It will hold the process for the specified duration and then execute the code defined in the run()
            { // The Welcome splash activity will be for 4 seconds and after another 1 second, we need to load the main activity
                val intent = Intent (this@WelcomeActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            },
            5000
        )
    }
}