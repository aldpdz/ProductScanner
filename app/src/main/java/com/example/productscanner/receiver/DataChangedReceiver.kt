package com.example.productscanner.receiver

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.content.ContextCompat
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.productscanner.util.ID_NOTIFICATION_PRODUCT_UPDATED
import com.example.productscanner.workers.UpdateWorker

const val IS_CANCEL = "cancel"

class DataChangedReceiver: BroadcastReceiver(){
    override fun onReceive(context: Context, intent: Intent) {
        val isCancel = intent.getBooleanExtra(IS_CANCEL, false)
        if(isCancel){
            // Revert data
            val workRequest = OneTimeWorkRequestBuilder<UpdateWorker>()
                .build()
            WorkManager.getInstance(context).enqueue(workRequest)
        }

        val notificationManager = ContextCompat.getSystemService(
            context, NotificationManager::class.java
        ) as NotificationManager
        notificationManager.cancel(ID_NOTIFICATION_PRODUCT_UPDATED)
    }
}