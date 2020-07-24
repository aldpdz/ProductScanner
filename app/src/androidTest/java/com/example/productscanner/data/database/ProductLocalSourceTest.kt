package com.example.productscanner.data.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.productscanner.MainCoroutineRuleInstrumental
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.NetworkProduct
import com.example.productscanner.getOrAwaitValueInstrumental
import com.example.productscanner.networkToDomain
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest // Integration test
class ProductLocalSourceTest{

    // Set the main coroutines dispatcher for unit testing.
    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRuleInstrumental()

    // Executes each task synchronously using Architecture Components
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localSource: ProductLocalSource
    private lateinit var database: ProductsDatabase

    @Before
    fun setup(){
        // It doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ProductsDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localSource = ProductLocalSource(
            database.productDao,
            Dispatchers.Main
        )
    }

    @After
    fun cleanUp(){
        database.close()
    }

    @Test
    fun insertProducts_getProducts() = runBlockingTest{
        // GIVEN - A new product added to the database
        val networkProduct = NetworkProduct(
            0, "product", "description", "picture", "sku-code",
            "upc-code", 0, 25.0f)
        localSource.insertProducts(listOf(networkProduct))
        val resultProduct = DomainProduct(
            0, "product", "description", "picture", "sku-code",
            "upc-code", 0, 25.0f, false)

        // WHEN - Retrieve all the tasks
        val result = localSource.getProducts().getOrAwaitValueInstrumental()
        result as Result.Success

        // THEN - Same product is returned
        assertThat(result.data[0], IsEqual(resultProduct))
    }

    @Test
    fun updateProduct() = runBlockingTest {
        // GIVEN - A new product added to the database and an updated product
        val networkProduct = NetworkProduct(
            0, "product", "description", "picture", "sku-code",
            "upc-code", 0, 25.0f)
        localSource.insertProducts(listOf(networkProduct))
        val updateProduct = DomainProduct(
            0, "product", "description", "picture", "sku-code",
            "upc-code", 1, 50.0f, false)

        // WHEN - Update the product
        localSource.updateProduct(updateProduct)
        val result = localSource.getProducts().getOrAwaitValueInstrumental()
        result as Result.Success

        // THEN - The updated product is returned
        assertThat(result.data[0], IsEqual(updateProduct))
    }

    @Test
    fun getProductByUPC() = runBlockingTest{
        // GIVEN - Two products
        val networkProduct1 = NetworkProduct(
            0, "product1", "description", "picture", "sku-code1",
            "upc-code1", 0, 25.0f)
        val networkProduct2 = NetworkProduct(
            0, "product2", "description", "picture", "sku-code2",
            "upc-code2", 0, 25.0f)
        localSource.insertProducts(listOf(networkProduct1, networkProduct2))

        // WHEN - Searching product2 by upc code
        val result = localSource.getProductByUPC("upc-code2") as Result.Success

        // THEN - The product is product2
        assertThat(result.data, IsEqual(networkToDomain(networkProduct2)))
    }

    @Test
    fun getProductBySKU() = runBlockingTest{
        // GIVEN - Two products
        val networkProduct1 = NetworkProduct(
            0, "product1", "description", "picture", "sku-code1",
            "upc-code1", 0, 25.0f)
        val networkProduct2 = NetworkProduct(
            0, "product2", "description", "picture", "sku-code2",
            "upc-code2", 0, 25.0f)
        localSource.insertProducts(listOf(networkProduct1, networkProduct2))

        // WHEN - Searching product2 by sku code
        val result = localSource.getProductBySKU("sku-code2") as Result.Success

        // THEN - The product is product2
        assertThat(result.data, IsEqual(networkToDomain(networkProduct2)))
    }
}