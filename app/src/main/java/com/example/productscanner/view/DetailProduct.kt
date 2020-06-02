package com.example.productscanner.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.productscanner.databinding.FragmentDetailProductBinding

/**
 * A simple [Fragment] subclass.
 */
class DetailProduct : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentDetailProductBinding.inflate(inflater)

        // TODO remove args
        val args = arguments?.let { DetailProductArgs.fromBundle(it).passingValue }
        var detailProduct = arguments?.let { DetailProductArgs.fromBundle(it).argProduct }

        binding.viewModel = detailProduct

        (activity as AppCompatActivity).supportActionBar?.title = "Producto"

        return binding.root
    }

}
