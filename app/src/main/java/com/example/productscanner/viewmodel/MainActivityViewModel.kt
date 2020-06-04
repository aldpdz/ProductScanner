package com.example.productscanner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.model.Product
import com.example.productscanner.model.ProductsApi
import kotlinx.coroutines.*

enum class ProductApiStatus {LOADING, ERROR, DONE}

class MainActivityViewModel: ViewModel() {
    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError(throwable.localizedMessage)
    }

    private val _products = MutableLiveData<List<Product>>()
    private val _productsError = MutableLiveData<String?>()
    private val _status = MutableLiveData<ProductApiStatus>()

    val productsError : LiveData<String?> get() =  _productsError
    val status: LiveData<ProductApiStatus> get() = _status
    val products: LiveData<List<Product>> get() = _products

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
                _products.value = response.body()
                _productsError.value = null
                _status.value = ProductApiStatus.DONE
            } else {
                onError("The products couldn't be loaded")
            }
        }
    }

    fun updateProduct(product: Product?){
        _products.value?.let {
            for(_product in it){
                if(_product.id == product?.id){
                    _product.quantity = product.quantity
                    _product.price = product.price
                }
            }
        }
    }

    private fun onError(message: String){
        _products.value = ArrayList()
        _status.value = ProductApiStatus.ERROR
        _productsError.value = message
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}