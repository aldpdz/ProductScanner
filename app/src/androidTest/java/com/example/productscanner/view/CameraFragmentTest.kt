package com.example.productscanner.view

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.productscanner.di.ProductsRepositoryModule
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
    fun someTest(){}
}