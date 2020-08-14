package com.example.productscanner.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.util.Size
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer

import com.example.productscanner.R
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.databinding.CameraFragmentBinding
import com.example.productscanner.viewmodel.*
import com.google.common.util.concurrent.ListenableFuture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.camera_fragment.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class CameraFragment : Fragment() {

    private val viewModel by viewModels<CameraViewModel>()
    private lateinit var binding: CameraFragmentBinding

    private lateinit var cameraProviderFuture : ListenableFuture<ProcessCameraProvider>
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var imageAnalyzer: ImageAnalysis

    private var productBottomDialogFragment: ProductBottomDialogFragment? = null

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

        binding.viewModel = viewModel

        // Request camera permissions
        if (allPermissionsGranted()){
            cameraProviderFuture = ProcessCameraProvider.getInstance(activity as MainActivity)
            startCamera()
        }else{
            ActivityCompat.requestPermissions(activity as MainActivity,
                REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        cameraExecutor = Executors.newSingleThreadExecutor()
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
                cameraProviderFuture.get().unbindAll()
                if (productBottomDialogFragment == null){
                    newBottomSheet(it)
                } else{
                    if (!productBottomDialogFragment!!.isVisible){
                        newBottomSheet(it)
                    }
                }
            }
        })

        viewModel.productUPC.observe(viewLifecycleOwner, Observer {
            it?.let {
                Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.bottomSheetPaused.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let { isPaused ->
                if(isPaused){
                    startCamera()
                }
            }
        })
    }

    private fun newBottomSheet(product: DomainProduct) {
        productBottomDialogFragment =
            ProductBottomDialogFragment(viewModel.bottomSheetPaused, product)
        productBottomDialogFragment?.let {
            it.show((activity as MainActivity).supportFragmentManager, it.tag)
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission((activity as MainActivity).baseContext,
        it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        cameraProviderFuture = ProcessCameraProvider.getInstance((activity as MainActivity))

        cameraProviderFuture.addListener(Runnable {
            // Used to bind the lifecycle of camera to the lifecycle owner
            val cameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.createSurfaceProvider())
                }

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .setTargetResolution(Size(480, 640))
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

    private fun onFail() {
        // TODO add to string resources
        Toast.makeText(
            context,
            "Sorry, something went wrong!",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun tryAgain() {
        // TODO add to string resources
        Toast.makeText(
            activity, "Try again", Toast.LENGTH_SHORT
        ).show()
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
}
