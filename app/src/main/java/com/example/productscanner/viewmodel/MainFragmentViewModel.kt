package com.example.productscanner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.model.Product
import com.example.productscanner.model.ProductsApi
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

//enum class ProductApiStatus {LOADING, ERROR, DONE}

class MainFragmentViewModel: ViewModel() {
//    private var job: Job? = null
//    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
//        onError(throwable.localizedMessage)
//    }

//    private val _products = MutableLiveData<List<Product>>()
    private val _productsFiltered = MutableLiveData<List<Product>>()
//    private val _productsError = MutableLiveData<String?>()
    private val _navigationToDetail = MutableLiveData<Product>()
//    private val _status = MutableLiveData<ProductApiStatus>()
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
            for(Product in products?.value!!){
                if(Product.name.toLowerCase(Locale.getDefault()).contains(_query!!)){
                    filteredProducts.add(Product)
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