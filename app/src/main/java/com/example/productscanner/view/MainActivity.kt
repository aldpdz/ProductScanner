package com.example.productscanner.view

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
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
    private lateinit var sharedPreferences: SharedPreferences
    private val mainViewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(
            this, R.layout.activity_main)

        // nav manages the up button
        navController = this.findNavController(R.id.navHostFragment)
        NavigationUI.setupActionBarWithNavController(this, navController)

        readSettingPreferences()
        setSettingsListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    private fun readSettingPreferences() {
        Log.d("MainFragment", "reading settings")
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        mainViewModel.runWorker(sharedPreferences, false)
    }

    private fun setSettingsListeners() {
        // Use the MainActivity to listen to changes in the settings
        // The fragments are killed when moving between them using navigation
        // causing the listeners never be called
        val manageSettings = ManageSettings(sharedPreferences){
            mainViewModel.runWorker(it, true)
        }
        lifecycle.addObserver(manageSettings)
    }
}
