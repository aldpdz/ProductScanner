package com.example.productscanner

import android.app.Application
import com.example.productscanner.repositories.IProductsRepository
import dagger.hilt.android.HiltAndroidApp

// initialize a container that is attached to the app's lifecycle
// The application container is the parent container of the app, which means that other
// containers can access the dependencies.
@HiltAndroidApp
class ScannerProductApplication : Application() {
}