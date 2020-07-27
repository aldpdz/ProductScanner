package com.example.productscanner.util

import android.app.Activity
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.productscanner.R
import com.example.productscanner.data.domain.DomainProduct

private val NOTIFICATION_ID = 0
const val IS_FIRST_INSTALLATION = "FIRST_INSTALLATION"
const val SHARE_FILE = "ShredFile"

/***
 * Write on the preferences the product's id
 */
fun writeOnPreferences(activity: Activity, id: Int){
    val sharedPref = activity.getSharedPreferences(SHARE_FILE, Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()){
        putInt(id.toString(), id)
        commit()
    }
}

/***
 * Write on preferences the first time the data is loaded
 */
fun writeFirstLoad(activity: Activity){
    Log.i("Utils", "Preference first data loaded")
    val sharedPref = activity.getSharedPreferences(SHARE_FILE, Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()){
        putBoolean(IS_FIRST_INSTALLATION, false)
        commit()
    }
}

/***
 * Check if the data has been loaded previously
 */
fun readFirstLoad(activity: Activity) : Boolean{
    val sharedPref = activity.getSharedPreferences(SHARE_FILE, Context.MODE_PRIVATE)
    return sharedPref.getBoolean(IS_FIRST_INSTALLATION, true)
}

/***
 * Get all the products' keys from the preferences
 */
fun getAllKeys(activity: Activity): MutableSet<String> {
    return activity.getSharedPreferences(SHARE_FILE, Context.MODE_PRIVATE).all.keys
}

/***
 * Extension function to send notifications
 */
fun NotificationManager.sendNotification(
    messageBody: String,
    expandedMessage: String,
    applicationContext: Context,
    domainProduct: DomainProduct
)
{
    val bundle = Bundle()
    bundle.putParcelable("argProduct", domainProduct)

    val pendingIntent = NavDeepLinkBuilder(applicationContext)
        .setGraph(R.navigation.navigation)
        .setDestination(R.id.detailProduct)
        .setArguments(bundle)
        .createPendingIntent()

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.product_notification_channel_id))
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setStyle(NotificationCompat.BigTextStyle().bigText(expandedMessage))
        .setContentIntent(pendingIntent)
        .setAutoCancel(true) // Remove notification after is activated
        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Support devices running API 25 or lower

    // Send the notification
    notify(NOTIFICATION_ID, builder.build())
}