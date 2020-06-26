package com.example.productscanner.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.productscanner.MainCoroutineRule
import com.example.productscanner.getOrAwaitValue
import com.example.productscanner.model.Product
import com.example.productscanner.repositories.FakeTestRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainActivityViewModelTest{

    // Use a fake repository to be injected into the viewModel
    private lateinit var repository: FakeTestRepository
    // Subject under test
    private lateinit var mainActivityViewModel: MainActivityViewModel
    private lateinit var listProducts: List<Product>

    // Executes each task synchronously using Architecture Components.
    // Runs all the Architecture Components-related background jobs in the same
    // thread so that the test results happen synchronously.
    // Use when dealing with live data
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @ExperimentalCoroutinesApi
    @get:Rule
    var mainCoroutineRule = MainCoroutineRule()

    @Before
    fun setupViewModel(){
        // We initialise the products, with two saved products
        repository = FakeTestRepository()
        // Fake products
        val product1 = Product(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            1.0f,
            false)

        val product2 = Product(
            2,
            "Product2",
            "Description product2",
            "Path image",
            "sku-product2",
            "upc-product2",
            2,
            2.0f,
            true)

        val product3 = Product(
            3,
            "Product3",
            "Description product3",
            "Path image",
            "sku-product3",
            "upc-product3",
            3,
            3.0f,
            true)

        repository.addProducts(product1, product2, product3)
        mainActivityViewModel =  MainActivityViewModel(repository)
    }

    @Test
    fun startApp_callGetProducts(){
        // Then the products must be loaded from the repository
        val products = mainActivityViewModel.products.getOrAwaitValue()
        val loadPreference = mainActivityViewModel.loadPreference.getOrAwaitValue()
        val productsError = mainActivityViewModel.productsError.getOrAwaitValue()
        val status = mainActivityViewModel.status.getOrAwaitValue()

        // The products are loaded from the repository
        assertThat(products, IsEqual(repository.getListProducts()))
        // The preferences can be loaded
        assertThat(loadPreference, `is`(true))
        // There is no error
        assertThat(productsError, `is`(nullValue()))
        // The operation has an status DONE
        assertThat(status, `is`(ProductApiStatus.DONE))
    }

    @Test
    fun updateProduct(){
        // Create a new product to update
        val productUpdated = Product(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            2,
            25.0f,
            false)
        // Update the product in the view model
        mainActivityViewModel.updateProduct(productUpdated)

        // Assert that the product has been updated
        val products = mainActivityViewModel.products.getOrAwaitValue()
        assertThat(productUpdated, IsEqual(products[0]))
    }
}