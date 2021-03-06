package com.example.productscanner.viewmodel

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.productscanner.getOrAwaitValue
import com.example.productscanner.repositories.FakeTestRepository
import org.hamcrest.CoreMatchers.`is`

class CameraViewModelTest{

    private lateinit var repository: FakeTestRepository
    // Subject under test
    private lateinit var cameraViewModel: CameraViewModel

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        repository = FakeTestRepository()
        cameraViewModel = CameraViewModel(repository)
    }

    @Test
    fun atStart_buttonsAreVisible(){
        // Then the buttons are visible
        val value = cameraViewModel.btnVisibility.getOrAwaitValue()
        assertThat(value, `is`(View.VISIBLE))
    }

    @Test
    fun getProduct_upc_productFound(){
        // TODO - Implement test
//        // GIVEN - a set of products
//        val product1 = NetworkProduct(
//            1,
//            "Product1",
//            "Description product1",
//            "Path image",
//            "sku-product1",
//            "054585412659",
//            1,
//            1.0f,
//            false
//        )
//
//        val product2 = NetworkProduct(
//            2,
//            "Product2",
//            "Description product2",
//            "Path image",
//            "sku-product2",
//            "upc-product2",
//            2,
//            2.0f,
//            true
//        )
//        val listProducts = MutableLiveData<List<NetworkProduct>>()
//        listProducts.value = listOf(product1, product2)
//
//        cameraViewModel.setProducts(listProducts)
//
//        // WHEN - scanning an image
//        // TODO not working
//        val image = CameraViewModelTest::class.java.getResource("/fish-bike.jpg")!!.readBytes()
//        cameraViewModel.typeScanner = TypeScanner.UPC
//        cameraViewModel.processInputImage(image)
//
//        val product = cameraViewModel.productByBarCode
//        val statusScannerStatusItem = cameraViewModel.scannerStatusItem.getOrAwaitValue()
//
//        // THEN - the product is product1
//        assertThat(product, IsEqual(product1))
//        // The status is FOUND
//        assertThat(statusScannerStatusItem.getContentIfNotHandled(), `is`(ScannerStatusItem.FOUND))
    }

    @Test
    fun getProduct_upc_productNotFound(){
        // TODO - Implement test
    }

    @Test
    fun getProduct_sku_productFound(){
        // TODO - Implement test
    }

    @Test
    fun getProduct_sku_productNotFound(){
        // TODO - Implement test
    }

    @Test
    fun processInputImage_upc_tryAgain(){
        // TODO - Implement test
    }

    @Test
    fun processInputImage_sku_tryAgain(){
        // TODO - Implement test
    }
}