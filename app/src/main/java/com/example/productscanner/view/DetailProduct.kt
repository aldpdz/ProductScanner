package com.example.productscanner.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.productscanner.databinding.FragmentDetailProductBinding
import com.example.productscanner.model.Product
import com.example.productscanner.viewmodel.DetailProductViewModel
import com.example.productscanner.viewmodel.MainActivityViewModel

/**
 * A simple [Fragment] subclass.
 */
class DetailProduct : Fragment() {

    private lateinit var viewModel: DetailProductViewModel
    private lateinit var viewModelShared: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding = FragmentDetailProductBinding.inflate(inflater)

        // TODO remove args
        val detailProduct = arguments?.let { DetailProductArgs.fromBundle(it).argProduct }
        viewModelShared = (activity as (MainActivity)).viewModel
        viewModel = ViewModelProviders.of(this).get(DetailProductViewModel::class.java)
        viewModel.setDetailProduct(detailProduct)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnUpdate.setOnClickListener {
            val product: Product? = detailProduct
            product?.quantity = binding.etQuantity.text.toString().toInt()
            product?.price = binding.etPrice.text.toString().toFloat()
            viewModelShared.updateProduct(detailProduct)
        }

        (activity as AppCompatActivity).supportActionBar?.title = "Producto"

        return binding.root
    }

}
