package com.example.productscanner.viewmodel

import android.app.Activity
import android.util.Log
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.productscanner.model.Product
import com.example.productscanner.repositories.IProductsRepository
import com.example.productscanner.util.Event
import com.example.productscanner.util.readOnPreferences
import com.example.productscanner.util.writeOnPreferences
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.ArrayList

enum class ProductApiStatus {LOADING, ERROR, DONE}

class SharedViewModel @ViewModelInject constructor(
    private val repository: IProductsRepository): ViewModel() {
    private var jobPreference: Job? = null
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError(throwable.localizedMessage)
    }

    private val _productsFiltered = MutableLiveData<List<Product>>()
    private val _navigationToDetail = MutableLiveData<Event<Product>>()
    private val _products = MutableLiveData<List<Product>>()
    private val _productsError = MutableLiveData<String?>()
    private val _status = MutableLiveData<ProductApiStatus>()
    private val _loadPreference = MutableLiveData<Boolean?>()

    val productsError : LiveData<String?> get() =  _productsError
    val status: LiveData<ProductApiStatus> get() = _status
    val products: LiveData<List<Product>> get() = _products
    val productsFiltered: LiveData<List<Product>> get() = _productsFiltered
    val loadPreference: LiveData<Boolean?> get() = _loadPreference
    val navigationToDetail: LiveData<Event<Product>> get() = _navigationToDetail

    private var _query: String? = null

    init {
        getProducts()
    }

    /***
     * Refresh the data from the network
     */
    fun refreshData(){
        getProducts()
    }

    /***
     * Load the products from the network
     */
    private fun getProducts(){
        viewModelScope.launch(exceptionHandler) {
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

    /***
     * Updates the product
     * @param product: object product to be updated
     */
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

    // TODO unit test
    /***
     * Manage the repository error
     * @param message: String to display
     */
    private fun onError(message: String){
        _products.value = ArrayList()
        _status.value = ProductApiStatus.ERROR
        _productsError.value = message
    }

    // TODO unit test instrumental
    // TODO update to use vieModelScope
    /***
     * Sets the isSaved attribute to true if the products is find in the preferences file
     */
    fun setSavedIds(activity: Activity){
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

    // TODO Unit test instrumental
    /***
     * Saves the product in the preferences file
     * @param idProduct: id of the product
     */
    fun saveIdProduct(activity: Activity, idProduct: Int){
        jobPreference = CoroutineScope(Dispatchers.IO).launch {
            writeOnPreferences(activity, idProduct)
            for (product in _products.value!!){
                if(product.id == idProduct){
                    product.isSaved = true
                }
            }
        }
    }

    fun queryProducts(query: String?){
        this._query = query
        filterProducts()
    }

    fun filterProducts(){
        products.value?.let {
            if (_query.isNullOrEmpty()){
                _productsFiltered.value = products.value
            }else{
                val filteredProducts = ArrayList<Product>()
                for(product in it){
                    if(product.name.toLowerCase(Locale.getDefault()).contains(_query!!)){
                        filteredProducts.add(product)
                    }
                }
                _productsFiltered.value = filteredProducts
            }
        }
    }

    fun displayNavigationToDetail(product: Product){
        _navigationToDetail.value = Event(product)
    }

    override fun onCleared() {
        super.onCleared()
        jobPreference?.cancel()
    }
}