package com.example.productscanner.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.productscanner.R
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.databinding.FragmentDetailProductBinding
import com.example.productscanner.viewmodel.DetailProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailProductFragment : Fragment() {

    private val viewModel by viewModels<DetailProductViewModel>()
    private lateinit var binding: FragmentDetailProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDetailProductBinding.inflate(inflater)

        val detailProduct = arguments?.let { DetailProductFragmentArgs.fromBundle(it).argProduct }
        viewModel.setDetailProduct(detailProduct)

        detailProduct?.let {
            if(!it.isSaved){
                viewModel.saveIdProduct(it.id)
            }
        }

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setUpdateBtn(detailProduct)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.product_detail)

        return binding.root
    }

    private fun setUpdateBtn(product: DomainProduct?) {
        binding.btnUpdate.setOnClickListener {
            val price = binding.etPrice.text.toString()
            val quantity = binding.etQuantity.text.toString()

            if (price.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(context, R.string.invalid_input, Toast.LENGTH_LONG).show()
            } else {
                val updatedProduct: DomainProduct? = product?.copy(quantity =  quantity.toInt(), price = price.toFloat())
                if(updatedProduct != product){ // if there are changes in the product
                    viewModel.sendNotification(updatedProduct)
                    updatedProduct?.let {viewModel.updateProduct(it)}
                    this.findNavController().popBackStack(R.id.mainFragment, false)
                }
            }
        }
    }
}
