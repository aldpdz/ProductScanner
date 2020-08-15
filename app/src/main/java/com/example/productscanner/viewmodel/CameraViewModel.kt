package com.example.productscanner.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.repositories.IProductsRepository
import com.example.productscanner.util.Event
import kotlinx.coroutines.*

enum class ScannerStatusItem {FOUND}
enum class ScannerStatus{TRY_AGAIN, FAIL}
enum class TypeScanner {UPC, SKU}
enum class CameraStatus {STOP, START}

class CameraViewModel @ViewModelInject constructor(
    private val repository: IProductsRepository
): ViewModel() {

    // TODO method to listen permissions from the shared model

    // Status when the product is found
    private val _scannerStatusItem = MutableLiveData<Event<ScannerStatusItem>>()
    val scannerStatusItem: LiveData<Event<ScannerStatusItem>> get() = _scannerStatusItem

    // Status for the mlkit library
    private val _scannerStatus = MutableLiveData<Event<ScannerStatus>>()
    val scannerStatus: LiveData<Event<ScannerStatus>> get() = _scannerStatus

    val cameraStatus = MutableLiveData<Event<CameraStatus>>()
    val bottomSheetPaused = MutableLiveData<Event<Boolean>>()

    val sku = MutableLiveData<String>()
    val productSKU = sku.switchMap { getProduct(it, TypeScanner.SKU) }

    val upc = MutableLiveData<String>()
    val productUPC = upc.switchMap { getProduct(it, TypeScanner.UPC) }

    private fun getProduct(code: String, typeScanner: TypeScanner) : LiveData<Event<DomainProduct>>{
        // TODO - Use live data builder
        val result = MutableLiveData<Event<DomainProduct>>()
        viewModelScope.launch {
            when(typeScanner){
                TypeScanner.SKU -> {
                    val findResult = repository.findBySKU(code)
                    if(findResult is Result.Success){
                        result.value = Event(findResult.data)
                    }else{
                        result.value = null
                    }
                }
                TypeScanner.UPC -> {
                    val findResult = repository.findByUPC(code)
                    if(findResult is Result.Success){
                        result.value = Event(findResult.data)
                    }else{
                        result.value = null
                    }
                }
            }
        }
        return result
    }
}
