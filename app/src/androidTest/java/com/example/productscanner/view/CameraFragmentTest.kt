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
import com.example.productscanner.data.network.FakeAndroidTestRepository
import com.example.productscanner.repositories.IProductsRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import javax.inject.Inject
import javax.inject.Singleton

@MediumTest
@RunWith(AndroidJUnit4::class)
@UninstallModules(ProductsRepositoryModule::class)
@HiltAndroidTest
class CameraFragmentTest{

    @Inject
    lateinit var repository: IProductsRepository

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

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

    // Just for this class
    @Module
    @InstallIn(ApplicationComponent::class)
    abstract class ProductsRepositoryTestModule{
        @Singleton
        @Binds
        abstract fun bindProductsRepository(productsRepository: FakeAndroidTestRepository): IProductsRepository
    }
}