package com.example.productscanner.view

import androidx.fragment.app.testing.launchFragment
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.core.app.ActivityScenario
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import com.example.productscanner.R
import com.example.productscanner.ServiceLocator
import com.example.productscanner.model.FakeAndroidTestRepository
import com.example.productscanner.model.Product
import com.example.productscanner.viewmodel.MainActivityViewModel
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@MediumTest // integration test
@RunWith(AndroidJUnit4::class)
class DetailProductFragmentTest{

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
}