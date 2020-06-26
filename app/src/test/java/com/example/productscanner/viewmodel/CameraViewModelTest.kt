package com.example.productscanner.viewmodel

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.productscanner.getOrAwaitValue
import com.example.productscanner.model.Product
import org.hamcrest.CoreMatchers.`is`

//@RunWith(AndroidJUnit4::class) needed when using context
class CameraViewModelTest{

    // Subject under test
    private lateinit var cameraViewModel: CameraViewModel

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        cameraViewModel = CameraViewModel()
    }

    @Test
    fun atStart_buttonsAreVisible(){
        // Then the buttons are visible
        val value = cameraViewModel.btnVisibility.getOrAwaitValue()
        assertThat(value, `is`(View.VISIBLE))
    }

    @Test
    fun getProduct_upc_productFound(){
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
        val listProducts = MutableLiveData<List<Product>>()
        listProducts.value = listOf(product1, product2)

        cameraViewModel.setProducts(listProducts)

    }
}