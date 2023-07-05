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
    // now the data can be retrieved from the db using the 'reference' object

    // Assigning the retrieved data to a containers
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

    // Countdown Timer class is an abstract class
    private lateinit var timer: CountDownTimer
    private val totalTime = 25000L // Default time for a qn is 25 seconds.

    // timerContinue -> It will be 'false' when the timer is not running and 'true' when the timer is running.
    var timerContinue = false
    var timeLeft =
        totalTime    // Initially, the remaining time will be started from the total time.

    // Start retrieving the questions from Qn1
    var questionNumber = 0

    // In order to reach the user data in FireBase
    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val user = auth.currentUser

    // Create a database reference to reach the child "scores".
    val scoreRef = database.reference

    // Use hash set as we are randomly generating numbers and there is a possibility of generating a sample number more than once.
    // And hash set array ignores the duplicate number
    val questions = HashSet<Int>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quizBinding = ActivityQuizBinding.inflate(layoutInflater)
        val view = quizBinding.root
        setContentView(view)

        do {
            val number = Random.nextInt(1, 11)
            questions.add(number)
            Log.d("number",number.toString())
        } while (questions.size < 5)
        Log.d("numberOfQuestions",questions.toString())

        gameLogic()
        quizBinding.buttonFinish.setOnClickListener {
            // Send the data to the database
            sendScoreToDB()
        }
        quizBinding.buttonNextQn.setOnClickListener {
            if ((quizBinding.textViewOptionA.currentTextColor == Color.GREEN) ||
                (quizBinding.textViewOptionB.currentTextColor == Color.GREEN) ||
                (quizBinding.textViewOptionC.currentTextColor == Color.GREEN) ||
                (quizBinding.textViewOptionD.currentTextColor == Color.GREEN)
            ) {
                // If the timer is not reset, it will start from the time of the previous qn
                resetTimer()
                gameLogic()
            } else {
                Toast.makeText(applicationContext, "Please provide an answer!", Toast.LENGTH_SHORT)
                    .show()
            }
            // If the user clicks on the next button, the highlighted options should be restored back to default values.
        }
        quizBinding.textViewOptionA.setOnClickListener {
            // Selecting a option should stop the timer.
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

    // Method to retrieve data from RTDB
    private fun gameLogic() {
        // Resetting the options to default
        restoreOptions()
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Data Retrieving is performed in this method and also this method continuously monitors the DB life, if any change in the data it immediately reflects on the app.

                // Knowing the total number of questions using the 'snapshot' object i.e., FireBase lets us know how many children are under a particular parent
                questionCountInDB = snapshot.childrenCount.toInt()

                if (questionNumber < questions.size) {
                    // Data is retrieved from the 'snapshot' object created from the "DataSnapshot" class
                    /* for(questionNumber in questions){
            For loop is not required here as we display one qn after one using "Next" button or "Timer"" } */

                    question = snapshot.child("qn${questions.elementAt(questionNumber)}").child("qn").value.toString()
                    optA = snapshot.child("qn${questions.elementAt(questionNumber)}").child("optA").value.toString()
                    optB = snapshot.child("qn${questions.elementAt(questionNumber)}").child("optB").value.toString()
                    optC = snapshot.child("qn${questions.elementAt(questionNumber)}").child("optC").value.toString()
                    optD = snapshot.child("qn${questions.elementAt(questionNumber)}").child("optD").value.toString()
                    correctAnswer =
                        snapshot.child("qn${questions.elementAt(questionNumber)}").child("answer").value.toString()

                    // Printing the retrieved data to the respective text view
                    quizBinding.textViewQuestion.text = question
                    quizBinding.textViewOptionA.text = optA
                    quizBinding.textViewOptionB.text = optB
                    quizBinding.textViewOptionC.text = optC
                    quizBinding.textViewOptionD.text = optD

                    // Disabling the progress after retrieving the data.
                    quizBinding.progressBarQnPage.visibility = View.INVISIBLE
                    quizBinding.linearLayoutInfo.visibility = View.VISIBLE
                    quizBinding.linearLayoutQns.visibility = View.VISIBLE
                    quizBinding.linearLayoutButtons.visibility = View.VISIBLE
                    //quizBinding.imageViewFinal.visibility = View.INVISIBLE

                    startTimer()

                } else {
                    /*handler.postDelayed({
                        this@QuizActivity
                        quizBinding.progressBarQnPage.visibility = View.INVISIBLE
                        quizBinding.linearLayoutInfo.visibility = View.INVISIBLE
                        quizBinding.linearLayoutQns.visibility = View.INVISIBLE
                        quizBinding.linearLayoutButtons.visibility = View.INVISIBLE
                        quizBinding.imageViewFinal.visibility = View.VISIBLE
                    }, 2000)
                    Toast.makeText(
                        applicationContext,
                        "You have answered all the questions",
                        Toast.LENGTH_SHORT
                    ).show() */

                    // Show a dialog message if all questions are answered
                    finalDialogMessage()
                }
                questionNumber++
            }

            override fun onCancelled(error: DatabaseError) {
                // Action to be taken when the data cannot be retrieved or an error occurs.
                Toast.makeText(
                    applicationContext,
                    error.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    // Function that highlights the correct answer
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

    // Options should be un clickable once one is selected.
    private fun disableOptions() {
        quizBinding.textViewOptionA.isClickable = false
        quizBinding.textViewOptionB.isClickable = false
        quizBinding.textViewOptionC.isClickable = false
        quizBinding.textViewOptionD.isClickable = false
    }

    // Reset the color of the buttons to default
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

        // Enable the buttons
        quizBinding.textViewOptionA.isClickable = true
        quizBinding.textViewOptionB.isClickable = true
        quizBinding.textViewOptionC.isClickable = true
        quizBinding.textViewOptionD.isClickable = true
    }

    // Method to start the timer
    private fun startTimer() {
        timer = object : CountDownTimer(
            // Parameter 1 -> Initial value i.e., the 25 seconds
            // Parameter 2 -> CountDown Interval
            timeLeft, 1000
        ) {
            override fun onTick(millisUntilFinish: Long) {
                // This is what we the timer to do for every second
                // p0 or millisUntilFinish -> It represents the time left in milliseconds until finish.
                timeLeft =
                    millisUntilFinish  // This states that the onTick() will work until the 25 seconds are complete.
                // The time text should update for every second as the interval is 1 second
                updateCountDownText()

            }

            override fun onFinish() {
                // Here we specify what to be done, once the timer finishes.
                // As the time is completed, first the time in the timer should reset and also the time should update.
                resetTimer()
                updateCountDownText()
                // As the time is up, the user must not be able to answer the qn.
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
        // Timer can be paused by using the cancel function
        timer.cancel()
        timerContinue = false // So, timer won't work
    }

    private fun resetTimer() {
        pauseTimer()
        timeLeft = totalTime // set to default.
        updateCountDownText()
    }

    // Function to send the score to the DB
    private fun sendScoreToDB() {
        // Get the User UUID
        //  Check that the user object is not null using the let
        user?.let {
            val userUID = it.uid
            // 'it' keyword represents the "not-null" user object.
            // Send data to DB
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
        dialogMessage.setPositiveButton("Result") {
            // dialogInterface represents the dialog window
            // i parameter returns an integer value when the button is clicked
                dialogInterface, i ->
            // Clicking on see result must save the score
            sendScoreToDB()
        }
        dialogMessage.setNegativeButton("Play Again") { dialogInterface, i ->
            val intent = Intent(this@QuizActivity, MainActivity::class.java)
            startActivity(intent)
            finish() // This is called to remove this activity from the back stack.
        }
        dialogMessage.create().show()
    }


}
