package com.turbotechnologies.quiz

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf

class DataObserveWorker(
    private val context: Context,
    private val workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    private val channelID = "data_Observer"

    @RequiresApi(VERSION_CODES.N)
    override suspend fun doWork(): Result {
        startNotification()
        return Result.failure(
            workDataOf(
                "success" to false,
                "error_message" to
                        "An error occurred"
            )
        )
    }


    @RequiresApi(VERSION_CODES.N)
    private fun startNotification() {
        val builder = NotificationCompat.Builder(context, channelID)

        if (Build.VERSION.SDK_INT > VERSION_CODES.O) {
            val channel = NotificationChannel(
                // Parameter 1 -> Channel ID -> String value
                // Parameter 2 -> Name that will be given to this channel
                // Parameter 3 -> Specify the level of importance
                channelID,
                "data_Observer",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager: NotificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)

            // Customization of the notification for devices above O
            builder.setSmallIcon(R.drawable.icon).setContentTitle("Data Sync")
                .setContentText("Data Successfully sent to DB")
        } else {
            // Before Android O, we have priority
            builder.setSmallIcon(R.drawable.icon).setContentTitle("Data Sync")
                .setContentText("Data Successfully sent to DB").priority =
                NotificationManager.IMPORTANCE_HIGH
        }

        // Create an object from the notification manager compact class to show the notification
        val notificationManagerCompat = NotificationManagerCompat.from(context)
        notificationManagerCompat.notify(
            1,
            builder.build()
        )
    }
}

