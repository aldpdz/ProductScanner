package com.example.productscanner.util

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent

class ManageSettings(
    private val sharedPreferences: SharedPreferences,
    private val callback: (SharedPreferences) -> Unit
)
    : LifecycleObserver{

    private val listener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, s -> 
            callback(sharedPreferences)
        }
    
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun registerSharedPref(){
        Log.d("ManageSettings", "register listener")
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun unregisterSharedPref(){
        Log.d("ManageSettings", "unregister listener")
        sharedPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}