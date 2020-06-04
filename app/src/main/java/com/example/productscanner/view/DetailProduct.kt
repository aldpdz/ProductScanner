package com.example.productscanner.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.productscanner.databinding.FragmentDetailProductBinding
import com.example.productscanner.viewmodel.DetailProductViewModel

/**
 * A simple [Fragment] subclass.
 */
class DetailProduct : Fragment() {

    private lateinit var viewModel: DetailProductViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentDetailProductBinding.inflate(inflater)

        // TODO remove args
        var detailProduct = arguments?.let { DetailProductArgs.fromBundle(it).argProduct }
        viewModel = ViewModelProviders.of(this).get(DetailProductViewModel::class.java)
        viewModel.setDetailProduct(detailProduct)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        (activity as AppCompatActivity).supportActionBar?.title = "Producto"

        return binding.root
    }

}
