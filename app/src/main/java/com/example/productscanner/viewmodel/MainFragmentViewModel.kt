package com.example.productscanner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.model.Product
import com.example.productscanner.model.ProductsApi
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

enum class ProductApiStatus {LOADING, ERROR, DONE}

class MainFragmentViewModel: ViewModel() {
    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError(throwable.localizedMessage)
    }

    private val _products = MutableLiveData<List<Product>>()
    private val _productsFiltered = MutableLiveData<List<Product>>()
    private val _productsError = MutableLiveData<String?>()
    private val _navigationToDetail = MutableLiveData<Product>()
    private val _status = MutableLiveData<ProductApiStatus>()
    var _query: String? = null

    val productsFiltered: LiveData<List<Product>> get() = _productsFiltered
    val productsError : LiveData<String?> get() =  _productsError
    val navigationToDetail: LiveData<Product> get() = _navigationToDetail
    val status: LiveData<ProductApiStatus> get() = _status

    init {
        getProducts()
    }

    fun refreshData(){
        getProducts()
    }

    private fun getProducts(){
        job = CoroutineScope(Dispatchers.Main + exceptionHandler).launch {
            _status.value = ProductApiStatus.LOADING
            val response = ProductsApi.retrofitService.getProducts()
            if (response.isSuccessful) {
                _status.value = ProductApiStatus.DONE
                _products.value = response.body()
                _productsError.value = null
                filterProducts()
            } else {
                onError("The products couldn't be loaded")
            }
        }
    }

    fun queryProducts(query: String?){
        this._query = query
        filterProducts()
    }

    private fun filterProducts(){
        if (_query.isNullOrEmpty()){
            _productsFiltered.value = _products.value
        }else{
            val filteredProducts = ArrayList<Product>()
            for(Product in _products.value!!){
                if(Product.name.toLowerCase(Locale.getDefault()).contains(_query!!)){
                    filteredProducts.add(Product)
                }
            }
            _productsFiltered.value = filteredProducts
        }
    }

    private fun onError(message: String){
        _products.value = ArrayList()
        _productsFiltered.value = ArrayList()
        _status.value = ProductApiStatus.ERROR
        _productsError.value = message
    }

    fun displayNavigationToDetail(product: Product){
        _navigationToDetail.value = product
    }

    fun displayNavigationToDetailComplete(){
        _navigationToDetail.value = null
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}