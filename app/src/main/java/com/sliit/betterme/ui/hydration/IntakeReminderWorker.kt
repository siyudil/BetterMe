package com.sliit.betterme.ui.hydration

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sliit.betterme.R
import com.sliit.betterme.util.NotificationHelper

class IntakeReminderWorker(appContext: Context, params: WorkerParameters) : Worker(appContext, params) {
    override fun doWork(): Result {
        val title = inputData.getString("title") ?: applicationContext.getString(R.string.app_name)
        val message = inputData.getString("message") ?: "Time to drink water 💧"
        NotificationHelper.ensureChannel(applicationContext)
        val n = NotificationCompat.Builder(applicationContext, "hydration_channel")
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .build()
        NotificationManagerCompat.from(applicationContext).notify((System.currentTimeMillis()%100000).toInt(), n)
        return Result.success()
    }
}
