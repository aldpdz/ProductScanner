package com.example.productscanner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.model.Product
import java.util.*
import kotlin.collections.ArrayList

class MainFragmentViewModel: ViewModel() {
    private val _productsFiltered = MutableLiveData<List<Product>>()
    private val _navigationToDetail = MutableLiveData<Product>()

    var _query: String? = null
    private var products: LiveData<List<Product>>? = null
    val productsFiltered: LiveData<List<Product>> get() = _productsFiltered
    var productsError : LiveData<String?>? = null
    val navigationToDetail: LiveData<Product> get() = _navigationToDetail
    var status: LiveData<ProductApiStatus>? = null

    fun setResponseData(productsError: LiveData<String?>, status: LiveData<ProductApiStatus>, products: LiveData<List<Product>>){
        this.productsError = productsError
        this.status = status
        this.products = products
    }

    fun queryProducts(query: String?){
        this._query = query
        filterProducts()
    }

    fun filterProducts(){
        if (_query.isNullOrEmpty()){
            _productsFiltered.value = products?.value
        }else{
            val filteredProducts = ArrayList<Product>()
            for(product in products?.value!!){
                if(product.name.toLowerCase(Locale.getDefault()).contains(_query!!)){
                    filteredProducts.add(product)
                }
            }
            _productsFiltered.value = filteredProducts
        }
    }

    fun displayNavigationToDetail(product: Product){
        _navigationToDetail.value = product
    }

    fun displayNavigationToDetailComplete(){
        _navigationToDetail.value = null
    }
}