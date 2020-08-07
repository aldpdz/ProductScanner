package com.example.productscanner.viewmodel

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.productscanner.R
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.repositories.IProductsRepository
import com.example.productscanner.util.sendNotification
import com.example.productscanner.util.writeOnPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.StringBuilder

class DetailProductViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val repository: IProductsRepository): ViewModel() {

    private val _detailProduct = MutableLiveData<DomainProduct>()
    val detailProduct: LiveData<DomainProduct> get() = _detailProduct

    private val _quantity = MutableLiveData<Int>()
    private val _price = MutableLiveData<Float>()

    // TODO - Receive id and get the product with the repository instead of setting the values
    fun setDetailProduct(product: DomainProduct?){
        _detailProduct.value = product
        _quantity.value = product?.quantity
        _price.value = product?.price
    }

    fun updateProduct(product: DomainProduct){
        viewModelScope.launch {
            repository.updateProduct(product)
        }
    }

    // TODO Unit test instrumental
    /***
     * Saves the product in the preferences file
     * @param idProduct: id of the product
     */
    fun saveIdProduct(idProduct: Int){
        viewModelScope.launch {
            withContext(Dispatchers.IO){
                writeOnPreferences(appContext, idProduct)

            }
        }
    }

    fun sendNotification(updatedProduct: DomainProduct?){
        val notificationManager = ContextCompat.getSystemService(appContext,
            NotificationManager::class.java) as NotificationManager

        // Create expanded text
        val expandedMsgStringBuilder = StringBuilder()
            .append(updatedProduct?.name?.plus("\n"))
        if(updatedProduct?.quantity != _detailProduct.value?.quantity){
            expandedMsgStringBuilder.append(appContext.getString(R.string.quantity_updated))
                .append(_detailProduct.value?.quantity)
                .append(appContext.getString(R.string.quantity_update_to))
                .append(updatedProduct?.quantity)
        }

        if(updatedProduct?.price != _detailProduct.value?.price){
            if(expandedMsgStringBuilder.isNotEmpty()) expandedMsgStringBuilder.append("\n")
            expandedMsgStringBuilder.append(appContext.getString(R.string.price_updated))
                .append(_detailProduct.value?.price)
                .append(appContext.getString(R.string.price_updated_to))
                .append(updatedProduct?.price)
        }

        Log.d("Notification text", expandedMsgStringBuilder.toString())

        viewModelScope.launch {
            // Saving old product as a temp product
            _detailProduct.value?.let {
                repository.insertTempProduct(it)
            }
        }

        _detailProduct.value?.let { oldProduct ->
            updatedProduct?.let{
                notificationManager.sendNotification(
                    appContext.getString(R.string.messageNotification),
                    expandedMsgStringBuilder.toString(),
                    appContext,
                    updatedProduct)
            }
        }
    }

    val quantityString = Transformations.map(_quantity){
        it.toString()
    }

    val priceString = Transformations.map(_price){
        it.toString()
    }
}
