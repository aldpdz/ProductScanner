package com.example.productscanner.view

import android.content.Context
import android.util.Log
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.testing.WorkManagerTestInitHelper
import com.example.productscanner.DataBindingIdlingResource
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

    private lateinit var context: Context

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    // An idling resource that waits for Data Binding to have no pending bindings.
    private val dataBindingIdlingResource = DataBindingIdlingResource()

    @BindValue
    @JvmField
    val repository: IProductsRepository = FakeTestRepository()

    @Before
    fun initRepository(){
        hiltRule.inject()
    }

    @Before
    fun deletePreferences(){
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        clearSharedPrefs(context)
    }

    @Before
    fun setup(){
        context = ApplicationProvider.getApplicationContext()
        val config = Configuration.Builder()
            // Set log level to Log.DEBUG to make it easier to debug
            .setMinimumLoggingLevel(Log.DEBUG)
            .build()

        // Initialize WorkManager for instrumentation test.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }

//    /***
//     * Idling resources tell Espresso that the app is idle or busy. This is needed when operations
//     * are not scheduled in the main Looper (for example when executed on a different thread).
//     */
//    @Before
//    fun registerIdlingResource(){
//        IdlingRegistry.getInstance().register(dataBindingIdlingResource)
//    }
//
//    /***
//     * Unregister your Idling Resource so it can be garbage collected and does not leak any memory.
//     */
//    @After
//    fun unregisterIdlingResource(){
//        IdlingRegistry.getInstance().unregister(dataBindingIdlingResource)
//    }
//
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

    @Test
    fun keepSearch(){
        val product1 = NetworkProduct(
            1,
            "Product1",
            "Description product1",
            "https://raw.githubusercontent.com/aldpdz/productScannerData/master/mouse.jpg",
            "sku-product1",
            "upc-product1",
            1,
            1.0f)

        val product2 = NetworkProduct(
            2,
            "Product2",
            "Description product2",
            "https://raw.githubusercontent.com/aldpdz/productScannerData/master/mouse.jpg",
            "sku-product2",
            "upc-product2",
            2,
            2.0f)

        // Set initial state
        // The initial state must be set before calling launch
        runBlocking {
            repository.saveProducts(listOf(product1, product2))
        }

        // Start up Products screen.
        val activityScenario = ActivityScenario.launch(MainActivity::class.java)

        // WHEN - Search a product and enter in the product's detail view
        onView(withId(R.id.action_search)).perform(click())
        onView(withId(androidx.appcompat.R.id.search_src_text))
            .perform(replaceText("roduct1"))
        onView(withText("Product1")).perform(click())

        // TODO - Add extension function to get the content of the up button instead of the
        // TODO - back button, do this add a toolbar
        // And going back
        pressBack()

        // THEN - the search is not refresh
        // The product1 is displayed
        onView(withText("Product1")).check(matches(isDisplayed()))
        // The product2 is not displayed because of the search
        onView(withText("Product2")).check(doesNotExist())

        // Important when you're working with a database
        activityScenario.close()
    }
}