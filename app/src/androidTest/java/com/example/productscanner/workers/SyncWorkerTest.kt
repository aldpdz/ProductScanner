package com.example.productscanner.workers

import android.content.Context
import android.util.Log
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.hilt.work.HiltWorkerFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import androidx.work.*
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.NetworkProduct
import com.example.productscanner.di.ProductsRepositoryModule
import com.example.productscanner.getOrAwaitValueInstrumental
import com.example.productscanner.networkToDomain
import com.example.productscanner.repositories.FakeTestRepository
import com.example.productscanner.repositories.IProductsRepository
import com.example.productscanner.util.basicStart
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@SmallTest
@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
@UninstallModules(ProductsRepositoryModule::class)
@HiltAndroidTest
class SyncWorkerTest{

    private lateinit var context: Context

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // Use when dealing with live data
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @BindValue
    @JvmField // avoid issues with Hilt
    val repository: IProductsRepository = FakeTestRepository()

    @Inject
    lateinit var  workerFactory: HiltWorkerFactory

    @Before
    fun setup(){
        context = ApplicationProvider.getApplicationContext()
        val config = Configuration.Builder()
            // Set log level to Log.DEBUG to make it easier to debug
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

        // Initialize WorkManager for instrumentation test.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

    @Before
    fun initRepository(){
        hiltRule.inject()
    }

    @Test
    fun syncSuccess(){
        val networkProduct1 = NetworkProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            1.0f)

        val updateProduct1 = DomainProduct(
            1,
            "Product1",
            "Description product1",
            "Image",
            "sku-product1",
            "upc-product1",
            10,
            10.0f)

        runBlocking {
            (repository as FakeTestRepository).basicStart(listOf(networkProduct1))
            // GIVEN - A product updated locally
            repository.updateProduct(updateProduct1)
        }

        // Get the ListenableWorker
        val worker = TestListenableWorkerBuilder<SyncWorker>(context)
            .setWorkerFactory(workerFactory)
            .build()

        // WHEN - The worker is launched
        // Start the work synchronously
        val result = worker.startWork().get()
        val resultProducts = repository.getProductsFromLocal().getOrAwaitValueInstrumental()
        resultProducts as Result.Success

        // THEN - The worker must be return Success
        assertThat(result, `is`(ListenableWorker.Result.success()))
        // The product must have the data from the network
        assertThat(resultProducts.data.first(), IsEqual(networkToDomain(networkProduct1)))
    }

    @Test
    fun syncNoConnexion(){
        val networkProduct1 = NetworkProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            1.0f)

        runBlocking {
            (repository as FakeTestRepository).basicStart(listOf(networkProduct1))
        }

        // WHEN - Running the work but no internet connexion
        val constrains = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create request
        val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constrains)
            .build()

        val workManager = WorkManager.getInstance(context)
        // Test driver helps you to set constrains or delays as met
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        // Enqueue and wait for result
        workManager.enqueue(request).result.get()
        testDriver!!.setInitialDelayMet(request.id)
        // Get workInfo
        val workInfo = workManager.getWorkInfoById(request.id).get()

        // THEN - There must be a failure result
        assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))
    }

    @Test
    fun syncErrorConnexion(){
        val networkProduct1 = NetworkProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            1.0f)

        runBlocking {
            (repository as FakeTestRepository).basicStart(listOf(networkProduct1))
        }
        (repository as FakeTestRepository).setReturnError(true)

        // WHEN - Running the work but we meet a error connexion
        val constrains = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create request
        val request = PeriodicWorkRequestBuilder<SyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constrains)
            .build()

        val workManager = WorkManager.getInstance(context)
        // Test driver helps you to set constrains or delays as met
        val testDriver = WorkManagerTestInitHelper.getTestDriver(context)
        // Enqueue and wait for result
        workManager.enqueue(request).result.get()
        testDriver!!.setInitialDelayMet(request.id)
        testDriver.setAllConstraintsMet(request.id)
        // Get workInfo
        val workInfo = workManager.getWorkInfoById(request.id).get()

        // THEN - There must be a failure result
        assertThat(workInfo.state, `is`(WorkInfo.State.FAILED))
    }
}