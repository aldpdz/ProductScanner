package com.example.productscanner.viewmodel

import android.app.Activity
import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.repositories.IProductsRepository
import com.example.productscanner.util.*
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

// TODO - change name
enum class ProductApiStatus {LOADING, ERROR, DONE}
enum class LocalStatus {ERROR, SUCCESS}

class SharedViewModel @ViewModelInject constructor(
    private val repository: IProductsRepository): ViewModel() {
    private var jobPreference: Job? = null
    private var deferredKeys: Deferred<List<Int>>? = null
    private var deferredBoolean: Deferred<Boolean>? = null
    private val exceptionHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
        onError(throwable.localizedMessage)
    }

    private val _productsFiltered = MutableLiveData<List<DomainProduct>>()
    val productsFiltered: LiveData<List<DomainProduct>> get() = _productsFiltered

    private val _navigationToDetail = MutableLiveData<Event<DomainProduct>>()
    val navigationToDetail: LiveData<Event<DomainProduct>> get() = _navigationToDetail

    private val _productsError = MutableLiveData<String?>()
    val productsError : LiveData<String?> get() =  _productsError

    private val _networkStatus = MutableLiveData<ProductApiStatus>()
    val networkStatus: LiveData<ProductApiStatus> get() = _networkStatus

    private val _localStatus = MutableLiveData<LocalStatus>()
    val localStatus : LiveData<LocalStatus> get() = _localStatus

    private val _loadPreference = MutableLiveData<Boolean?>()
    val loadPreference: LiveData<Boolean?> get() = _loadPreference

    val products = repository.getProductsFromLocal().map { getData(it) }

    private var _query: String? = null
    private var idsFromPreferences = listOf<Int>()

    /***
     * Load the data from the internet if it's the first time to load it
     */
    fun firstDataLoad(activity: Activity){
        // true if it's the first time to load the data
        viewModelScope.launch {
            val firstLoad = getFirstLoad(activity)
            firstLoad.let {
                if(it){
                    getProducts()
                }
            }
        }
    }

    private suspend fun getFirstLoad(activity: Activity): Boolean{
        deferredBoolean = CoroutineScope(Dispatchers.IO).async {
            readFirstLoad(activity)
        }
        return deferredBoolean!!.await()
    }

    /***
     * Set in the preferences that the data has been loaded
     */
    fun setFirstDataLoad(activity: Activity){
        Log.i("SharedVM", "First data load")
        jobPreference =  CoroutineScope(Dispatchers.IO).launch {
            writeFirstLoad(activity)
        }
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
        _networkStatus.value = ProductApiStatus.LOADING
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.getProductsFromRemote()
                _productsError.value = null
                _networkStatus.value = ProductApiStatus.DONE
            }catch (e: Exception){
                _networkStatus.value = ProductApiStatus.ERROR
                onError("The data couldn't be loaded")
            }
        }
    }

    // TODO unit test
    /***
     * Manage the repository error
     * @param message: String to display
     */
    private fun onError(message: String){
        _productsError.value = message
    }

    /***
     * Load products' ids from preferences
     */
    fun loadIdsFromPreferences(activity: Activity){
        // TODO - good case to livedata builder with emit
        Log.i("ShareVM", "Load preferences")
        viewModelScope.launch {
            idsFromPreferences = getIdsFromPreferences(activity)
            _loadPreference.value = true
        }
    }

    private suspend fun getIdsFromPreferences(activity: Activity): List<Int>{
        deferredKeys = CoroutineScope(Dispatchers.IO).async {
            getAllKeys(activity).toList().filter {
                it.isDigitsOnly()
            }.map {
                it.toInt()
            }
        }
        return deferredKeys!!.await()
    }

    // TODO unit test instrumental
    // TODO update to use vieModelScope
    /***
     * Sets the isSaved attribute to true if the products is find in the preferences file
     */
    private fun setSavedIds(argProducts: List<DomainProduct>?) : List<DomainProduct>?{
        val newProducts: List<DomainProduct>? = argProducts
        newProducts?.let{
            for (product in it){
                if(product.id in idsFromPreferences){
                    product.isSaved = true
                }
            }
        }
        return newProducts
    }

    // TODO Unit test instrumental
    /***
     * Saves the product in the preferences file
     * @param idProduct: id of the product
     */
    fun saveIdProduct(activity: Activity, idProduct: Int){
        jobPreference = CoroutineScope(Dispatchers.IO).launch {
            writeOnPreferences(activity, idProduct)
            for (product in products.value!!){
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

    // TODO - maybe add a switchmap
    /***
     * Filter the products base on the name with a query
     */
    fun filterProducts(){
        products.value?.let {
            if (_query.isNullOrEmpty()){
                _productsFiltered.value = setSavedIds(products.value)
            }else{
                val filteredProducts = ArrayList<DomainProduct>()
                for(product in it){
                    if(product.name.toLowerCase(Locale.getDefault()).contains(_query!!)){
                        filteredProducts.add(product)
                    }
                }
                _productsFiltered.value = setSavedIds(filteredProducts)
            }
        }
    }

    private fun getData(productsResult: Result<List<DomainProduct>>): List<DomainProduct>{
        val localData = (productsResult as Result.Success).data
        if(localData.isEmpty()){
            _localStatus.value = LocalStatus.ERROR
        }else{
            _localStatus.value = LocalStatus.SUCCESS
        }
        return localData
    }

    fun displayNavigationToDetail(domainProduct: DomainProduct){
        _navigationToDetail.value = Event(domainProduct)
    }

    override fun onCleared() {
        super.onCleared()
        jobPreference?.cancel()
        deferredKeys?.cancel()
    }
}