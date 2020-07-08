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
class SharedViewModelTest{

    // Use a fake repository to be injected into the viewModel
    private lateinit var repository: FakeTestRepository
    // Subject under test
    private lateinit var sharedViewModel: SharedViewModel
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
            "Product1 query",
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
        listProducts = listOf(product1, product2, product3)
        repository.addProducts(product1, product2, product3)
    }

    @Test
    fun displayNavigationToDetail(){
        // GIVEN - a product
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

        // WHEN - calling displayNavigationToDetail
        sharedViewModel = SharedViewModel(repository)
        sharedViewModel.displayNavigationToDetail(product1)

        val display = sharedViewModel.navigationToDetail.getOrAwaitValue()

        // THEN - the product in the viewModel is the same as the one pass as a parameter
        assertThat(display.getContentIfNotHandled(), IsEqual(product1))
    }

    @Test
    fun callGetProducts_Success(){
        sharedViewModel =  SharedViewModel(repository)

        // Then the products must be loaded from the repository
        val products = sharedViewModel.products.getOrAwaitValue()
        val loadPreference = sharedViewModel.loadPreference.getOrAwaitValue()
        val productsError = sharedViewModel.productsError.getOrAwaitValue()
        val status = sharedViewModel.status.getOrAwaitValue()

        // The products are loaded from the repository
        assertThat(products, IsEqual(listProducts))
        // The preferences can be loaded
        assertThat(loadPreference, `is`(true))
        // There is no error
        assertThat(productsError, `is`(nullValue()))
        // The operation has an status DONE
        assertThat(status, `is`(ProductApiStatus.DONE))
    }

    @Test
    fun callGetProducts_Failure(){
        val error = "Error in the connexion"
        repository.error = error
        repository.productsServiceData = null
        sharedViewModel = SharedViewModel(repository)

        // Then the products are not loaded from the repository
        val products = sharedViewModel.products.getOrAwaitValue()
        val productError = sharedViewModel.productsError.getOrAwaitValue()
        val status = sharedViewModel.status.getOrAwaitValue()

        // The products are not loaded
        assertThat(products, IsEqual(emptyList()))
        // Error message
        assertThat(productError, IsEqual(error))
        // With status ERROR
        assertThat(status, `is`(ProductApiStatus.ERROR))
    }

    @Test
    fun updateProduct(){
        sharedViewModel = SharedViewModel(repository)

        // Create a new product to update
        val productUpdated = Product(
            1,
            "Product1 query",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            25,
            25.0f,
            false)
        // Update the product in the view model
        sharedViewModel.updateProduct(productUpdated)

        // Assert that the product has been updated
        val products = sharedViewModel.products.getOrAwaitValue()
        assertThat(products[0], IsEqual(productUpdated))
    }

    @Test
    fun refresh(){
        sharedViewModel = SharedViewModel(repository)

        // Create a new product to update
        val productUpdated = Product(
            1,
            "Product1 query",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            25,
            25.0f,
            false)
        // Update the product in the view model
        sharedViewModel.updateProduct(productUpdated)
        // Refresh the data
        sharedViewModel.refreshData()

        val products = sharedViewModel.products.getOrAwaitValue()

        // THEN - there are not changes in the list
        assertThat(products, IsEqual(listProducts))
    }

    @Test
    fun queryProducts_nullOrEmptyQuery_allProducts(){
        sharedViewModel = SharedViewModel(repository)
        // WHEN - the query is null or empty
        val query = ""
        sharedViewModel.queryProducts(query)

        val filteredProducts = sharedViewModel.productsFiltered.getOrAwaitValue()

        // THEN - all the products are presented in the list
        assertThat(filteredProducts, IsEqual(listProducts))
    }

    @Test
    fun queryProducts_query_productsFiltered(){
        sharedViewModel = SharedViewModel(repository)
        // WHEN - there is a valid string query
        val query = "query"
        sharedViewModel.queryProducts(query)

        val filteredProducts = sharedViewModel.productsFiltered.getOrAwaitValue()

        // THEN - the list just contain one product
        assertThat(filteredProducts, IsEqual(listOf(listProducts[0])))
    }

    @Test
    fun queryProducts_nullProducts_null() {
        // WHEN - there are not products and a valid string query
        repository.productsServiceData = null
        sharedViewModel = SharedViewModel(repository)

        val query = "query_filter"
        sharedViewModel.queryProducts(query)

        val filteredProducts = sharedViewModel.productsFiltered.value

        // THEN - the filtered products are null
        assertThat(filteredProducts, `is`(nullValue()))
    }

    @Test
    fun queryProducts_nullOrEmptyQueryNoProducts_null(){
        // WHEN - there are not products and not a valid query
        repository.productsServiceData = null
        sharedViewModel = SharedViewModel(repository)

        val query = null
        sharedViewModel.queryProducts(query)

        val filteredProducts = sharedViewModel.productsFiltered.value

        // THEN - the filtered products are null
        assertThat(filteredProducts, `is`(nullValue()))
    }
}