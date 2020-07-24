package com.example.productscanner

import android.app.Activity
import android.content.Context

fun clearSharedPrefs(activity: Activity){
    val pref = activity.getPreferences(Context.MODE_PRIVATE)
    pref.edit().clear().commit()
}