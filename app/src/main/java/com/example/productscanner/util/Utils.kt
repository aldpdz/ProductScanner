package com.example.productscanner.util

import android.app.Activity
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.productscanner.R
import com.example.productscanner.model.Product
import com.example.productscanner.view.MainActivity

private val NOTIFICATION_ID = 0

fun writeOnPreferences(activity: Activity, id: Int){
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE) ?: return
    with(sharedPref.edit()){
        putInt(id.toString(), id)
        commit()
    }
}

fun readOnPreferences(activity: MainActivity, id: Int): Int{
    val sharedPref = activity.getPreferences(Context.MODE_PRIVATE)
    val defaultValue = -1
    return sharedPref.getInt(id.toString(), defaultValue)
}

/***
 * Extension function to send notifications
 */
fun NotificationManager.sendNotification(
    messageBody: String,
    expandedMessage: String,
    applicationContext: Context,
    product: Product)
{
    val bundle = Bundle()
    bundle.putParcelable("argProduct", product)

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