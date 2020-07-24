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
import com.example.productscanner.clearSharedPrefs
import com.example.productscanner.di.ProductsRepositoryModule
import com.example.productscanner.data.network.NetworkProduct
import com.example.productscanner.repositories.FakeTestRepository
import com.example.productscanner.repositories.IProductsRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest // End-to-end test
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@UninstallModules(ProductsRepositoryModule::class) // Ignore production module
@HiltAndroidTest
class MainActivityTest{
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField
    val repository: IProductsRepository = FakeTestRepository()

    @Before
    fun initRepository(){
        hiltRule.inject()
    }
    // TODO - Add idle
    @Test
    fun editPriceQuantity(){
        val product1 = NetworkProduct(
            1,
            "Product1",
            "Description product1",
            "https://raw.githubusercontent.com/aldpdz/productScannerData/master/mouse.jpg",
            "sku-product1",
            "upc-product1",
            1,
            1.0f)

        // Set initial state
        // The initial state must be set before calling launch
        runBlocking {
            repository.saveProducts(listOf(product1))
        }

        // Todo use better approach with resources
        val prefixPrice = "Price: $"
        val prefixQuantity = "Quantity: "

        // Start up Products screen.
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            // Clear preferences
            clearSharedPrefs(activity)
        }

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