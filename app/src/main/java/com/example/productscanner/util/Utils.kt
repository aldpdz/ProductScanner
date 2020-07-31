package com.example.productscanner.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.productscanner.R
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.view.MainActivity

private const val NOTIFICATION_ID = 0
const val IS_FIRST_INSTALLATION = "FIRST_INSTALLATION"
const val SHARE_FILE = "ShredFile"

/***
 * Write on the preferences the product's id
 */
fun writeOnPreferences(context: Context, id: Int){
    val sharedPref = context.getSharedPreferences(SHARE_FILE, Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()){
        putInt(id.toString(), id)
        commit()
    }
}

/***
 * Write on preferences the first time the data is loaded
 */
fun writeFirstLoad(context: Context){
    Log.i("Utils", "Preference first data loaded")
    val sharedPref = context.getSharedPreferences(SHARE_FILE, Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()){
        putBoolean(IS_FIRST_INSTALLATION, false)
        commit()
    }
}

/***
 * Check if the data has been loaded previously
 */
fun readFirstLoad(context: Context) : Boolean{
    val sharedPref = context.getSharedPreferences(SHARE_FILE, Context.MODE_PRIVATE)
    return sharedPref.getBoolean(IS_FIRST_INSTALLATION, true)
}

/***
 * Get all the products' keys from the preferences
 */
fun getAllKeys(context: Context): MutableSet<String> {
    return context.getSharedPreferences(SHARE_FILE, Context.MODE_PRIVATE).all.keys
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

fun NotificationManager.sendSimpleNotification(applicationContext: Context){
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.product_notification_channel_id))
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle("background work")
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    notify(NOTIFICATION_ID, builder.build())
}