package com.turbotechnologies.quiz.view

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.database.FirebaseDatabase
import com.turbotechnologies.quiz.R
import com.turbotechnologies.quiz.databinding.ActivityQuizBinding
import com.turbotechnologies.quiz.viewModel.QuizViewModel
import java.text.SimpleDateFormat
import kotlin.random.Random

class QuizActivity : InActivity() {
    lateinit var
            quizBinding: ActivityQuizBinding
    private val handler = Handler(Looper.getMainLooper())
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    lateinit var qnData: QuizViewModel
    var correctAnswer = ""
    private var userAnswer = ""
    private var userCorrectInputScore = 0
    private var userWrongInputScore = 0
    private lateinit var timer: CountDownTimer
    private val totalTime = 25000L
    var timerContinue = false
    var timeLeft =
        totalTime
    var index = 0
    private var time: Long = 0L
    var qnSet = HashSet<Int>()
    private lateinit var currentQn: Map<String, String>
    private val user = auth.currentUser
    private val scoreRef = database.reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root
        setContentView(view)
        qnData = ViewModelProvider(this)[QuizViewModel::class.java]
        gameLogic()
        do {
            val number = Random.nextInt(1, 10)
            qnSet.add(number)
        } while (qnSet.size < 5)
        Log.d("qnSet", qnSet.toString())

