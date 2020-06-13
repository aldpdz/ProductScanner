package com.example.productscanner.view

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
import com.example.productscanner.viewmodel.*
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
        binding.viewModel = viewModel

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.scanner)

        hidePreview()

        binding.btnScanUpc.setOnClickListener {
            viewModel.typeScanner = TypeScanner.UPC
            binding.cameraView.takePicture()
            viewModel.hideButtons()
        }

        binding.btnScanSku.setOnClickListener {
            viewModel.typeScanner = TypeScanner.SKU
            binding.cameraView.takePicture()
            viewModel.hideButtons()
        }

        binding.cameraView.addCameraListener(object: CameraListener(){
            @Override
            override fun onPictureTaken(result: PictureResult) {
                Log.d("Camera View", "Picture taken")
                // TODO send it to a coroutine
//                val bitmap = BitmapFactory.decodeByteArray(result.data, 0, result.data.size)
                viewModel.getBitmap(result.data)

                // TODO observe change when the bitmap is ready
//                binding.imagePreview.setImageBitmap(bitmap)
//                showPreview()

//                when(viewModel.typeScanner){
//                    TypeScanner.UPC -> {
//                        viewModel.runBarcodeScanner(bitmap)
//                    }
//                    TypeScanner.SKU -> {
//                        viewModel.textRecognition(bitmap)
//                    }
//                }
            }
        })

        setObservers()

        return binding.root
    }

    private fun setObservers(){
        // The scanner was sucessful
        viewModel.scannerStatusItem.observe(viewLifecycleOwner, Observer {
            it?.let {
                when(it){
                    ScannerStatusItem.FOUND -> {
                        viewModel.productByBarCode?.let { product ->
                            this.findNavController()
                                .navigate(CameraFragmentDirections
                                    .actionCameraxToDetailProduct(product))
                        }
                        viewModel.displayBarCodeToDetailComplete()
                    }
                    ScannerStatusItem.NOT_FOUND -> {
                        Toast.makeText(context, "Item not found",Toast.LENGTH_LONG).show()
                        hidePreview()
                        viewModel.displayBarCodeToDetailComplete()
                    }
                }
            }
        })

        // If the scanner fail
        viewModel.scannerStatus.observe(viewLifecycleOwner, Observer {
            it?.let{
                hidePreview()
                when(it){
                    ScannerStatus.TRY_AGAIN -> {
                        tryAgain()
                    }
                    ScannerStatus.FAIL -> {
                        onFail()
                    }
                }
                viewModel.scannerStatusFailComplete()
            }
        })

        viewModel.bitMap.observe(viewLifecycleOwner, Observer {
            it?.let {
                binding.imagePreview.setImageBitmap(it)
                showPreview()
            }
        })
    }

    private fun onFail() {
        Toast.makeText(
            context,
            "Sorry, something went wrong!",
            Toast.LENGTH_LONG
        ).show()
        hidePreview()
    }

    private fun tryAgain() {
        Toast.makeText(
            activity, "Try again", Toast.LENGTH_SHORT
        ).show()
        hidePreview()
    }

    fun showPreview() {
        binding.imagePreview.visibility = View.VISIBLE
        binding.cameraView.visibility = View.GONE
    }

    private fun hidePreview() {
        binding.imagePreview.visibility = View.GONE
        binding.cameraView.visibility = View.VISIBLE
        viewModel.showButtons()
    }
}
