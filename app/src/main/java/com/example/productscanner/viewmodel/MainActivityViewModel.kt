package com.example.productscanner.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.model.Product
import com.example.productscanner.model.ProductsApi
import com.example.productscanner.repositories.ProductsRepository
import com.example.productscanner.util.readOnPreferences
import com.example.productscanner.util.writeOnPreferences
import com.example.productscanner.view.MainActivity
import kotlinx.coroutines.*

enum class ProductApiStatus {LOADING, ERROR, DONE}

class MainActivityViewModel: ViewModel() {
    private val repository: ProductsRepository = ProductsRepository(ProductsApi.retrofitService)

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
            val response = repository.getProducts()
            if(response.body != null){
                _products.value = response.body
                _loadPreference.value = true
                _productsError.value = null
                _status.value = ProductApiStatus.DONE
            }else{
                response.errorMessage?.let { onError(it) }
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

    /***
     * Sets the isSaved attribute to true if the products is find in the preferences file
     */
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

    /***
     * Saves the product in the preferences file
     */
    fun saveIdProduct(activity: MainActivity, idProduct: Int){
        jobPreference = CoroutineScope(Dispatchers.IO).launch {
            writeOnPreferences(activity, idProduct)
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