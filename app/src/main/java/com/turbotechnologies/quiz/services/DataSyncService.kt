package com.turbotechnologies.quiz.services

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.turbotechnologies.quiz.R
import com.turbotechnologies.quiz.view.MainActivity


class DataSyncService : Service() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val dataBaseScore: DatabaseReference = database.reference.child("scores")
    var userCorrectScore = ""
    var userWrongScore = ""

    private fun dataForService(userScore: (userCorrect: String, userWrong: String) -> Unit) {
        dataBaseScore.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val user = auth.currentUser
                val userId = user?.uid
                if (user != null) {
                    userCorrectScore =
                        snapshot.child(userId.toString()).child("correctAnswers").value.toString()
                    userWrongScore =
                        snapshot.child(userId.toString()).child("wrongAnswers").value.toString()
                }
                Log.d("userCorrectScore", userCorrectScore)
                userScore(userCorrectScore, userWrongScore)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        notification()
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    private fun notification() {
        Log.d("ServiceStart","Service has been started.")
        var userPositiveResponse: String
        var userNegativeResponse: String
        dataForService { userCorrect, userWrong ->
            userPositiveResponse = userCorrect
            userNegativeResponse = userWrong

            val notificationIntent = Intent(this, MainActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
            val notificationChannelID = "DataSync Notification"

            val notificationBuilder =
                NotificationCompat.Builder(this, notificationChannelID)

            val channel = NotificationChannel(
                notificationChannelID,
                "notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager: NotificationManager by lazy {
                getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            }
            notificationManager.createNotificationChannel(channel)

            notificationBuilder.setContentTitle("Turbo Technologies")
                .setContentText("Data updated Successfully!").setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        "Updated Results \n" +
                                " Correct Answers: $userPositiveResponse \n" +
                                " Wrong Answers: $userNegativeResponse"
                    )
                )
                .setSmallIcon(R.drawable.ic_launcher_background).priority =
                NotificationCompat.PRIORITY_MAX
            notificationBuilder.setContentIntent(pendingIntent)
            val notification = notificationBuilder.build()
            startForeground(1, notification)
        }
    }
}



