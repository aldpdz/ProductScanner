package com.example.productscanner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.productscanner.model.Product

class DetailProductViewModel: ViewModel() {
    private val _detailProduct = MutableLiveData<Product>()
    private val _quantity = MutableLiveData<Int>()
    private val _price = MutableLiveData<Float>()
    val detailProduct: LiveData<Product> get() = _detailProduct

    fun setDetailProduct(product: Product?){
        _detailProduct.value = product
        _quantity.value = product?.quantity
        _price.value = product?.price
    }

    val quantityString = Transformations.map(_quantity){
        it.toString()
    }

    val priceString = Transformations.map(_price){
        it.toString()
    }
}