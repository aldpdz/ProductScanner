package com.example.productscanner.workers

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.hilt.work.HiltWorkerFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.filters.SmallTest
import androidx.work.ListenableWorker
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.workDataOf
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.NetworkProduct
import com.example.productscanner.di.ProductsRepositoryModule
import com.example.productscanner.getOrAwaitValueInstrumental
import com.example.productscanner.networkToDomain
import com.example.productscanner.receiver.PRODUCT_ID
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
import javax.inject.Inject

@SmallTest
@RunWith(JUnit4::class)
@ExperimentalCoroutinesApi
@UninstallModules(ProductsRepositoryModule::class)
@HiltAndroidTest
class UpdateWorkerTest{
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
    }

    @Before
    fun initRepository(){
        hiltRule.inject()
    }

    @Test
    fun updateProduct(){
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

        // GIVEN - A product updated and saved as temp
        runBlocking {
            // Logic needed for the worker
            (repository as FakeTestRepository).basicStart(listOf(networkProduct1))
            repository.insertTempProduct(networkToDomain(networkProduct1))
            repository.updateProduct(updateProduct1)
        }

        // Get the ListenableWorker
        val worker = TestListenableWorkerBuilder<UpdateWorker>(
            context = context,
            inputData = workDataOf(PRODUCT_ID to 1))
            .setWorkerFactory(workerFactory)
            .build()

        // WHEN - The product is revert
        val result = worker.startWork().get()
        val resultProducts = repository.getProductsFromLocal().getOrAwaitValueInstrumental()
        resultProducts as Result.Success

        // THEN - The product has its previous values before updating it
        // The worker return Success
        assertThat(result, `is`(ListenableWorker.Result.success()))
        assertThat(resultProducts.data.first(), IsEqual(networkToDomain(networkProduct1)))
    }
}