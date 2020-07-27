package com.example.productscanner

import android.content.Context
import com.example.productscanner.util.SHARE_FILE

fun clearSharedPrefs(context: Context){
    val pref = context.getSharedPreferences(SHARE_FILE, Context.MODE_PRIVATE)
    pref.edit().clear().commit()
}