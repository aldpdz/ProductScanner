package com.example.productscanner.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController

import com.example.productscanner.R
import com.example.productscanner.databinding.CameraFragmentBinding
import com.example.productscanner.viewmodel.*
//import com.otaliastudios.cameraview.CameraListener
//import com.otaliastudios.cameraview.PictureResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.camera_fragment.*
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private val viewModel by viewModels<CameraViewModel>()
    private val shareViewModel by activityViewModels<SharedViewModel>()
    private lateinit var binding: CameraFragmentBinding

    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview
    private lateinit var imageAnalyzer: ImageAnalysis

    companion object {
        private const val TAG = "CameraXBasic"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.scanner)

        binding = CameraFragmentBinding.inflate(inflater)
        binding.lifecycleOwner = viewLifecycleOwner
//        binding.cameraView.setLifecycleOwner(viewLifecycleOwner)

        binding.viewModel = viewModel

        // Request camera permissions
        if (allPermissionsGranted()){
            startCamera()
        }else{
            ActivityCompat.requestPermissions(activity as MainActivity,
                REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()

//        hidePreview()

//        binding.btnScanUpc.setOnClickListener {
//            viewModel.typeScanner = TypeScanner.UPC
//            binding.cameraView.takePicture()
//            viewModel.hideButtons()
//        }
//
//        binding.btnScanSku.setOnClickListener {
//            viewModel.typeScanner = TypeScanner.SKU
//            binding.cameraView.takePicture()
//            viewModel.hideButtons()
//        }
//
//        binding.cameraView.addCameraListener(object: CameraListener(){
//            @Override
//            override fun onPictureTaken(result: PictureResult) {
//                Log.d("Camera View", "Picture taken")
//                viewModel.processInputImage(result.data)
//
//            }
//        })

        setObservers()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun setObservers(){

        viewModel.productSKU.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.productUPC.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()
            }
        })

//        viewModel.cameraStatus.observe(viewLifecycleOwner, Observer {
//            it.getContentIfNotHandled()?.let {cameraStatus ->
//                when(cameraStatus){
//                    CameraStatus.START -> {
//                        Log.d(TAG, "Restart processing")
//                        reBind()
//                    }
//                    CameraStatus.STOP -> {
//                        Log.d(TAG, "Stop processing")
//                        cameraProvider.unbind(imageAnalyzer)
//                        cameraProvider.unbind(preview)
//                    }
//                }
//            }
//        })

        // The scanner was successful
//        viewModel.scannerStatusItem.observe(viewLifecycleOwner, Observer {
//            it.getContentIfNotHandled()?.let {scannerStatusItem ->
//                // TODO add to string resources
//                Toast.makeText(context, "Item not found",Toast.LENGTH_LONG).show()
////                hidePreview()
//            }
//        })

//        viewModel.productByCode.observe(viewLifecycleOwner, Observer { it ->
//            it.getContentIfNotHandled()?.let{product ->
//                this.findNavController()
//                    .navigate(CameraFragmentDirections
//                        .actionCameraxToDetailProduct(product))
//            }
//        })

        // If the scanner fail
//        viewModel.scannerStatus.observe(viewLifecycleOwner, Observer {
//            it.getContentIfNotHandled()?.let { scannerStatus ->
//                when(scannerStatus){
//                    ScannerStatus.TRY_AGAIN -> {
//                        tryAgain()
//                    }
//                    ScannerStatus.FAIL -> {
//                        onFail()
//                    }
//                }
//            }
//        })

//        viewModel.bitMap.observe(viewLifecycleOwner, Observer {
//            it?.let {
//                binding.imagePreview.setImageBitmap(it)
//                showPreview()
//            }
//        })
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission((activity as MainActivity).baseContext,
        it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance((activity as MainActivity))

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of camera to the lifecycle owner
            cameraProvider = cameraProviderFuture.get()

            // Preview
            preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

//            imageCapture = ImageCapture.Builder()
//                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, ScannerAnalyzer(
                        viewModel.cameraStatus, viewModel.sku, viewModel.upc))
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner, cameraSelector, preview, imageAnalyzer)
            }catch (e: Exception){
                Log.e(TAG, "Use case binding filed", e)
            }
        }, ContextCompat.getMainExecutor(activity))
    }

    private fun reBind(){
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        cameraProvider.bindToLifecycle(viewLifecycleOwner, cameraSelector, preview)
    }

    private fun onFail() {
        // TODO add to string resources
        Toast.makeText(
            context,
            "Sorry, something went wrong!",
            Toast.LENGTH_LONG
        ).show()
//        hidePreview()
    }

    private fun tryAgain() {
        // TODO add to string resources
        Toast.makeText(
            activity, "Try again", Toast.LENGTH_SHORT
        ).show()
//        hidePreview()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()){
                startCamera()
            }else {
                Toast.makeText(context,
                "Permissions not granted by the user.",
                Toast.LENGTH_SHORT).show()
            }
        }
    }

    //    private fun showPreview() {
//        binding.imagePreview.visibility = View.VISIBLE
//        binding.cameraView.visibility = View.GONE
//    }
//
//    private fun hidePreview() {
//        binding.imagePreview.visibility = View.GONE
//        binding.cameraView.visibility = View.VISIBLE
//        viewModel.showButtons()
//    }
}