        quizBinding.buttonFinish.setOnClickListener {
            sendScoreToDB()
        }
        quizBinding.buttonNextQn.setOnClickListener {
            if ((quizBinding.textViewOptionA.currentTextColor == Color.GREEN) ||
                (quizBinding.textViewOptionB.currentTextColor == Color.GREEN) ||
                (quizBinding.textViewOptionC.currentTextColor == Color.GREEN) ||
                (quizBinding.textViewOptionD.currentTextColor == Color.GREEN)
            ) {
                resetTimer()
                if (index < 4) {
                    index++
                    gameLogic()
                    Log.d("index", index.toString())
                } else {
                    finalDialogMessage()
                }
            } else {
                Toast.makeText(applicationContext, "Please provide an answer!", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        quizBinding.textViewOptionA.setOnClickListener {
            pauseTimer()
            userAnswer = "optA"
            if (correctAnswer == userAnswer) {
                quizBinding.textViewOptionA.setTextColor(Color.GREEN)
                userCorrectInputScore++
                quizBinding.textViewRightAnswerCount.text = userCorrectInputScore.toString()
            } else {
                quizBinding.textViewOptionA.setTextColor(Color.RED)
                userWrongInputScore++
                quizBinding.textViewWrongAnswerCount.text = userWrongInputScore.toString()
                actualAnswer()
            }
            disableOptions()
            handler.postDelayed(
                {
                    QuizActivity::class.java
                    quizBinding.buttonNextQn.performClick()
                }, 4000
            )
        }
        quizBinding.textViewOptionB.setOnClickListener {
            pauseTimer()
            userAnswer = "optB"
            if (correctAnswer == userAnswer) {
                quizBinding.textViewOptionB.setTextColor(Color.GREEN)
                userCorrectInputScore++
                quizBinding.textViewRightAnswerCount.text = userCorrectInputScore.toString()
            } else {
                quizBinding.textViewOptionB.setTextColor(Color.RED)
                userWrongInputScore++
                quizBinding.textViewWrongAnswerCount.text = userWrongInputScore.toString()
                actualAnswer()
            }
            disableOptions()
        }
        quizBinding.textViewOptionC.setOnClickListener {
            pauseTimer()
            userAnswer = "optC"
            if (correctAnswer == userAnswer) {
                quizBinding.textViewOptionC.setTextColor(Color.GREEN)
                userCorrectInputScore++
                quizBinding.textViewRightAnswerCount.text = userCorrectInputScore.toString()
            } else {
                quizBinding.textViewOptionC.setTextColor(Color.RED)
                userWrongInputScore++
                quizBinding.textViewWrongAnswerCount.text = userWrongInputScore.toString()
                actualAnswer()
            }
            disableOptions()
        }
        quizBinding.textViewOptionD.setOnClickListener {
            pauseTimer()
            userAnswer = "optD"
            if (correctAnswer == userAnswer) {
                quizBinding.textViewOptionD.setTextColor(Color.GREEN)
                userCorrectInputScore++
                quizBinding.textViewRightAnswerCount.text = userCorrectInputScore.toString()
            } else {
                quizBinding.textViewOptionD.setTextColor(Color.RED)
                userWrongInputScore++
                quizBinding.textViewWrongAnswerCount.text = userWrongInputScore.toString()
                actualAnswer()
            }
            disableOptions()
        }
    }

    private fun gameLogic() {
        restoreOptions()
        var qnIndex = 0
        qnData.question.observe(this) { qn ->
            val currentCount = qn.size
            qnIndex = qnSet.elementAt(index)
            currentQn = qn[qnIndex]
            quizBinding.textViewQuestion.text = currentQn["qn"]
            quizBinding.textViewOptionA.text = currentQn["optA"]
            quizBinding.textViewOptionB.text = currentQn["optB"]
            quizBinding.textViewOptionC.text = currentQn["optC"]
            quizBinding.textViewOptionD.text = currentQn["optD"]
            correctAnswer = currentQn["correctAnswer"].toString()
            Log.d("Count:", currentCount.toString())
        }
        Log.d("qnNumber", qnIndex.toString())
        qnData.qnestions()

        quizBinding.progressBarQnPage.visibility = View.INVISIBLE
        quizBinding.linearLayoutInfo.visibility = View.VISIBLE
        quizBinding.linearLayoutQns.visibility = View.VISIBLE
        quizBinding.linearLayoutButtons.visibility = View.VISIBLE

        startTimer()
    }

    private fun actualAnswer() {
        when (correctAnswer) {
            "optA" -> {
                quizBinding.textViewOptionA.setTextColor(Color.GREEN)
                quizBinding.textViewOptionA.setBackgroundResource(R.drawable.button_shape)
            }
            "optB" -> {
                quizBinding.textViewOptionB.setTextColor(Color.GREEN)
                quizBinding.textViewOptionB.setBackgroundResource(R.drawable.button_shape)
            }
            "optC" -> {
                quizBinding.textViewOptionC.setTextColor(Color.GREEN)
                quizBinding.textViewOptionC.setBackgroundResource(R.drawable.button_shape)
            }
            "optD" -> {
                quizBinding.textViewOptionD.setTextColor(Color.GREEN)
                quizBinding.textViewOptionD.setBackgroundResource(R.drawable.button_shape)
            }
        }
    }

    private fun disableOptions() {
        quizBinding.textViewOptionA.isClickable = false
        quizBinding.textViewOptionB.isClickable = false
        quizBinding.textViewOptionC.isClickable = false
        quizBinding.textViewOptionD.isClickable = false
    }

    private fun restoreOptions() {
        quizBinding.textViewQuestion.setBackgroundResource(R.drawable.button_shape)
        quizBinding.textViewOptionA.setBackgroundResource(R.drawable.button_shape)
        quizBinding.textViewOptionB.setBackgroundResource(R.drawable.button_shape)
        quizBinding.textViewOptionC.setBackgroundResource(R.drawable.button_shape)
        quizBinding.textViewOptionD.setBackgroundResource(R.drawable.button_shape)

        quizBinding.textViewOptionA.setTextColor(Color.BLACK)
        quizBinding.textViewOptionB.setTextColor(Color.BLACK)
        quizBinding.textViewOptionC.setTextColor(Color.BLACK)
        quizBinding.textViewOptionD.setTextColor(Color.BLACK)

        quizBinding.textViewOptionA.isClickable = true
        quizBinding.textViewOptionB.isClickable = true
        quizBinding.textViewOptionC.isClickable = true
        quizBinding.textViewOptionD.isClickable = true
    }

    private fun startTimer() {
        timer = object : CountDownTimer(
            timeLeft, 1000
        ) {
            override fun onTick(millisUntilFinish: Long) {
                timeLeft =
                    millisUntilFinish
                updateCountDownText()

            }

            override fun onFinish() {
                resetTimer()
                updateCountDownText()
                Toast.makeText(
                    applicationContext,
                    "Sorry, time up, moving to the next question",
                    Toast.LENGTH_LONG
                ).show()
                actualAnswer()
                userWrongInputScore++
                quizBinding.textViewWrongAnswerCount.text = userWrongInputScore.toString()
                disableOptions()
                timerContinue = false
                handler.postDelayed(
                    {
                        QuizActivity::class.java
                        quizBinding.buttonNextQn.performClick()
                    }, 2000
                )
            }
        }.start()
        timerContinue = true
    }

    private fun updateCountDownText() {
        val remainingTime: Int = (timeLeft / 1000).toInt()
        quizBinding.textViewTime.text = remainingTime.toString()
    }

    private fun pauseTimer() {
        timer.cancel()
        timerContinue = false
    }

    private fun resetTimer() {
        pauseTimer()
        timeLeft = totalTime
        updateCountDownText()
    }

    private fun sendScoreToDB() {
        user?.let {
            val userUID = it.uid
            scoreRef.child("scores").child(userUID).child("correctAnswers")
                .setValue(userCorrectInputScore)
            scoreRef.child("scores").child(userUID).child("wrongAnswers")
                .setValue(userWrongInputScore).addOnSuccessListener {
                    Toast.makeText(
                        applicationContext,
                        "Scores successfully sent to DB",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this@QuizActivity, ResultActivity::class.java)
                    startActivity(intent)
                    finish()
                }
        }
    }

    private fun finalDialogMessage() {
        val dialogMessage = AlertDialog.Builder(this@QuizActivity)
        dialogMessage.setTitle("Quiz Game")
        dialogMessage.setMessage("Congratulations!! \nYou have answered all the questions. Do you want to see the results?")
        dialogMessage.setCancelable(false)
        dialogMessage.setPositiveButton("Result") { dialogInterface, i ->
            sendScoreToDB()
        }
        dialogMessage.setNegativeButton("Play Again") { dialogInterface, i ->
            val intent = Intent(this@QuizActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        dialogMessage.create().show()
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
