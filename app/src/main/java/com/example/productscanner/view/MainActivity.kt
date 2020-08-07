package com.example.productscanner.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import androidx.preference.PreferenceManager
import com.example.productscanner.R
import com.example.productscanner.databinding.ActivityMainBinding
import com.example.productscanner.util.ManageSettings
import com.example.productscanner.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        // Set theme
        setTheme(R.style.AppTheme)
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        // nav manages the up button
        navController = this.findNavController(R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        setSettingsListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    private fun setSettingsListeners() {
        // Use the MainActivity to listen to changes in the settings
        // The fragments are killed when moving between them using navigation
        // causing the listeners never be called
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val manageSettings = ManageSettings(sharedPreferences){
            mainViewModel.runWorker(it)
        }
        lifecycle.addObserver(manageSettings)
    }
}
