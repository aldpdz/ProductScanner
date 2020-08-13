package com.example.productscanner.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.View
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.example.productscanner.data.Result
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.repositories.IProductsRepository
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

    val sku = MutableLiveData<String>()
    val productSKU = sku.switchMap { getProduct(it, TypeScanner.SKU) }

    val upc = MutableLiveData<String>()
    val productUPC = upc.switchMap { getProduct(it, TypeScanner.UPC) }

//    private val _btnVisibility = MutableLiveData<Int>()
//    val btnVisibility: LiveData<Int> get() = _btnVisibility

//    private val _bitMap = MutableLiveData<Bitmap>()
//    val bitMap: LiveData<Bitmap> get() = _bitMap

    private val _productByCode = MutableLiveData<Event<DomainProduct>>()
    val productByCode: LiveData<Event<DomainProduct>> get() = _productByCode

//    var typeScanner: TypeScanner? = null

//    init {
//        _btnVisibility.value = View.VISIBLE
//    }

    // TODO instrumental test
//    fun processInputImage(inputImage: InputImage){
////        val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
////        _bitMap.value = bitmap
//        runBarcodeScanner(inputImage)
//        runTextRecognition(inputImage)
////        when(typeScanner){
////            TypeScanner.UPC -> {
////                runBarcodeScanner(bitmap)
////            }
////            TypeScanner.SKU -> {
////                textRecognition(bitmap)
////            }
////        }
//    }

//    private fun runBarcodeScanner(inputImage: InputImage){
//        viewModelScope.launch {
////            val image = InputImage.fromBitmap(bitmap, 0)
//
//            val options = BarcodeScannerOptions.Builder()
//                .setBarcodeFormats(Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
//                .build()
//
//            val scanner = BarcodeScanning.getClient(options)
//
//            try{
//                val barCodes = processImageBarCode(inputImage, scanner)
//                processBarCodes(barCodes)
//            }catch (e: Exception){
//                _scannerStatus.value = Event(ScannerStatus.FAIL)
//            }
//        }
//    }

//    private fun runTextRecognition(inputImage: InputImage){
//        viewModelScope.launch {
////            val image = InputImage.fromBitmap(i, 0)
//            val recognizer = TextRecognition.getClient()
//            try {
//                val text = processImageText(inputImage, recognizer)
//                processTextRecognitionResult(text)
//            }catch (e: Exception){
//                _scannerStatus.value = Event(ScannerStatus.FAIL)
//            }
//        }
//    }

//    private suspend fun processImageBarCode(inputImage: InputImage, scanner: BarcodeScanner):
//            List<Barcode> = withContext(Dispatchers.Default){
//        return@withContext scanner.process(inputImage).await()
//    }
//
//    private suspend fun processImageText(inputImage: InputImage, recognizer: TextRecognizer):
//            Text = withContext(Dispatchers.Default) {
//        return@withContext recognizer.process(inputImage).await()
//    }
//
//    private fun processBarCodes(barCodes: List<Barcode>){
//        if(barCodes.isEmpty()){
//            _scannerStatus.value = Event(ScannerStatus.TRY_AGAIN)
//        }else{
//            val barCode: String? = barCodes[0].displayValue
//            barCode?.let {
//                getProduct(it, TypeScanner.UPC)
//            }
//        }
//    }

//    private fun processTextRecognitionResult(texts: Text){
//        val blocks = texts.textBlocks
//        if(blocks.isEmpty()){
//            _scannerStatus.value = Event(ScannerStatus.TRY_AGAIN)
//        }else{
//            val skuCode = findSKUCode(texts.text)
//            if (skuCode == null){
//                _scannerStatus.value = Event(ScannerStatus.TRY_AGAIN)
//            }else{
//                getProduct(skuCode, TypeScanner.SKU)
//            }
//        }
//    }


    private fun getProduct(code: String, typeScanner: TypeScanner) : LiveData<DomainProduct>{
        // TODO - Use live data builder
        val result = MutableLiveData<DomainProduct>()
        viewModelScope.launch {
            when(typeScanner){
                TypeScanner.SKU -> {
                    val findResult = repository.findBySKU(code)
                    if(findResult is Result.Success){
                        result.value = findResult.data
                    }else{
                        result.value = null
                    }
                }
                TypeScanner.UPC -> {
                    val findResult = repository.findByUPC(code)
                    if(findResult is Result.Success){
                        result.value = findResult.data
                    }else{
                        result.value = null
                    }
                }
            }
        }
        return result
    }

    private fun checkResult(result: Result<DomainProduct>){
        if (result is Result.Success){
            _productByCode.value = Event(result.data)
        }else{
            _scannerStatusItem.value = Event(ScannerStatusItem.FOUND)
        }
    }

//    fun hideButtons(){
//        _btnVisibility.value = View.GONE
//    }
//
//    fun showButtons(){
//        _btnVisibility.value = View.VISIBLE
//    }
}
