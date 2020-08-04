package com.example.productscanner.util

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.navigation.NavDeepLinkBuilder
import com.example.productscanner.R
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.receiver.DataChangedReceiver
import com.example.productscanner.receiver.IS_CANCEL
import com.example.productscanner.receiver.PRODUCT_ID
import com.example.productscanner.view.MainActivity

const val ID_NOTIFICATION_PRODUCT_UPDATED = 0
const val ID_NOTIFICATION_SYNC = 1
const val IS_FIRST_INSTALLATION = "FIRST_INSTALLATION"
const val SHARE_FILE = "SharedFile"
const val REQUEST_CODE_ACCEPT = 0
const val REQUEST_CODE_CANCEL =  1


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
    updateProduct: DomainProduct)
{
    val bundle = Bundle()
    bundle.putParcelable("argProduct", updateProduct)

    // Action when clicking the notification
    val pendingIntent = NavDeepLinkBuilder(applicationContext)
        .setGraph(R.navigation.navigation)
        .setDestination(R.id.detailProduct)
        .setArguments(bundle)
        .createPendingIntent()

    // Action when clicking the notification buttons
    val dataChangeIntentAccept = Intent(applicationContext, DataChangedReceiver::class.java).apply {
        putExtra(IS_CANCEL, false)
    }
    val acceptPendingIntent = PendingIntent.getBroadcast(
        applicationContext, REQUEST_CODE_ACCEPT, dataChangeIntentAccept, 0)

    val dataChangeIntentCancel = Intent(applicationContext, DataChangedReceiver::class.java).apply {
        putExtra(IS_CANCEL, true)
        putExtra(PRODUCT_ID, updateProduct.id)
    }
    val cancelPendingIntent = PendingIntent.getBroadcast(
        applicationContext, REQUEST_CODE_CANCEL, dataChangeIntentCancel, 0)

    val soundUri = getSoundResource(R.raw.flick, applicationContext)

    // Create notification
    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.product_notification_channel_id))
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(applicationContext.getString(R.string.notification_update_title))
        .setContentText(messageBody)
        .setStyle(NotificationCompat.BigTextStyle().bigText(expandedMessage))
        .setSound(soundUri)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true) // Remove notification after is activated
        .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Support devices running API 25 or lower
        .addAction(0,
            applicationContext.getString(R.string.accept), acceptPendingIntent)
        .addAction(0,
            applicationContext.getString(R.string.cancel), cancelPendingIntent)

    // Send the notification
    notify(ID_NOTIFICATION_PRODUCT_UPDATED, builder.build())
}

fun NotificationManager.sendSimpleNotification(applicationContext: Context){
    val contentIntent = Intent(applicationContext, MainActivity::class.java)
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        ID_NOTIFICATION_SYNC,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val uriSound = getSoundResource(R.raw.pipes, applicationContext)

    val builder = NotificationCompat.Builder(
        applicationContext,
        applicationContext.getString(R.string.product_notification_channel_id))
        .setSmallIcon(R.mipmap.ic_launcher)
        .setContentTitle(applicationContext.getString(R.string.notification_sync_title))
        .setSound(uriSound)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
    notify(ID_NOTIFICATION_SYNC, builder.build())
}

/***
 * Get the ringtone form the android resources
 */
fun getSoundResource(resourceId: Int, context: Context): Uri{
    return Uri.parse("android.resource://"
        .plus(context.packageName)
        .plus("/")
        .plus(resourceId))
}