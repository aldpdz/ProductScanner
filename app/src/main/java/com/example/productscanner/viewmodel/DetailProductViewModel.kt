package com.example.productscanner.viewmodel

import android.app.Application
import android.app.NotificationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.example.productscanner.R
import com.example.productscanner.data.network.NetworkProduct
import com.example.productscanner.util.sendNotification
import java.lang.StringBuilder

class DetailProductViewModel(private val app: Application): AndroidViewModel(app) {
    private val _detailProduct = MutableLiveData<NetworkProduct>()
    private val _quantity = MutableLiveData<Int>()
    private val _price = MutableLiveData<Float>()
    val detailNetworkProduct: LiveData<NetworkProduct> get() = _detailProduct

    fun setDetailProduct(networkProduct: NetworkProduct?){
        _detailProduct.value = networkProduct
        _quantity.value = networkProduct?.quantity
        _price.value = networkProduct?.price
    }

    fun sendNotification(oldNetworkProduct: NetworkProduct?){
        val notificationManager = ContextCompat.getSystemService(app,
            NotificationManager::class.java) as NotificationManager

        // Create expanded text
        val expandedMsgStringBuilder = StringBuilder()
        if(oldNetworkProduct?.quantity != _detailProduct.value?.quantity){
            expandedMsgStringBuilder.append(app.getString(R.string.quantity_updated))
                .append(_detailProduct.value?.quantity)
                .append(app.getString(R.string.quantity_update_to))
                .append(oldNetworkProduct?.quantity)
        }

        if(oldNetworkProduct?.price != _detailProduct.value?.price){
            if(expandedMsgStringBuilder.isNotEmpty()) expandedMsgStringBuilder.append("\n")
            expandedMsgStringBuilder.append(app.getString(R.string.price_updated))
                .append(_detailProduct.value?.price)
                .append(app.getString(R.string.price_updated_to))
                .append(oldNetworkProduct?.price)
        }
        Log.d("Notification text", expandedMsgStringBuilder.toString())

        _detailProduct.value?.let {
            notificationManager.sendNotification(
                app.getString(R.string.messageNotification),
                expandedMsgStringBuilder.toString(),
                app,
                it
            )
        }
    }

    val quantityString = Transformations.map(_quantity){
        it.toString()
    }

    val priceString = Transformations.map(_price){
        it.toString()
    }
}
