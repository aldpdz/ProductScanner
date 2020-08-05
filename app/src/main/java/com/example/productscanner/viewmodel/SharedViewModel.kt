package com.example.productscanner.viewmodel

import android.content.Context
import android.util.Log
import androidx.core.text.isDigitsOnly
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.repositories.IProductsRepository
import com.example.productscanner.util.*
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

enum class RemoteStatus {LOADING, ERROR, DONE}
enum class LocalStatus {ERROR, SUCCESS}

class SharedViewModel @ViewModelInject constructor(
    @ApplicationContext private val appContext: Context,
    private val repository: IProductsRepository): ViewModel() {
    private var jobPreference: Job? = null
    private var deferredKeys: Deferred<List<Int>>? = null
    private var deferredBoolean: Deferred<Boolean>? = null
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.localizedMessage?.let {
            onError(it)
        }
    }

    private val _productsFiltered = MutableLiveData<List<DomainProduct>>()
    val productsFiltered: LiveData<List<DomainProduct>> get() = _productsFiltered

    private val _navigationToDetail = MutableLiveData<Event<DomainProduct>>()
    val navigationToDetail: LiveData<Event<DomainProduct>> get() = _navigationToDetail

    private val _productsError = MutableLiveData<Event<String>>()
    val productsError : LiveData<Event<String>> get() =  _productsError

    private val _networkStatus = MutableLiveData<RemoteStatus>()
    val networkStatus: LiveData<RemoteStatus> get() = _networkStatus

    private val _localStatus = MutableLiveData<LocalStatus>()
    val localStatus : LiveData<LocalStatus> get() = _localStatus

    val products = repository.getProductsFromLocal().map {
        Log.d("ShareVM", "Data loaded from local source")
        getData(it)
    }

    private var _query: String? = null
    private var _savedQuery: String? = null // Save query when changing views

    init {
        firstDataLoad()
    }

    /***
     * Load the data from the internet if it's the first time to load it
     */
    private fun firstDataLoad(){
        // true if it's the first time to load the data
        viewModelScope.launch {
            val firstLoad = getFirstLoad()
            firstLoad.let {
                if(it){
                    getProducts()
                }
            }
        }
    }

    private suspend fun getFirstLoad(): Boolean{
        deferredBoolean = CoroutineScope(Dispatchers.IO).async {
            readFirstLoad(appContext)
        }
        return deferredBoolean!!.await()
    }

    /***
     * Set in the preferences that the data has been loaded
     */
    private fun setFirstDataLoad(){
        Log.i("SharedVM", "First data load")
        jobPreference =  CoroutineScope(Dispatchers.IO).launch {
            writeFirstLoad(appContext)
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
        _networkStatus.value = RemoteStatus.LOADING
        viewModelScope.launch(exceptionHandler) {
            try {
                repository.getProductsFromRemote()
                setFirstDataLoad()
                _networkStatus.value = RemoteStatus.DONE
            }catch (e: Exception){
                _networkStatus.value = RemoteStatus.ERROR
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
        _productsError.value = Event(message)
    }

    private suspend fun getIdsFromPreferences(): List<Int>{
        deferredKeys = CoroutineScope(Dispatchers.IO).async {
            getAllKeys(appContext).toList().filter {
                it.isDigitsOnly()
            }.map {
                it.toInt()
            }
        }
        return deferredKeys!!.await()
    }

    /***
     * Sets the isSaved attribute to true if the products is find in the preferences file
     */
    private fun setSavedIds(argProducts: List<DomainProduct>?, idsFromPreferences: List<Int>) : List<DomainProduct>?{
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

    fun queryProducts(query: String?){
        this._query = query
        filterProducts()
    }

    fun saveQuery(){
        _savedQuery = _query
    }

    // TODO - maybe add a switchmap
    /***
     * Filter the products base on the name with a query
     */
    fun filterProducts(){

        if(_savedQuery != null){
            _query = _savedQuery
        }
        _savedQuery = null

        Log.d("ShareVM", "Filter:".plus(_query))
        viewModelScope.launch {
            val idsFromPreferences = getIdsFromPreferences()
            products.value?.let {
                if (_query.isNullOrEmpty()){
                    _productsFiltered.value = setSavedIds(products.value, idsFromPreferences)
                }else{
                    val filteredProducts = ArrayList<DomainProduct>()
                    // TODO - Use filter function
                    for(product in it){
                        if(product.name.toLowerCase(Locale.getDefault()).contains(_query!!)){
                            filteredProducts.add(product)
                        }
                    }
                    _productsFiltered.value = setSavedIds(filteredProducts, idsFromPreferences)
                }
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