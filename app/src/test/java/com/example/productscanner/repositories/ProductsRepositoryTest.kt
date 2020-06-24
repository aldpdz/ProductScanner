package com.example.productscanner.repositories

import com.example.productscanner.model.FakeProductsApiService
import com.example.productscanner.model.Product
import com.example.productscanner.model.ProductsApiService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

@ExperimentalCoroutinesApi
class ProductsRepositoryTest{
    // Fake products
    private val product1 = Product(
        1,
        "Product1",
        "Description product1",
        "Path image",
        "sku-product1",
        "upc-product1",
        1,
        1.0f,
        false)

    private val product2 = Product(
        2,
        "Product2",
        "Description product2",
        "Path image",
        "sku-product2",
        "upc-product2",
        2,
        2.0f,
        false)

    private val product3 = Product(
        3,
        "Product3",
        "Description product3",
        "Path image",
        "sku-product3",
        "upc-product3",
        3,
        3.0f,
        false)

    private val productsFromApi = listOf(product1, product2, product3)

    private lateinit var productsRepository: ProductsApiService

    @Before
    fun createRepository(){
        productsRepository = FakeProductsApiService(productsFromApi.toMutableList())
    }

    @Test
    fun getProducts_requestAllProductsFromApiService() = runBlockingTest{
        // When products are requested from the products repository
        val products = productsRepository.getProducts()

        // Then products are loaded from the apiService
        assertThat(products.body(), IsEqual(productsFromApi))
    }
}