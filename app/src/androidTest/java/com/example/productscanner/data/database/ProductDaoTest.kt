package com.example.productscanner.data.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.example.productscanner.getOrAwaitValueInstrumental
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.core.IsEqual
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest // unit test
class ProductDaoTest {

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: ProductsDatabase

    @Before
    fun initDb(){
        // Disappears when the process is killed
        database = Room.inMemoryDatabaseBuilder(
            getApplicationContext(),
            ProductsDatabase::class.java
        ).build()
    }

    @After
    fun closeDb() = database.close() // Clean up database

    @Test
    fun insertAll_getProducts() = runBlockingTest {
        // GIVEN - Insert a product.
        val product = DatabaseProduct(
            1, "Product1", "Description", "Picture", "sku-code",
            "upc-code", 25, 15.0f
        )
        database.productDao.insertAll(listOf(product))

        // WHEN - Get all the products from the database.
        val productsSaved = database.productDao.getProducts()

        // THEN - The saved data contains the expected values.
        assertThat<DatabaseProduct>(productsSaved.getOrAwaitValueInstrumental()[0] as DatabaseProduct, notNullValue())
        assertThat(productsSaved.getOrAwaitValueInstrumental()[0], IsEqual(product))
    }
}