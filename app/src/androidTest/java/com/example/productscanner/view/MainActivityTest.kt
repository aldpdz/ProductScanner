package com.example.productscanner.view

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.productscanner.R
import com.example.productscanner.ServiceLocator
import com.example.productscanner.model.FakeAndroidTestRepository
import com.example.productscanner.model.Product
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.Thread.sleep

@LargeTest // End-to-end test
@RunWith(AndroidJUnit4::class)
class MainActivityTest{
    private lateinit var repository: FakeAndroidTestRepository

    @Before
    fun initRepository(){
        repository = FakeAndroidTestRepository()
        ServiceLocator.productRepository = repository
    }

    @After
    fun cleanUp(){
        // It needs to be reset, because there might be the possibility that the ServiceLocator
        // is share between tests.
        ServiceLocator.resetRepository()
    }

    @Test
    fun editPriceQuantity(){
        val product1 = Product(
            1,
            "Product1",
            "Description product1",
            "https://raw.githubusercontent.com/aldpdz/productScannerData/master/mouse.jpg",
            "sku-product1",
            "upc-product1",
            1,
            1.0f,
            false)

        // Set initial state
        // The initial state must be set before calling launch
        repository.addProducts(product1)

        // Todo use better approach with resources
        val prefixPrice = "Price: $"
        val prefixQuantity = "Quantity: "

        // Start up Products screen.
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        // Click on the product on the list and verify that all the data is correct.
        onView(withText("Product1")).perform(click())
        onView(withId(R.id.tv_name_detail)).check(matches(withText("Product1")))
        onView(withId(R.id.tv_description_detail)).check(matches(withText("Description product1")))

        // Edit and save
        onView(withId(R.id.et_price)).perform(replaceText("2.0"))
        onView(withId(R.id.et_quantity)).perform(replaceText("2"))
        onView(withId(R.id.btn_update)).perform(click())

        // Verify product is displayed on screen in the product list
        onView(withText(prefixPrice.plus("2.0"))).check(matches(isDisplayed()))
        onView(withText(prefixQuantity.plus("2"))).check(matches(isDisplayed()))
        // Verify previous quantity and price is not displayed
        onView(withText(prefixPrice.plus("1.0"))).check(doesNotExist())
        onView(withText(prefixQuantity.plus("1"))).check(doesNotExist())

        // Important when you're working with a database
        activityScenario.close()
    }
}