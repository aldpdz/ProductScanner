package com.example.productscanner.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.model.Product
import com.example.productscanner.model.ProductsApi
import com.example.productscanner.util.readOnPreferences
import com.example.productscanner.util.writeOnPrefereces
import com.example.productscanner.view.MainActivity
import kotlinx.coroutines.*

enum class ProductApiStatus {LOADING, ERROR, DONE}

class MainActivityViewModel: ViewModel() {
    private var job: Job? = null
    private var jobPreference: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError(throwable.localizedMessage)
    }

    private val _products = MutableLiveData<List<Product>>()
    private val _productsError = MutableLiveData<String?>()
    private val _status = MutableLiveData<ProductApiStatus>()
    private val _loadPreference = MutableLiveData<Boolean?>()

    val productsError : LiveData<String?> get() =  _productsError
    val status: LiveData<ProductApiStatus> get() = _status
    val products: LiveData<List<Product>> get() = _products
    val loadPreference: LiveData<Boolean?> get() = _loadPreference

    init {
        getProducts()
        _loadPreference.value = null
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
                _loadPreference.value = true
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

    fun setSavedIds(activity: MainActivity){
        jobPreference = CoroutineScope(Dispatchers.IO).launch {
            val newProducts: List<Product>? =  _products.value?.toList()
            newProducts?.let{
                for (product in it){
                    if (readOnPreferences(activity, product.id) != -1){
                        product.isSaved = true
                    }
                    Log.d("Preferences", "saved products")
                }
            }
            withContext(Dispatchers.Main){
                _products.value = newProducts
            }
        }
    }

    fun saveIdProduct(activity: MainActivity, idProduct: Int){
        jobPreference = CoroutineScope(Dispatchers.IO).launch {
            writeOnPrefereces(activity, idProduct)
            for (product in _products.value!!){
                if(product.id == idProduct){
                    product.isSaved = true
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
        jobPreference?.cancel()
    }
}