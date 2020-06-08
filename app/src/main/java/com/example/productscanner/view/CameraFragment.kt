package com.example.productscanner.view

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

import com.example.productscanner.R
import com.example.productscanner.databinding.CameraFragmentBinding
import com.example.productscanner.viewmodel.CameraViewModel
import com.example.productscanner.viewmodel.MainActivityViewModel
import com.example.productscanner.viewmodel.ScannerStatus
import com.example.productscanner.viewmodel.TypeScanner
import com.google.mlkit.vision.barcode.Barcode
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult

class CameraFragment : Fragment() {

    private lateinit var viewModel: CameraViewModel
    private lateinit var viewModelShared: MainActivityViewModel
    private lateinit var binding: CameraFragmentBinding
    private val sufix:String = "SKU-"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = CameraFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.cameraView.setLifecycleOwner(viewLifecycleOwner)

        viewModelShared = (activity as MainActivity).viewModel
        viewModel = ViewModelProviders.of(this).get(CameraViewModel::class.java)
        viewModel.setProducts(viewModelShared.products)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.scanner)

        hidePreview()

        binding.btnScanUpc.setOnClickListener {
            viewModel.typeScanner = TypeScanner.UPC
            binding.cameraView.takePicture()
            hideButtons()
        }

        binding.btnScanSku.setOnClickListener {
            viewModel.typeScanner = TypeScanner.SKU
            binding.cameraView.takePicture()
            hideButtons()
        }

        binding.cameraView.addCameraListener(object: CameraListener(){
            @Override
            override fun onPictureTaken(result: PictureResult) {
                Log.d("Camera View", "Picture taken")
                val bitmap = BitmapFactory.decodeByteArray(result.data, 0, result.data.size)
                binding.imagePreview.setImageBitmap(bitmap)
                showPreview()

                when(viewModel.typeScanner){
                    TypeScanner.UPC -> {
                        runBarcodeScanner(bitmap)
                    }
                    TypeScanner.SKU -> {
                        textRecognition(bitmap)
                    }
                }
            }
        })

        setObservers()

        return binding.root
    }

    private fun setObservers(){
        viewModel.scannerStatus.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it){
                    ScannerStatus.FOUND -> {
                        viewModel.productByBarCode?.let { product ->
                            this.findNavController()
                                .navigate(CameraFragmentDirections
                                    .actionCameraxToDetailProduct(product))
                        }
                        viewModel.displayBarCodeToDetailComplete()
                    }
                    ScannerStatus.NOT_FOUND -> {
                        Toast.makeText(context, "Item not found",Toast.LENGTH_LONG).show()
                        hidePreview()
                        viewModel.displayBarCodeToDetailComplete()
                    }
                }
            }
        })
    }

    fun runBarcodeScanner(bitmap: Bitmap){
        val image = InputImage.fromBitmap(bitmap, 0)

        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
            .build()

        val scanner = BarcodeScanning.getClient(options)

        scanner.process(image)
            .addOnSuccessListener {
                processBarcodes(it)
            }
            .addOnFailureListener {
                onFail()
            }
    }

    private fun processBarcodes(barcodes: List<Barcode>){
        if(barcodes.isEmpty()){
            tryAgain()
        }else{
            val barCode: String? = barcodes[0].displayValue
            barCode?.let {
                viewModel.getProduct(it)
            }
        }
    }

    fun textRecognition(bitmap: Bitmap){
        val image = InputImage.fromBitmap(bitmap, 0)
        val recognizer = TextRecognition.getClient()

        recognizer.process(image)
            .addOnSuccessListener { texts ->
                processTextRecognitionResult(texts)
            }
            .addOnFailureListener {
                onFail()
            }
    }

    private fun onFail() {
        Toast.makeText(
            context,
            "Sorry, something went wrong!",
            Toast.LENGTH_LONG
        ).show()
        hidePreview()
    }

    private fun processTextRecognitionResult(texts: Text){
        val blocks = texts.textBlocks
        if(blocks.isEmpty()){
            tryAgain()
        }else{
            val skuCode = findSKUCode(texts)
            if (skuCode == null){
                tryAgain()
            }else{
                viewModel.getProduct(skuCode)
            }
        }
    }

    private fun tryAgain() {
        Toast.makeText(
            activity, "Try again", Toast.LENGTH_SHORT
        ).show()
        hidePreview()
    }

    private fun findSKUCode(texts: Text):String?{
        val lines = texts.text.split("\n")
        for (line in lines){
            val words = line.split(" ")
            for (word in words){
                if (word.contains(sufix))
                    return word
            }
        }
        return null
    }


    fun hideButtons(){
        binding.btnScanSku.visibility = View.GONE
        binding.btnScanUpc.visibility = View.GONE
    }

    fun showButtons(){
        binding.btnScanSku.visibility = View.VISIBLE
        binding.btnScanUpc.visibility = View.VISIBLE
    }

    fun showPreview() {
        binding.imagePreview.visibility = View.VISIBLE
        binding.cameraView.visibility = View.GONE
    }

    fun hidePreview() {
        binding.imagePreview.visibility = View.GONE
        binding.cameraView.visibility = View.VISIBLE
        showButtons()
    }
}
