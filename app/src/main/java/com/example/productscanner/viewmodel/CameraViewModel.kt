package com.example.productscanner.viewmodel

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.model.Product
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition

enum class ScannerStatusItem {FOUND, NOT_FOUND}
enum class ScannerStatus{TRY_AGAIN, FAIL}
enum class TypeScanner {UPC, SKU}

class CameraViewModel : ViewModel() {
    private val _scannerStatusItem = MutableLiveData<ScannerStatusItem>()
    private val _scannerStatus = MutableLiveData<ScannerStatus>()
    private val _btnVisibility = MutableLiveData<Int>()

    private var products: LiveData<List<Product>>? = null
    val scannerStatusItem: LiveData<ScannerStatusItem> get() = _scannerStatusItem
    val scannerStatus: LiveData<ScannerStatus> get() = _scannerStatus
    val btnVisibility: LiveData<Int> get() = _btnVisibility
    var productByBarCode: Product? = null
    var typeScanner: TypeScanner? = null

    private val prefix: String = "SKU-"

    init {
        _btnVisibility.value = View.VISIBLE
    }

    fun setProducts(products: LiveData<List<Product>>){
        this.products = products
    }

    // TODO run on a coroutine
    fun runBarcodeScanner(bitmap: Bitmap){
        val image = InputImage.fromBitmap(bitmap, 0)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
            .build()

        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener {
                processBarcodes(it)
            }
            .addOnFailureListener {
                _scannerStatus.value = ScannerStatus.FAIL
            }
    }

    private fun processBarcodes(barcodes: List<Barcode>){
        if(barcodes.isEmpty()){
            _scannerStatus.value = ScannerStatus.TRY_AGAIN
        }else{
            val barCode: String? = barcodes[0].displayValue
            barCode?.let {
                getProduct(it)
            }
        }
    }

    // TODO run on a coroutine
    fun textRecognition(bitmap: Bitmap){
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient()

        recognizer.process(image)
            .addOnSuccessListener { texts ->
                processTextRecognitionResult(texts)
            }
            .addOnFailureListener {
                _scannerStatus.value = ScannerStatus.FAIL
            }
    }

    private fun findSKUCode(texts: Text):String?{
        val lines = texts.text.split("\n")
        for (line in lines){
            val words = line.split(" ")
            for (word in words){
                if (word.contains(prefix))
                    return word
            }
        }
        return null
    }

    private fun processTextRecognitionResult(texts: Text){
        val blocks = texts.textBlocks
        if(blocks.isEmpty()){
            _scannerStatus.value = ScannerStatus.TRY_AGAIN
        }else{
            val skuCode = findSKUCode(texts)
            if (skuCode == null){
                _scannerStatus.value = ScannerStatus.TRY_AGAIN
            }else{
                getProduct(skuCode)
            }
        }
    }

    private fun getProduct(code: String){
        var found = false
        for(product in products?.value!!){
            when(typeScanner){
                TypeScanner.UPC -> {
                    if(product.upc == code){
                        productByBarCode = product
                        found = true
                    }
                }
                TypeScanner.SKU -> {
                    if(product.sku == code){
                        productByBarCode = product
                        found = true
                    }
                }
            }
        }
        if(found){
            _scannerStatusItem.value = ScannerStatusItem.FOUND
        }else{
            _scannerStatusItem.value = ScannerStatusItem.NOT_FOUND
        }
    }

    fun hideButtons(){
        _btnVisibility.value = View.GONE
    }

    fun showButtons(){
        _btnVisibility.value = View.VISIBLE
    }

    fun displayBarCodeToDetailComplete(){
        _scannerStatusItem.value = null
    }
    
    fun scannerStatusFailComplete(){
        _scannerStatus.value = null
    }
}
