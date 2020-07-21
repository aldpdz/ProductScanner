package com.example.productscanner.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.productscanner.data.Result
import com.example.productscanner.data.database.FakeProductLocalSource
import com.example.productscanner.data.database.IProductLocalSource
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.*
import com.example.productscanner.domainToNetwork
import com.example.productscanner.getOrAwaitValue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
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
    private lateinit var localSource: IProductLocalSource
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
}