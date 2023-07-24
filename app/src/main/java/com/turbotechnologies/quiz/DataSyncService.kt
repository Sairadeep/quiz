package com.turbotechnologies.quiz

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Build.VERSION_CODES
import android.os.IBinder
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat


class DataSyncService : Service() {

    @RequiresApi(VERSION_CODES.N)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val userCorrectScore = intent?.getStringExtra("correctAnswers")
        val userWrongScore = intent?.getStringExtra("wrongAnswers")
        if ((userCorrectScore != null) && (userWrongScore != null)) {
            notification(userCorrectScore, userWrongScore)
        }
        return super.onStartCommand(intent, flags, startId)
    }


    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    @RequiresApi(VERSION_CODES.N)
    private fun notification(userCorrectScore: String, userWrongScore: String) {

        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notificationChannelID = "DataSync Notification"

        val notificationBuilder =
            NotificationCompat.Builder(this, notificationChannelID)

        if (Build.VERSION.SDK_INT >= VERSION_CODES.O) {
            val channel = NotificationChannel(
                notificationChannelID,
                "notification",
                NotificationManager.IMPORTANCE_HIGH
            )

            val notificationManager: NotificationManager by lazy {
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            }

            Toast.makeText(applicationContext, "Hey You", Toast.LENGTH_SHORT).show()
            notificationManager.createNotificationChannel(channel)

            notificationBuilder.setContentTitle("Turbo Technologies")
                .setContentText("Data updated Successfully!").setStyle(
                    NotificationCompat.BigTextStyle().bigText(
                        "Hey here are the results \n" +
                                " Correct Answers: $userCorrectScore \n" +
                                " Wrong Answers: $userWrongScore"
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



