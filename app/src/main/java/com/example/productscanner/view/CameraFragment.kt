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
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult

class CameraFragment : Fragment() {

    private lateinit var viewModel: CameraViewModel
    private lateinit var viewModelShared: MainActivityViewModel
    private lateinit var binding: CameraFragmentBinding

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
            binding.cameraView.takePicture()
            binding.btnScanUpc.visibility = View.GONE
        }

        binding.cameraView.addCameraListener(object: CameraListener(){
            @Override
            override fun onPictureTaken(result: PictureResult) {
                Log.d("Camera View", "Picture taken")
                val bitmap = BitmapFactory.decodeByteArray(result.data, 0, result.data.size)
                binding.imagePreview.setImageBitmap(bitmap)
                showPreview()
                runBarcodeScanner(bitmap)
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
        // Create a FirebaseVisionImage
        val image = FirebaseVisionImage.fromBitmap(bitmap)

        // Optional: Define what kind of barcodes to scan
        val options = FirebaseVisionBarcodeDetectorOptions.Builder()
            .setBarcodeFormats(
                // Detect all kind of barcodes
                FirebaseVisionBarcode.FORMAT_ALL_FORMATS
            ).build()

        // Get access to an instance of FirebaseBarcodeDetector
        val detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options)

        // Use the detector to detect the labels inside the image
        detector.detectInImage(image)
            .addOnSuccessListener{
                // Task completed successfully
                if(it.isNullOrEmpty()){
                    // TODO validate when the fragment is null
                    Toast.makeText(
                        activity, "Try again", Toast.LENGTH_SHORT
                    ).show()
                    hidePreview()
                }else{
                    val barCode: String? = it[0].displayValue
                    barCode?.let {
                        viewModel.getProductByBarCode(it)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(context,
                    "Sorry, something went wrong!",
                    Toast.LENGTH_LONG).show()
                hidePreview()
            }
    }

    fun showPreview() {
        binding.imagePreview.visibility = View.VISIBLE
        binding.cameraView.visibility = View.GONE
    }

    fun hidePreview() {
        binding.imagePreview.visibility = View.GONE
        binding.cameraView.visibility = View.VISIBLE
        binding.btnScanUpc.visibility = View.VISIBLE
    }
}
