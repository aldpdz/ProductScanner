package com.example.productscanner.view

import android.content.Context
import android.os.Bundle
import androidx.appcompat.view.menu.ActionMenuItem
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ActivityScenario.launch
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.productscanner.*
import com.example.productscanner.data.domain.DomainProduct
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
import org.mockito.Mockito.*

@MediumTest
@RunWith(AndroidJUnit4::class)
@ExperimentalCoroutinesApi
@UninstallModules(ProductsRepositoryModule::class)
@HiltAndroidTest
class MainFragmentTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField// avoid issues with Hilt
    val repository: IProductsRepository = FakeTestRepository()

    @Before
    fun initRepository(){
        hiltRule.inject()
    }

    // TODO add an extension function for runBlocking
    @Test
    fun displayProduct_whenRepositoryHasData(){
        val networkProduct1 = NetworkProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            1.0f)

        runBlocking {
            repository.saveProducts(listOf(networkProduct1))
        }

        // WHEN - On startup
        launchActivity()

        // THEN - Verify product is displayed on screen
        onView(withText("Product1")).check(matches(isDisplayed()))
    }

    @Test
    fun displayError_whenRepositoryHasNoData(){
        // WHEN - On startup
        launchActivity()

        // THEN - Verify that the error image is load
        onView(withId(R.id.statusProducts)).check(matches(isDisplayed()))
    }

    @Test
    fun displayFilterProducts(){
        // GIVEN - two products
        val networkProduct1 = NetworkProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            1.0f)

        val networkProduct2 = NetworkProduct(
            2,
            "Product2",
            "Description product2",
            "Path image",
            "sku-product2",
            "upc-product2",
            2,
            2.0f)

        runBlocking {
            repository.saveProducts(listOf(networkProduct1, networkProduct2))
        }

        // WHEN - On startup
        launchActivity()

        onView(withId(R.id.action_search)).perform(click())
        onView(withId(androidx.appcompat.R.id.search_src_text)).perform(replaceText("roduct1"))

        // THEN - Verify product1 is displayed on screen and product2 not
        onView(withText("Product1")).check(matches(isDisplayed()))
        onView(withText("Product2")).check(doesNotExist())
    }

    @Test
    fun clickProduct_navigateToDetailFragment(){
        val networkProduct1 = NetworkProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            1.0f)

        val networkProduct2 = NetworkProduct(
            2,
            "Product2",
            "Description product2",
            "Path image",
            "sku-product2",
            "upc-product2",
            2,
            2.0f)

        val product = DomainProduct(
            1,
            "Product1",
            "Description product1",
            "Path image",
            "sku-product1",
            "upc-product1",
            1,
            1.0f,
            false)

        runBlocking {
            repository.saveProducts(listOf(networkProduct1, networkProduct2))
        }

        // GIVEN - On the home screen
        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<MainFragment>(Bundle(), R.style.AppTheme){
            Navigation.setViewNavController(this.view!!, navController)
        }

        // WHEN - Click on one item
        onView(withText("Product1")).perform(click())

        // THEN - Verify that we navigate to the detail screen
        verify(navController).navigate(
            MainFragmentDirections.actionMainFragmentToDetailProduct(product)
        )
    }

    @Test
    fun clickScanner_navigateToCameraFragment(){
        // GIVEN - On the home screen
        val context: Context = getApplicationContext<Context>()
        val addMenuItem = ActionMenuItem(context, 0, R.id.scan, 0, 0, null)

        val navController = mock(NavController::class.java)
        launchFragmentInHiltContainer<MainFragment>(Bundle(), R.style.AppTheme){
            Navigation.setViewNavController(this.view!!, navController)
            onOptionsItemSelected(addMenuItem)
        }

        // WHEN - Click on the menu scan
        openActionBarOverflowOrOptionsMenu(getApplicationContext())
        onView(withText(R.string.scan)).perform(click())

        // THEN - Verify that we navigate to the camera screen
        verify(navController, times(2)).navigate(
            MainFragmentDirections.actionMainFragmentToCamerax()
        )
    }

    private fun launchActivity(): ActivityScenario<MainActivity>?{
        val activityScenario = launch(MainActivity::class.java)
        activityScenario.onActivity { activity ->
            // Disable animation in RecyclerView
            (activity.findViewById(R.id.rv_products) as RecyclerView).itemAnimator = null
            // Clear all preferences
            clearSharedPrefs(activity)
        }
        return activityScenario
    }
}