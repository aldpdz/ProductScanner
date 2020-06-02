package com.example.productscanner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.model.Product
import com.example.productscanner.model.ProductsApi
import kotlinx.coroutines.*

enum class ProductApiStatus {LOADING, ERROR, DONE}

class MainFragmentViewModel: ViewModel() {
    private var job: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError(throwable.localizedMessage)
    }

    private val _products = MutableLiveData<List<Product>>()
    private val _productsError = MutableLiveData<String?>()
    private val _navigationToDetail = MutableLiveData<Product>()
    private val _status = MutableLiveData<ProductApiStatus>()

    val products : LiveData<List<Product>> get() = _products
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
                _products.value = response.body()
                _status.value = ProductApiStatus.DONE
                _productsError.value = null
            } else {
                onError("The products couldn't be loaded")
            }
        }
    }

    private fun onError(message: String){
        _products.value = ArrayList()
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