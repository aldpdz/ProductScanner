package com.example.productscanner.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.lifecycle.MutableLiveData
import com.example.productscanner.util.Event
import com.google.android.gms.tasks.Task
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition

private val regexOption: RegexOption = RegexOption.IGNORE_CASE
private val skuCodeRegex: Regex = "SKU-code_\\d\\d".toRegex(regexOption)

/***
 * Find the first instance of a skuCode
 */
fun findSKUCode(texts: String):String?{
    return skuCodeRegex.find(texts)?.value
}

/***
 * Class to process the camera frames
 */
class ScannerAnalyzer(
    private val cameraStatus: MutableLiveData<Event<CameraStatus>>,
    private val sku: MutableLiveData<String>,
    private val upc: MutableLiveData<String>)
    : ImageAnalysis.Analyzer {

    companion object {
        private const val TAG = "ScannerAnalyzer"
    }

    @SuppressLint("UnsafeExperimentalUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if(mediaImage != null){
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
//            runBarcodeScanner(image)
            runTextRecognition(image).addOnCompleteListener {
                imageProxy.close()
            }
        }
    }

    private fun runBarcodeScanner(image: InputImage) : Task<List<Barcode>> {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_UPC_A, Barcode.FORMAT_UPC_E)
            .build()

        val scanner = BarcodeScanning.getClient(options)

        return scanner.process(image)
            .addOnSuccessListener {
                processBarCodes(it)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "barcode scanner error", exception)
            }
    }

    private fun runTextRecognition(inputImage: InputImage) : Task<Text>{
        val recognizer = TextRecognition.getClient()
        return recognizer.process(inputImage)
            .addOnSuccessListener {
                processTextRecognitionResult(it)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "text recognition error", exception)
            }
    }

    private fun processBarCodes(barCodes: List<Barcode>){
        if(barCodes.isEmpty()){
            Log.d(TAG, "Empty list BarCodes")
        }else{
            val barCode: String? = barCodes[0].displayValue
            barCode?.let {
                Log.d(TAG, it)
                upc.value = it
            }
        }
    }

    private fun processTextRecognitionResult(texts: Text){
        val blocks = texts.textBlocks
        if(blocks.isEmpty()){
            Log.d(TAG, "Empty list Text Recognition")
        }else{
            Log.d(TAG, texts.text)
            val skuCode = findSKUCode(texts.text)
            skuCode?.let { sku.value = it }
        }
    }
}