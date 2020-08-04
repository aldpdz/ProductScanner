package com.example.productscanner.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.productscanner.data.Result
import com.example.productscanner.data.database.FakeProductLocalSource
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.*
import com.example.productscanner.domainToNetwork
import com.example.productscanner.getOrAwaitValue
import com.example.productscanner.networkToDomain
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.collection.IsEmptyCollection
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class ProductsRepositoryTest{

    // Executes each task synchronously using Architecture Components
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    // Fake products
    private val product1 = NetworkProduct(
        1,
        "Product1",
        "Description product1",
        "Path image",
        "sku-product1",
        "upc-product1",
        1,
        1.0f
    )

    private val product2 = NetworkProduct(
        2,
        "Product2",
        "Description product2",
        "Path image",
        "sku-product2",
        "upc-product2",
        2,
        2.0f
    )

    private val productsRemote = listOf(product1, product2)

    private lateinit var remoteSource: FakeProductRemoteSource
    private lateinit var localSource: FakeProductLocalSource
    private lateinit var repository: IProductsRepository

    @Before
    fun createRepository(){
        remoteSource = FakeProductRemoteSource(productsRemote)
        localSource = FakeProductLocalSource()
        repository = ProductsRepository(localSource, remoteSource)
    }

    @Test
    fun saveProducts_getProductsFromLocal()= runBlockingTest{
        // When save products to the database
        repository.saveProducts(productsRemote)

        // get the data from the local source
        val result = repository.getProductsFromLocal().getOrAwaitValue()
        result as Result.Success

        // Then the products are the same into the database
        assertThat(domainToNetwork(result.data), IsEqual(productsRemote))
    }

    @Test
    fun insertTempProduct() = runBlockingTest {
        // GIVEN - Two products
        repository.saveProducts(productsRemote)

        val tempProduct = DomainProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            1.0f,
            false)

        // WHEN - Inserting a temporal product
        repository.insertTempProduct(tempProduct)

        val result = repository.getProductsFromLocal().getOrAwaitValue()
        result as Result.Success

        // THEN - The temp product was inserted
        assertThat(result.data.size, `is`(3))
    }

    @Test
    fun updateProduct() = runBlockingTest {

        val updatedProduct = DomainProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            10,
            10.0f,
            false
        )

        repository.saveProducts(productsRemote)

        // When update a product
        repository.updateProduct(updatedProduct)

        // get the product
        val result = repository.getProductsFromLocal().getOrAwaitValue()
        result as Result.Success
        val product = result.data.first { it.id == 1 }

        // Then - the product is updated
        assertThat(product, IsEqual(updatedProduct))

    }

    @Test
    fun getProductsFromRemote_Success() = runBlockingTest{
        // When get the product from the remote source
        repository.getProductsFromRemote()

        // get the data from the local source
        val result = repository.getProductsFromLocal().getOrAwaitValue()
        result as Result.Success

        // Then the products are the saved into the database
        assertThat(domainToNetwork(result.data), IsEqual(productsRemote))
    }

    @Test
    fun getProductsFromRemote_Fail() = runBlockingTest{
        // When get the product from the remote source and there is an error
        remoteSource.setReturnError(true)
        try{
            repository.getProductsFromRemote()
        }catch (e: Exception){
            // It must throw an error
        }

        // get the data from the local source
        val result = repository.getProductsFromLocal().getOrAwaitValue()
        result as Result.Success

        // Then there are not products in the local source
        assertThat(result.data, IsEmptyCollection())
    }

    @Test
    fun getProductBySKU_Success() = runBlockingTest{
        // GIVEN - Two products in the local source
        repository.saveProducts(productsRemote)

        // WHEN - Searching by sku code
        val result = repository.findBySKU("sku-product1")
        result as Result.Success

        // THEN - The result is the product1
        assertThat(result.data, IsEqual(networkToDomain(product1)))
    }

    @Test
    fun getProductBySKU_Fail() = runBlockingTest{
        // WHEN - There are not matches in the search
        localSource.setReturnError(true)
        val result = repository.findBySKU("sku-product1")
        result as Result.Error

        // THEN - There is an exception
        assertThat(result.exception, instanceOf(Exception::class.java))
    }

    @Test
    fun getProductByUPC_Success() = runBlockingTest{
        // GIVEN - Two products in the local source
        repository.saveProducts(productsRemote)

        // WHEN - Searching by upc code
        val result = repository.findByUPC("upc-product1")
        result as Result.Success

        // THEN - The result is the product1
        assertThat(result.data, IsEqual(networkToDomain(product1)))
    }

    @Test
    fun revertProduct() = runBlockingTest {
        // GIVEN - Two products
        repository.saveProducts(productsRemote)

        val tempProduct = DomainProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            1.0f,
            false)

        val updatedProduct = DomainProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            10,
            10.0f,
            false
        )

        // Inserting a temporal product
        repository.insertTempProduct(tempProduct)
        // Updating the product
        repository.updateProduct(updatedProduct)

        // WHEN - Reverting the product
        repository.revertProduct(1)

        val result = repository.getProductsFromLocal().getOrAwaitValue() as Result.Success

        // THEN - The product saved as temp is not modified
        assertThat(result.data.first{it.id == 1}, IsEqual(tempProduct))
    }
}