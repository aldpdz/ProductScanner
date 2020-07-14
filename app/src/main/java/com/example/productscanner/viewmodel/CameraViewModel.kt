package com.example.productscanner.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.util.Event
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.TextRecognizer
import kotlinx.coroutines.*
import kotlinx.coroutines.tasks.await

enum class ScannerStatusItem {FOUND, NOT_FOUND}
enum class ScannerStatus{TRY_AGAIN, FAIL}
enum class TypeScanner {UPC, SKU}

class CameraViewModel : ViewModel() {

    // TODO method to listen permissions from the shared model

    private val _scannerStatusItem = MutableLiveData<Event<ScannerStatusItem>>()
    val scannerStatusItem: LiveData<Event<ScannerStatusItem>> get() = _scannerStatusItem

    private val _scannerStatus = MutableLiveData<Event<ScannerStatus>>()
    val scannerStatus: LiveData<Event<ScannerStatus>> get() = _scannerStatus

    private val _btnVisibility = MutableLiveData<Int>()
    val btnVisibility: LiveData<Int> get() = _btnVisibility

    private val _bitMap = MutableLiveData<Bitmap>()
    val bitMap: LiveData<Bitmap> get() = _bitMap

//    private var products: LiveData<List<DomainProduct>>? = null
    var productByBarCode: DomainProduct? = null
    var typeScanner: TypeScanner? = null

    init {
        _btnVisibility.value = View.VISIBLE
    }

//    fun setProducts(products: LiveData<List<NetworkProduct>>){
//        this.products = products
//    }

    // TODO instrumental test
    fun processInputImage(byteArray: ByteArray){
        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        _bitMap.value = bitmap

        when(typeScanner){
            TypeScanner.UPC -> {
                runBarcodeScanner(bitmap)
            }
            TypeScanner.SKU -> {
                textRecognition(bitmap)
            }
        }
    }

    private fun runBarcodeScanner(bitmap: Bitmap){
        viewModelScope.launch {
            val image = InputImage.fromBitmap(bitmap, 0)

            val options = BarcodeScannerOptions.Builder()
                .setBarcodeFormats(Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
                .build()

            val scanner = BarcodeScanning.getClient(options)

            try{
                val barCodes = processImageBarCode(image, scanner)
                processBarCodes(barCodes)
            }catch (e: Exception){
                _scannerStatus.value = Event(ScannerStatus.FAIL)
            }
        }
    }

    private fun textRecognition(bitmap: Bitmap){
        viewModelScope.launch {
            val image = InputImage.fromBitmap(bitmap, 0)
            val recognizer = TextRecognition.getClient()
            try {
                val text = processImageText(image, recognizer)
                processTextRecognitionResult(text)
            }catch (e: Exception){
                _scannerStatus.value = Event(ScannerStatus.FAIL)
            }
        }
    }

    private suspend fun processImageBarCode(inputImage: InputImage, scanner: BarcodeScanner):
            List<Barcode> = withContext(Dispatchers.Default){
        return@withContext scanner.process(inputImage).await()
    }

    private suspend fun processImageText(inputImage: InputImage, recognizer: TextRecognizer):
            Text = withContext(Dispatchers.Default) {
        return@withContext recognizer.process(inputImage).await()
    }

    private fun processBarCodes(barCodes: List<Barcode>){
        if(barCodes.isEmpty()){
            _scannerStatus.value = Event(ScannerStatus.TRY_AGAIN)
        }else{
            val barCode: String? = barCodes[0].displayValue
            barCode?.let {
                getProduct(it)
            }
        }
    }

    private fun processTextRecognitionResult(texts: Text){
        val blocks = texts.textBlocks
        if(blocks.isEmpty()){
            _scannerStatus.value = Event(ScannerStatus.TRY_AGAIN)
        }else{
            val skuCode = findSKUCode(texts.text)
            if (skuCode == null){
                _scannerStatus.value = Event(ScannerStatus.TRY_AGAIN)
            }else{
                getProduct(skuCode)
            }
        }
    }


    private fun getProduct(code: String){
        // TODO - implement with room
//        var found = false
//        for(product in products?.value!!){
//            when(typeScanner){
//                TypeScanner.UPC -> {
//                    if(product.upc == code){
//                        productByBarCode = product
//                        found = true
//                    }
//                }
//                TypeScanner.SKU -> {
//                    if(product.sku == code){
//                        productByBarCode = product
//                        found = true
//                    }
//                }
//            }
//        }
//        if(found){
//            _scannerStatusItem.value = Event(ScannerStatusItem.FOUND)
//        }else{
//            _scannerStatusItem.value = Event(ScannerStatusItem.NOT_FOUND)
//        }
    }

    fun hideButtons(){
        _btnVisibility.value = View.GONE
    }

    fun showButtons(){
        _btnVisibility.value = View.VISIBLE
    }
}
