package com.example.productscanner.viewmodel

import android.app.Application
import android.app.NotificationManager
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.lifecycle.*
import com.example.productscanner.R
import com.example.productscanner.data.network.Product
import com.example.productscanner.util.sendNotification
import java.lang.StringBuilder

class DetailProductViewModel(private val app: Application): AndroidViewModel(app) {
    private val _detailProduct = MutableLiveData<Product>()
    private val _quantity = MutableLiveData<Int>()
    private val _price = MutableLiveData<Float>()
    val detailProduct: LiveData<Product> get() = _detailProduct

    fun setDetailProduct(product: Product?){
        _detailProduct.value = product
        _quantity.value = product?.quantity
        _price.value = product?.price
    }

    fun sendNotification(oldProduct: Product?){
        val notificationManager = ContextCompat.getSystemService(app,
            NotificationManager::class.java) as NotificationManager

        // Create expanded text
        val expandedMsgStringBuilder = StringBuilder()
        if(oldProduct?.quantity != _detailProduct.value?.quantity){
            expandedMsgStringBuilder.append(app.getString(R.string.quantity_updated))
                .append(_detailProduct.value?.quantity)
                .append(app.getString(R.string.quantity_update_to))
                .append(oldProduct?.quantity)
        }

        if(oldProduct?.price != _detailProduct.value?.price){
            if(expandedMsgStringBuilder.isNotEmpty()) expandedMsgStringBuilder.append("\n")
            expandedMsgStringBuilder.append(app.getString(R.string.price_updated))
                .append(_detailProduct.value?.price)
                .append(app.getString(R.string.price_updated_to))
                .append(oldProduct?.price)
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
