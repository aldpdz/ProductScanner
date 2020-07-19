package com.example.productscanner.data.network

import com.example.productscanner.data.Result
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.Matchers.instanceOf
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ProductRemoteSourceTest{

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

    private val productsFromApi = listOf(product1, product2)
    private val fakeProductsApiService = FakeProductsApiService(productsFromApi.toMutableList())
    private lateinit var productsRemoteSource: ProductRemoteSource

    @Before
    fun createRemoteSource(){
        productsRemoteSource = ProductRemoteSource(fakeProductsApiService)
    }

    @Test
    fun getProducts_networkAvailable_Success() = runBlockingTest {
        // When products are requested from the products remote source
        val result = productsRemoteSource.getProducts()
        result as Result.Success

        // Then products are loaded from the apiService
        assertThat(result.data, IsEqual(productsFromApi))
    }

    @Test
    fun getProducts_networkNotAvailable_Error() = runBlockingTest {
        // When products are requested and there is an error
        fakeProductsApiService.setReturnError(true)
        val result = productsRemoteSource.getProducts()
        result as Result.Error

        // Then there is an error in the process
        assertThat(result, instanceOf(Result.Error::class.java))
    }
}