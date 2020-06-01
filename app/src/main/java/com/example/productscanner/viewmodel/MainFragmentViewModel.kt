package com.example.productscanner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.model.Product
import com.example.productscanner.model.ProductsService
import kotlinx.coroutines.*

class MainFragmentViewModel: ViewModel() {
    private val productsService = ProductsService.getProductsService()
    private var job: Job? = null
    private var job2: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError(throwable.localizedMessage)
    }

    private val _products = MutableLiveData<List<Product>>()
    private val _productsError = MutableLiveData<String?>()
    private val _loading = MutableLiveData<Boolean>()
    private val _navigationToDetail = MutableLiveData<Product>()

    val products : LiveData<List<Product>> get() = _products
    val productsError : LiveData<String?> get() =  _productsError
    val loading : LiveData<Boolean> get() =  _loading
    val navigationToDetail: LiveData<Product> get() = _navigationToDetail

    init {
        getProducts()
    }

    fun refreshData(){
        getProducts()
    }

    private fun getProducts(){
        _loading.value = true

        job = CoroutineScope(Dispatchers.IO + exceptionHandler).launch {
            val response = productsService.getProducts()
            withContext(Dispatchers.Main){
                if(response.isSuccessful){
                    _products.value = response.body()
                    _productsError.value = null
                    _loading.value = false
                }else{
                    onError("The products couldn't be loaded")
                }
            }
        }
    }

    private fun onError(message: String){
        job2 = MainScope().launch {
            _productsError.value = message
            _loading.value = false
        }
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