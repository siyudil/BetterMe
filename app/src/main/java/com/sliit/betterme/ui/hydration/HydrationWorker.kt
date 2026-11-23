package com.sliit.betterme.ui.hydration

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.sliit.betterme.util.NotificationHelper

class HydrationWorker(appContext: Context, params: WorkerParameters) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        NotificationHelper.notifyHydration(applicationContext)
        return Result.success()
    }
}
