package com.example.productscanner.view

import android.content.Context
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.espresso.Espresso.onView
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.example.productscanner.R
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.launchFragmentInHiltContainer
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest // integration test
@RunWith(AndroidJUnit4::class)
//@UninstallModules(ProductsRepositoryModule::class)
@HiltAndroidTest
class DetailNetworkProductFragmentTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun initRepository(){
        hiltRule.inject()
    }

    @Test
    fun displayCorrectDetail(){
        // GIVEN - A product
        val product1 = DomainProduct(
            0,
            "Product1",
            "Description product1",
            "https://raw.githubusercontent.com/aldpdz/productScannerData/master/mouse.jpg",
            "sku-product1",
            "upc-product1",
            1,
            1.0f,
            false)

        // WHEN - Details fragment launched to display product
        val bundle = DetailProductFragmentArgs(product1).toBundle()
        launchFragmentInHiltContainer<DetailProductFragment>(bundle, R.style.AppTheme)

        val context = getApplicationContext<Context>()
        val upc = context.getString(R.string.upc) + product1.upc
        val sku = context.getString(R.string.sku) + product1.sku

        // THEN - details are displayed on the screen
        onView(withId(R.id.tv_name_detail)).check(matches(withText(product1.name)))
        onView(withId(R.id.tv_description_detail)).check(matches(withText(product1.description)))
        onView(withId(R.id.tv_upc_detail)).check(matches(withText(upc)))
        onView(withId(R.id.tv_sku_detail)).check(matches(withText(sku)))
        onView(withId(R.id.et_quantity)).check(matches(withText("1")))
        onView(withId(R.id.et_price)).check(matches(withText("1.0")))
        // TODO check image
    }
}