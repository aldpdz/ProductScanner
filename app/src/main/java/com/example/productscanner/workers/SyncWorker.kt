package com.example.productscanner.workers

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.hilt.Assisted
import androidx.hilt.work.WorkerInject
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.productscanner.repositories.IProductsRepository
import com.example.productscanner.util.sendSimpleNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SyncWorker @WorkerInject constructor(
    @Assisted private val ctx: Context,
    @Assisted params: WorkerParameters,
    private val repository: IProductsRepository)
    : CoroutineWorker(ctx, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO){
        Log.d("SyncWorker", "Background work")

        // Retrieve data from the network
        try {
            repository.getProductsFromRemote()
            // Send notification
            val notificationManager = ContextCompat.getSystemService(ctx,
                NotificationManager::class.java) as NotificationManager
            notificationManager.cancelAll()
            notificationManager.sendSimpleNotification(ctx)
            return@withContext Result.success()
        }catch (e: Exception) {
            return@withContext Result.failure()
        }
    }
}