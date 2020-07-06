package com.example.productscanner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.model.Product
import com.example.productscanner.util.Event

class MainFragmentViewModel: ViewModel() {
    private val _productsFiltered = MutableLiveData<List<Product>>()
    private val _navigationToDetail = MutableLiveData<Event<Product>>()

    private var _query: String? = null
    // TODO maybe use mutable live data instead
    private var products: LiveData<List<Product>>? = null
    val productsFiltered: LiveData<List<Product>> get() = _productsFiltered
    var productsError : LiveData<String?>? = null
    val navigationToDetail: LiveData<Event<Product>> get() = _navigationToDetail
    var status: LiveData<ProductApiStatus>? = null

    fun setResponseData(productsError: LiveData<String?>, status: LiveData<ProductApiStatus>, products: LiveData<List<Product>>){
        this.productsError = productsError
        this.status = status
        this.products = products
    }

    fun queryProducts(query: String?){
        this._query = query
    }

    fun displayNavigationToDetail(product: Product){
        _navigationToDetail.value = Event(product)
    }
}