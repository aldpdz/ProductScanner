package com.example.productscanner.data.database

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.MediumTest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.runner.RunWith

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@MediumTest // Integration test
class ProductLocalSourceTest{

    // Executes each task synchronously using AArchitecture Components
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var localSource: ProductLocalSource
    private lateinit var database: ProductsDatabase

    @Before
    fun setup(){
        // It doesn't survive killing the process
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ProductsDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()

        localSource = ProductLocalSource(
            database.productDao,
            Dispatchers.Main
        )
    }

    @After
    fun cleanUp(){
        database.close()
    }


}