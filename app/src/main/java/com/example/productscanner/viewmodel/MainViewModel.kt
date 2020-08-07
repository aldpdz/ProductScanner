package com.example.productscanner.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.example.productscanner.workers.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit

const val syncName = "SyncWorker"

class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context) : ViewModel() {

    fun runWorker(sharedPreferences: SharedPreferences){
        // Verify to synchronize the data
        val oneDaySync = sharedPreferences.getBoolean("onceADay", false)
        if(oneDaySync){
            val minutes = sharedPreferences.getInt("syncMinutes", 15).toLong()
            Log.d("SharedViewModel", "Sync periodically")
            sync(minutes)
        }
        else{
            Log.d("SharedViewModel", "Do not sync")
            WorkManager.getInstance(appContext).cancelUniqueWork(syncName)
        }
    }

    /***
     * Sync the local data with the remote data
     */
    private fun sync(minutes: Long){
        // Create the Constrains
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWorker = PeriodicWorkRequestBuilder<SyncWorker>(
            minutes, TimeUnit.MINUTES) // We can add a flexible interval, min 5 minutes
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
            syncName, ExistingPeriodicWorkPolicy.REPLACE, syncWorker)
    }
}