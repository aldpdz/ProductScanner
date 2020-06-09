package com.example.productscanner.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.productscanner.R
import com.example.productscanner.view.MainActivity

private val NOTIFICATION_ID = 0

fun writeOnPrefereces(activity: MainActivity, id: Int){
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
    applicationContext: Context)
{

    // Intent to launch an activity
    val contentIntent = Intent(applicationContext, MainActivity::class.java)

    // Grant rights to another application
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

//    val bundle = bundleOf(
//        Pair("argProduct", product!!)
//    )

//    val bundle = Bundle()
//    bundle.putParcelable("argProduct", product)
//
//    val contentPendingIntent = NavDeepLinkBuilder(applicationContext)
////        .setComponentName(MainActivity::class.java)
//
//        .setGraph(R.navigation.navigation)
//        .setDestination(R.id.detailProduct)
//        .setArguments(bundle)
//        .createPendingIntent()


    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.product_notification_channel_id))
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(applicationContext.getString(R.string.notification_title))
        .setContentText(messageBody)
        .setStyle(NotificationCompat.BigTextStyle().bigText(expandedMessage))
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true) // Remove notification after is activated
        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Support devices running API 25 or lower

    // Send the notification
    notify(NOTIFICATION_ID, builder.build())
}