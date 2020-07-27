package com.example.productscanner.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.productscanner.MainCoroutineRule
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.data.network.NetworkProduct
import com.example.productscanner.repositories.FakeTestRepository
import com.example.productscanner.domainToNetwork
import com.example.productscanner.getOrAwaitValue
import com.example.productscanner.observeForTesting
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runBlockingTest
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.collection.IsEmptyCollection
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
    private lateinit var networkProducts: List<NetworkProduct>

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
        // Fake products
        val product1 = NetworkProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            1.0f
        )

        val product2 = NetworkProduct(
            2,
            "Product2 query",
            "Description product2",
            "Path image",
            "sku-product2",
            "upc-product2",
            2,
            2.0f)

        networkProducts = listOf(product1, product2)
    }

    @Test
    fun displayNavigationToDetail(){
        // GIVEN - a product
        val product1 = DomainProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            1.0f,
            false
        )

        // WHEN - calling displayNavigationToDetail
        repository = FakeTestRepository()
        sharedViewModel = SharedViewModel(repository)
        sharedViewModel.displayNavigationToDetail(product1)

        val display = sharedViewModel.navigationToDetail.getOrAwaitValue()

        // THEN - the product in the viewModel is the same as the one pass as a parameter
        assertThat(display.getContentIfNotHandled(), IsEqual(product1))
    }

    @Test
    fun callGetProducts_Success(){
        // Pause dispatcher so you can verify initial values
        mainCoroutineRule.pauseDispatcher()

        repository = FakeTestRepository()
        runBlocking { repository.saveProducts(networkProducts) }
        sharedViewModel = SharedViewModel(repository)
        sharedViewModel.refreshData()

        sharedViewModel.products.observeForTesting {
            // The operation has an network status LOADING
            assertThat(sharedViewModel.networkStatus.getOrAwaitValue(), `is`(ProductApiStatus.LOADING))
        }

        // Execute pending coroutines actions
        mainCoroutineRule.resumeDispatcher()

        // Then the products must be loaded from the repository
        val products = sharedViewModel.products.getOrAwaitValue()
        val productsError = sharedViewModel.productsError.getOrAwaitValue()
        val networkStatus = sharedViewModel.networkStatus.getOrAwaitValue()
        val localStatus = sharedViewModel.localStatus.getOrAwaitValue()

        // The products are loaded from the repository
        assertThat(domainToNetwork(products), IsEqual( networkProducts))
        // There is no error
        assertThat(productsError, `is`(nullValue()))
        // The operation has an network status DONE
        assertThat(networkStatus, `is`(ProductApiStatus.DONE))
        // The status from the local storage is SUCCESS
        assertThat(localStatus, `is`(LocalStatus.SUCCESS))
    }

    @Test
    fun callGetProducts_Failure(){
        // When - There is an error loading de data from the remote source
        repository = FakeTestRepository()
        runBlocking { repository.saveProducts(networkProducts) }
        repository.setReturnError(true)
        sharedViewModel = SharedViewModel(repository)
        sharedViewModel.refreshData()

        // Then the products are not loaded from the repository
        val products = sharedViewModel.products.getOrAwaitValue()
        val productError = sharedViewModel.productsError.getOrAwaitValue()
        val networkStatus = sharedViewModel.networkStatus.getOrAwaitValue()
        val localStatus = sharedViewModel.localStatus.getOrAwaitValue()

        // The products are not loaded
        assertThat(products, IsEqual(emptyList()))
        // Error message from the network
        assertThat(productError, IsEqual("The data couldn't be loaded"))
        // The Loading has finish
        assertThat(networkStatus, `is`(ProductApiStatus.DONE))
        // No data in the local storage send an error
        assertThat(localStatus, `is`(LocalStatus.ERROR))
    }

    @Test
    fun refresh_Success() = runBlockingTest{
        repository = FakeTestRepository()
        runBlocking { repository.saveProducts(networkProducts) }
        sharedViewModel = SharedViewModel(repository)
        sharedViewModel.refreshData()

        // Create a new product to update
        val productUpdated = DomainProduct(
            1,
            "Product1 updated",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            25,
            25.0f,
            false
        )
        // Update the product
        repository.updateProduct(productUpdated)
        // Refresh the data
        sharedViewModel.refreshData()

        val products = sharedViewModel.products.getOrAwaitValue()

        // THEN - there are not changes in the list
        assertThat(domainToNetwork(products), IsEqual(networkProducts))
    }

    @Test
    fun refresh_Failure() = runBlockingTest{
        repository = FakeTestRepository()
        runBlocking { repository.saveProducts(networkProducts) }
        sharedViewModel = SharedViewModel(repository)
        sharedViewModel.refreshData()

        // Create a new product to update
        val productUpdated = DomainProduct(
            1,
            "Product1 updated",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            25,
            25.0f,
            false
        )

        // The result must be
        val resultProduct = DomainProduct(
            1,
            "Product1 updated",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            25,
            25.0f,
            false
        )

        // Update the product
        repository.updateProduct(productUpdated)
        // Refresh the data
        // When - There is an error in the network
        repository.setReturnError(true)
        sharedViewModel.refreshData()

        val products = sharedViewModel.products.getOrAwaitValue()
        val productError = sharedViewModel.productsError.getOrAwaitValue()

        // THEN - there are not changes in the product
        assertThat(products.first(), IsEqual(resultProduct))
        // Error message from the network
        assertThat(productError, IsEqual("The data couldn't be loaded"))
    }

    @Test
    fun queryProducts_nullOrEmptyQueryListProducts_allProducts(){
        repository = FakeTestRepository()
        runBlocking { repository.saveProducts(networkProducts) }
        sharedViewModel = SharedViewModel(repository)
        sharedViewModel.refreshData()

        sharedViewModel.products.observeForTesting {
            // WHEN - the query is null or empty
            val query = ""
            sharedViewModel.queryProducts(query)
            val filteredProducts = sharedViewModel.productsFiltered.getOrAwaitValue()
            // THEN - all the products are presented in the list
            assertThat(domainToNetwork(filteredProducts), IsEqual(networkProducts))
        }
    }

    @Test
    fun queryProducts_queryListProducts_productsFiltered(){
        repository = FakeTestRepository()
        runBlocking { repository.saveProducts(networkProducts) }
        sharedViewModel = SharedViewModel(repository)
        sharedViewModel.refreshData()

        sharedViewModel.products.observeForTesting{
            // WHEN - there is a valid string query
            val query = "query"
            sharedViewModel.queryProducts(query)

            val filteredProducts = sharedViewModel.productsFiltered.getOrAwaitValue()

            // THEN - the list just contain one product
            assertThat(domainToNetwork(filteredProducts), IsEqual(listOf(networkProducts[1])))
        }
    }

    @Test
    fun queryProducts_queryNoProducts_null() {
        // WHEN - there are not products and a valid string query
        repository = FakeTestRepository()
        sharedViewModel = SharedViewModel(repository)
        sharedViewModel.refreshData()

        sharedViewModel.products.observeForTesting{
            val query = "query"
            sharedViewModel.queryProducts(query)

            val filteredProducts = sharedViewModel.productsFiltered.getOrAwaitValue()

            // THEN - the filtered products are null
            assertThat(filteredProducts, IsEmptyCollection())
        }
    }

    @Test
    fun queryProducts_nullOrEmptyQueryNoProducts_null(){
        // WHEN - there are not products and not a valid query
        repository = FakeTestRepository()
        sharedViewModel = SharedViewModel(repository)
        sharedViewModel.refreshData()

        sharedViewModel.products.observeForTesting {
            val query = null
            sharedViewModel.queryProducts(query)

            val filteredProducts = sharedViewModel.productsFiltered.getOrAwaitValue()

            // THEN - the filtered products are null
            assertThat(filteredProducts, IsEmptyCollection())
        }
    }

    @Test
    fun noProductsInDatabase(){
        // WHEN - there are not products in the database
        repository = FakeTestRepository()
        sharedViewModel = SharedViewModel(repository)
        sharedViewModel.refreshData()

        sharedViewModel.products.observeForTesting {
            val localStatus = sharedViewModel.localStatus.getOrAwaitValue()

            // THEN - The status must be an error
            assertThat(localStatus, IsEqual(LocalStatus.ERROR))
        }
    }
}