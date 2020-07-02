package com.example.productscanner.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.example.productscanner.R
import com.example.productscanner.ScannerProductApplication
import com.example.productscanner.databinding.ActivityMainBinding
import com.example.productscanner.viewmodel.MainActivityViewModel
import com.example.productscanner.viewmodel.MainActivityViewModelFactory

class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory((application as ScannerProductApplication).productsRepository)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this, R.layout.activity_main)

        // nav manages the up button
        navController = this.findNavController(R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        viewModel.loadPreference.observe(this, Observer {
            it?.let {
                if(it){
                    viewModel.setSavedIds(this)
                }
            }
        })
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}
