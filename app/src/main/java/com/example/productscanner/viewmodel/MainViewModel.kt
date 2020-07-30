package com.example.productscanner.viewmodel

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import androidx.work.*
import com.example.productscanner.repositories.IProductsRepository
import com.example.productscanner.workers.SyncWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.concurrent.TimeUnit

const val syncName = "SyncWorker"

class MainViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val repository: IProductsRepository) : ViewModel() {

    private var workRequest : WorkRequest

    init{
        Log.d("MainViewModel", "create shared VM")
        // Create the Constrains
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        workRequest = OneTimeWorkRequestBuilder<SyncWorker>()
            .setConstraints(constraints)
            .build()
    }

    fun runWorker(sharedPreferences: SharedPreferences, isSettingsChanged: Boolean){
        // Verify to synchronize the data
        val oneDaySync = sharedPreferences.getBoolean("onceADay", false)
        if(oneDaySync){
            val minutes = sharedPreferences.getInt("syncMinutes", 15).toLong()

            // If the settings has changed
            if(isSettingsChanged){
                Log.d("SharedViewModel", "Sync periodically replace")
                sync(ExistingPeriodicWorkPolicy.REPLACE, minutes)
            }else{
                Log.d("SharedViewModel", "Sync periodically keep")
                sync(ExistingPeriodicWorkPolicy.KEEP, minutes)
            }
        }else{
            Log.d("SharedViewModel", "Do not sync")
            WorkManager.getInstance(appContext).cancelUniqueWork(syncName)
        }
    }

    /***
     * Sync the local data with the remote data
     */
    private fun sync(existingPeriodicWorkPolicy: ExistingPeriodicWorkPolicy, minutes: Long){
        val syncWorker = PeriodicWorkRequestBuilder<SyncWorker>(
            minutes, TimeUnit.MINUTES)
            .build()

        WorkManager.getInstance(appContext).enqueueUniquePeriodicWork(
            syncName, existingPeriodicWorkPolicy, syncWorker)
    }
}