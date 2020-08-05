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
import org.hamcrest.core.IsNull
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
        val tempProduct = DatabaseProduct(
            TEMP_ID, "TempProduct", "Description", "Picture", "sku-code",
            "upc-code", 0, 0.0f
        )

        database.productDao.insertAll(listOf(product, tempProduct))

        // WHEN - Get all the products from the database.
        val productsSaved = database.productDao.getProducts()

        // THEN - The saved data contains the expected values.
        assertThat(productsSaved.getOrAwaitValueInstrumental().first(), notNullValue())
        assertThat(productsSaved.getOrAwaitValueInstrumental().first(), IsEqual(product))
    }

    @Test
    fun insertProduct() = runBlockingTest {
        // GIVEN - Insert a product
        val product = DatabaseProduct(
            1, "Product1", "Description", "Picture", "sku-code",
            "upc-code", 25, 15.0f
        )
        database.productDao.insert(product)

        // WHEN - Get all the products from the database.
        val productsSaved = database.productDao.getProducts()

        // THEN - The saved data contains the expected values.
        assertThat(productsSaved.getOrAwaitValueInstrumental()[0], notNullValue())
        assertThat(productsSaved.getOrAwaitValueInstrumental()[0], IsEqual(product))
    }

    @Test
    fun updateProduct() = runBlockingTest {
        // GIVEN - Insert a product.
        val product = DatabaseProduct(
        1, "Product1", "Description", "Picture", "sku-code",
        "upc-code", 25, 15.0f)
        database.productDao.insertAll(listOf(product))

        // WHEN - Update one of the products
        val updatedProduct = DatabaseProduct(
            1, "Product1", "Description", "Picture", "sku-code",
            "upc-code", 50, 30.0f)
        database.productDao.updateProduct(updatedProduct)
        val productsSaved = database.productDao.getProducts()

        // THEN - The product has been updated
        assertThat(productsSaved.getOrAwaitValueInstrumental()[0], notNullValue())
        assertThat(productsSaved.getOrAwaitValueInstrumental()[0], IsEqual(updatedProduct))
    }

    @Test
    fun getProductBySKU_productFound() = runBlockingTest{
        // GIVEN - Two Products.
        val product1 = DatabaseProduct(
            1, "Product1", "Description", "Picture", "sku-code1",
            "upc-code1", 25, 15.0f)
        val product2 = DatabaseProduct(
            2, "Product2", "Description", "Picture", "sku-code2",
            "upc-code2", 25, 15.0f)
        database.productDao.insertAll(listOf(product1, product2))

        // WHEN - Searching a product by sku code
        val result = database.productDao.getProductBySKU("sku-code1")

        // THEN - The result is the product1
        assertThat(result, IsEqual(product1))
    }

    @Test
    fun getProductByID() = runBlockingTest{
        // GIVEN - Two Products.
        val product1 = DatabaseProduct(
            1, "Product1", "Description", "Picture", "sku-code1",
            "upc-code1", 25, 15.0f)
        val product2 = DatabaseProduct(
            2, "Product2", "Description", "Picture", "sku-code2",
            "upc-code2", 25, 15.0f)
        database.productDao.insertAll(listOf(product1, product2))

        // WHEN - Searching product by id
        val result = database.productDao.getProduct(1)

        // THEN - The result is the product1
        assertThat(result, IsEqual(product1))
    }

    @Test
    fun getProductBySKU_productNotFound() = runBlockingTest{
        // GIVEN - One Products.
        val product1 = DatabaseProduct(
            1, "Product1", "Description", "Picture", "sku-code1",
            "upc-code1", 25, 15.0f)
        database.productDao.insertAll(listOf(product1))

        // WHEN - Searching a product by sku code
        val result = database.productDao.getProductBySKU("sku-code2")

        // THEN - The result is the product1
        assertThat(result, IsNull())
    }

    @Test
    fun getProductByUPC_productFound() = runBlockingTest{
        // GIVEN - Two Products.
        val product1 = DatabaseProduct(
            1, "Product1", "Description", "Picture", "sku-code1",
            "upc-code1", 25, 15.0f)
        val product2 = DatabaseProduct(
            2, "Product2", "Description", "Picture", "sku-code2",
            "upc-code2", 25, 15.0f)
        database.productDao.insertAll(listOf(product1, product2))

        // WHEN - Searching a product by upc code
        val result = database.productDao.getProductByUPC("upc-code1")

        // THEN - The result is the product1
        assertThat(result, IsEqual(product1))
    }

    @Test
    fun getProductByUPC_productNotFound() = runBlockingTest{
        // GIVEN - One Products.
        val product1 = DatabaseProduct(
            1, "Product1", "Description", "Picture", "sku-code1",
            "upc-code1", 25, 15.0f)
        database.productDao.insertAll(listOf(product1))

        // WHEN - Searching a product by sku code
        val result = database.productDao.getProductByUPC("upc-code2")

        // THEN - The result is the product1
        assertThat(result, IsNull())
    }
}