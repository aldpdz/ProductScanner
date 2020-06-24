package com.example.productscanner.viewmodel

import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import com.example.productscanner.getOrAwaitValue
import org.hamcrest.CoreMatchers.`is`

//@RunWith(AndroidJUnit4::class) needed when using context
class CameraViewModelTest{

    // Subject under test
    private lateinit var cameraViewModel: CameraViewModel

    // Executes each task synchronously using Architecture Components.
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun setupViewModel(){
        cameraViewModel = CameraViewModel()
    }

    @Test
    fun atStart_buttonsAreVisible(){
        // Then the buttons are visible

        val value = cameraViewModel.btnVisibility.getOrAwaitValue()
        assertThat(value, `is`(View.VISIBLE))
    }
}