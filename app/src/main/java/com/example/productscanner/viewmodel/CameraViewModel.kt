package com.example.productscanner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.model.Product

enum class ScannerStatus {FOUND, NOT_FOUND}

class CameraViewModel : ViewModel() {
    private val _scannerStatus = MutableLiveData<ScannerStatus>()

    private var products: LiveData<List<Product>>? = null
    val scannerStatus: LiveData<ScannerStatus> get() = _scannerStatus
    var productByBarCode: Product? = null

    fun setProducts(products: LiveData<List<Product>>){
        this.products = products
    }

    fun getProductByBarCode(upc: String){
        var found = false
        for(product in products?.value!!){
            if(product.upc == upc){
                productByBarCode = product
                found = true
            }
        }
        if(found){
            _scannerStatus.value = ScannerStatus.FOUND
        }else{
            _scannerStatus.value = ScannerStatus.NOT_FOUND
        }
    }

    fun displayBarCodeToDetailComplete(){
        _scannerStatus.value = null
    }
}
