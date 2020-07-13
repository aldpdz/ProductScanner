package com.example.productscanner.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.productscanner.getOrAwaitValue
import com.example.productscanner.data.network.NetworkProduct
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.core.IsEqual
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class) // to get the context
class DetailNetworkProductViewModelTest{

    private lateinit var detailProductViewModel: DetailProductViewModel

    // To deal with live data
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        detailProductViewModel = DetailProductViewModel(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun setDetailProduct(){
        val product1 = NetworkProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            25.89f,
            false
        )

        detailProductViewModel.setDetailProduct(product1)

        val productVM = detailProductViewModel.detailNetworkProduct.getOrAwaitValue()
        val price = detailProductViewModel.priceString.getOrAwaitValue()
        val quantity = detailProductViewModel.quantityString.getOrAwaitValue()

        // The products is set in the view model
        assertThat(productVM, IsEqual(product1))
        // The price and quantity are converted to String
        assertThat(quantity, `is`("1"))
        assertThat(price, `is`("25.89"))
    }

}