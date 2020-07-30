package com.example.productscanner.workers

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.productscanner.util.sendSimpleNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker(private val ctx: Context, params: WorkerParameters) : CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
        Log.d("SyncWorker", "Background work")
        val notificationManager = ContextCompat.getSystemService(ctx,
        NotificationManager::class.java) as NotificationManager

        notificationManager.sendSimpleNotification(ctx)

        return@withContext Result.success()
    }
}