package com.example.productscanner.view

import android.os.Bundle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.productscanner.R
import com.example.productscanner.di.ProductsRepositoryModule
import com.example.productscanner.launchFragmentInHiltContainer
import com.example.productscanner.repositories.FakeTestRepository
import com.example.productscanner.repositories.IProductsRepository
import dagger.hilt.android.testing.BindValue
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(ProductsRepositoryModule::class)
@HiltAndroidTest
class CameraFragmentTest{

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @BindValue
    @JvmField// avoid issues with Hilt
    val repository: IProductsRepository = FakeTestRepository()

    @Before
    fun initRepository(){
        hiltRule.inject()
    }

    @Test
    fun openCameraFragment(){
        // WHEN - open the fragment
        launchFragmentInHiltContainer<CameraFragment>(Bundle(), R.style.AppTheme)

        // THEN - the buttons must be visible
        onView(withId(R.id.btn_scan_sku)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_scan_upc)).check(matches(isDisplayed()))
    }
}