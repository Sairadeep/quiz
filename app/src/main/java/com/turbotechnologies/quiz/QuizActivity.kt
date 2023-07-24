package com.turbotechnologies.quiz

import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.turbotechnologies.quiz.databinding.ActivityQuizBinding
import kotlin.random.Random

class QuizActivity : AppCompatActivity() {
    lateinit var
            quizBinding: ActivityQuizBinding
    val handler = Handler(Looper.getMainLooper())
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val databaseReference = database.reference.child("questions")
    private val databaseScoresReference = database.reference.child("scores")
    var question = ""
    var optA = ""
    var optB = ""
    var optC = ""
    var optD = ""
    var correctAnswer = ""
    var questionCountInDB = 0
    var userAnswer = ""
    private var userCorrectInputScore = 0
    private var userWrongInputScore = 0
    private lateinit var timer: CountDownTimer
    private val totalTime = 25000L
    var timerContinue = false
    var timeLeft =
        totalTime
    var questionNumber = 0
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val user = auth.currentUser
    private val scoreRef = database.reference
    val questions = HashSet<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root
        setContentView(view)

        dataSyncServices()

        do {
            val number = Random.nextInt(1, 11)
            questions.add(number)
            Log.d("number", number.toString())
        } while (questions.size < 5)
        Log.d("numberOfQuestions", questions.toString())

        gameLogic()
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
                gameLogic()
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

    private fun dataSyncServices() {
        databaseScoresReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = auth.currentUser
                val userUID = user?.uid
                if (user != null) {
                    val userCorrectAnswers =
                        snapshot.child(userUID.toString()).child("correctAnswers").value.toString()
                    val userWrongAnswers =
                        snapshot.child(userUID.toString()).child("wrongAnswers").value.toString()
                    val intent = Intent(this@QuizActivity, DataSyncService::class.java)
                    intent.putExtra("correctAnswers", userCorrectAnswers)
                    intent.putExtra("wrongAnswers", userWrongAnswers)
                    ContextCompat.startForegroundService(this@QuizActivity, intent)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun gameLogic() {
        restoreOptions()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                questionCountInDB = snapshot.childrenCount.toInt()
                if (questionNumber < questions.size) {
                    question = snapshot.child("qn${questions.elementAt(questionNumber)}")
                        .child("qn").value.toString()
                    optA = snapshot.child("qn${questions.elementAt(questionNumber)}")
                        .child("optA").value.toString()
                    optB = snapshot.child("qn${questions.elementAt(questionNumber)}")
                        .child("optB").value.toString()
                    optC = snapshot.child("qn${questions.elementAt(questionNumber)}")
                        .child("optC").value.toString()
                    optD = snapshot.child("qn${questions.elementAt(questionNumber)}")
                        .child("optD").value.toString()
                    correctAnswer =
                        snapshot.child("qn${questions.elementAt(questionNumber)}")
                            .child("answer").value.toString()
                    quizBinding.textViewQuestion.text = question
                    quizBinding.textViewOptionA.text = optA
                    quizBinding.textViewOptionB.text = optB
                    quizBinding.textViewOptionC.text = optC
                    quizBinding.textViewOptionD.text = optD

                    quizBinding.progressBarQnPage.visibility = View.INVISIBLE
                    quizBinding.linearLayoutInfo.visibility = View.VISIBLE
                    quizBinding.linearLayoutQns.visibility = View.VISIBLE
                    quizBinding.linearLayoutButtons.visibility = View.VISIBLE

                    startTimer()

                } else {
                    finalDialogMessage()
                }
                questionNumber++
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    applicationContext,
                    error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
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
        // Now put this remaining time value on the time text
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
}
