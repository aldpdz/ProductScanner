package com.example.productscanner.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.productscanner.getOrAwaitValue
import com.example.productscanner.model.Product
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

        // WHEN - to change to a new fragment
        mainFragmentViewModel.displayNavigationToDetail(product1)
        val productToDisplay = mainFragmentViewModel.navigationToDetail.getOrAwaitValue()

        // THEN - the product is equal to product1
        assertThat(productToDisplay.getContentIfNotHandled(), IsEqual(product1))
    }
}