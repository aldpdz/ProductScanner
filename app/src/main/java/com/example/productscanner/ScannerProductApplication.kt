package com.example.productscanner

import android.app.Application
import androidx.hilt.work.HiltWorkerFactory
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

// initialize a container that is attached to the app's lifecycle
// The application container is the parent container of the app, which means that other
// containers can access the dependencies.
@HiltAndroidApp
class ScannerProductApplication : Application(), androidx.work.Configuration.Provider {

    @Inject lateinit var  workerFactory: HiltWorkerFactory

    override fun getWorkManagerConfiguration() =
        androidx.work.Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
}