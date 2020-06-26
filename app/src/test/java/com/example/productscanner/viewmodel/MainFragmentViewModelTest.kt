package com.example.productscanner.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import com.example.productscanner.getOrAwaitValue
import com.example.productscanner.model.Product
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainFragmentViewModelTest{
    private lateinit var mainFragmentViewModel: MainFragmentViewModel

    // Executes each task synchronously using Architecture Components.
    // Runs all the Architecture Components-related background jobs in the same
    // thread so that the test results happen synchronously.
    // Use when dealing with live data
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        mainFragmentViewModel = MainFragmentViewModel()
    }

    @Test
    fun setResponseData_Success(){
        // Load a successful response
        val productsError = MutableLiveData<String?>()
        val status = MutableLiveData<ProductApiStatus>()
        val products = MutableLiveData<List<Product>>()
        productsError.value = null
        status.value = ProductApiStatus.DONE

        products.value = emptyList()
        // Setting the values
        mainFragmentViewModel.setResponseData(productsError, status, products)

        val productsErrorVM = mainFragmentViewModel.productsError?.getOrAwaitValue()
        val statusVM = mainFragmentViewModel.status?.getOrAwaitValue()

        // The error is null
        assertThat(productsErrorVM, `is`(productsError.value))

        // The status is DONE
        assertThat(statusVM, `is`(status.value))
    }

    @Test
    fun setResponseData_Loading(){
        val productsError = MutableLiveData<String?>()
        val status = MutableLiveData<ProductApiStatus>()
        val products = MutableLiveData<List<Product>>()
        productsError.value = null
        status.value = ProductApiStatus.LOADING

        products.value = emptyList()
        // Setting the values
        mainFragmentViewModel.setResponseData(productsError, status, products)

        val statusVM = mainFragmentViewModel.status?.getOrAwaitValue()

        // The status is DONE
        assertThat(statusVM, `is`(status.value))
    }

    @Test
    fun setResponseData_Failure(){
        val productsError = MutableLiveData<String?>()
        val status = MutableLiveData<ProductApiStatus>()
        val products = MutableLiveData<List<Product>>()
        productsError.value = "error connexion"
        status.value = ProductApiStatus.ERROR

        products.value = emptyList()
        // Setting the values
        mainFragmentViewModel.setResponseData(productsError, status, products)

        val productsErrorVM = mainFragmentViewModel.productsError?.getOrAwaitValue()
        val statusVM = mainFragmentViewModel.status?.getOrAwaitValue()

        // The error is "error connexion"
        assertThat(productsErrorVM, `is`(productsError.value))

        // The status is ERROR
        assertThat(statusVM, `is`(status.value))
    }

    @Test
    fun queryProducts_nullOrEmptyQuery_allProducts(){
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
        val listProducts = listOf(product1, product2)

        val productsError = MutableLiveData<String?>()
        val status = MutableLiveData<ProductApiStatus>()
        val products = MutableLiveData<List<Product>>()
        productsError.value = null
        status.value = ProductApiStatus.DONE
        products.value = listProducts

        // Setting the values
        mainFragmentViewModel.setResponseData(productsError, status, products)

        val query = ""
        mainFragmentViewModel.queryProducts(query)

        val filteredProducts = mainFragmentViewModel.productsFiltered.getOrAwaitValue()

        // All the products are presented in the list
        assertThat(filteredProducts, IsEqual(listProducts))
    }

    @Test
    fun queryProducts_query_productsFiltered(){
        val product1 = Product(
            1,
            "Product1_query",
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
        val listProducts = listOf(product1, product2)

        val productsError = MutableLiveData<String?>()
        val status = MutableLiveData<ProductApiStatus>()
        val products = MutableLiveData<List<Product>>()
        productsError.value = null
        status.value = ProductApiStatus.DONE
        products.value = listProducts

        // Setting the values
        mainFragmentViewModel.setResponseData(productsError, status, products)

        val query = "query"
        mainFragmentViewModel.queryProducts(query)

        val filteredProducts = mainFragmentViewModel.productsFiltered.getOrAwaitValue()

        // The list just contain one product
        assertThat(filteredProducts, IsEqual(listOf(product1)))
    }

    @Test
    fun queryProducts_nullProducts_null() {
        val query = "query_filter"
        mainFragmentViewModel.queryProducts(query)

        val filteredProducts = mainFragmentViewModel.productsFiltered.value

        // The filtered products are null
        assertThat(filteredProducts, `is`(nullValue()))
    }

    @Test
    fun queryProducts_nullOrEmptyQueryNoProducts_null(){
        val query = null
        mainFragmentViewModel.queryProducts(query)

        val filteredProducts = mainFragmentViewModel.productsFiltered.value

        // The filtered products are null
        assertThat(filteredProducts, `is`(nullValue()))
    }

    @Test
    fun displayNavigationToDetail_Product(){
        val product1 = Product(
            1,
            "Product1_query",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            1.0f,
            false)

        mainFragmentViewModel.displayNavigationToDetail(product1)
        val productToDisplay = mainFragmentViewModel.navigationToDetail.getOrAwaitValue()

        // The product is equal to product1
        assertThat(productToDisplay.getContentIfNotHandled(), IsEqual(product1))
    }
}