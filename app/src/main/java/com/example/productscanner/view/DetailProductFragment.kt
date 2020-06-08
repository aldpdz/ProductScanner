package com.example.productscanner.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import com.example.productscanner.R
import com.example.productscanner.databinding.FragmentDetailProductBinding
import com.example.productscanner.model.Product
import com.example.productscanner.viewmodel.DetailProductViewModel
import com.example.productscanner.viewmodel.MainActivityViewModel

/**
 * A simple [Fragment] subclass.
 */
class DetailProductFragment : Fragment() {

    private lateinit var viewModel: DetailProductViewModel
    private lateinit var viewModelShared: MainActivityViewModel
    private lateinit var binding: FragmentDetailProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDetailProductBinding.inflate(inflater)

        // TODO remove args
        val detailProduct = arguments?.let { DetailProductFragmentArgs.fromBundle(it).argProduct }
        viewModelShared = (activity as (MainActivity)).viewModel

        viewModel = ViewModelProviders.of(this).get(DetailProductViewModel::class.java)
        viewModel.setDetailProduct(detailProduct)

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        setUpdateBtn(detailProduct)

        (activity as AppCompatActivity).supportActionBar?.title = getString(R.string.product_detail)

        return binding.root
    }

    private fun setUpdateBtn(detailProduct: Product?) {
        binding.btnUpdate.setOnClickListener {
            val price = binding.etPrice.text.toString()
            val quantity = binding.etQuantity.text.toString()

            if (price.isEmpty() || quantity.isEmpty()) {
                Toast.makeText(context, R.string.invalid_input, Toast.LENGTH_LONG).show()
            } else {
                val product: Product? = detailProduct?.copy(quantity =  quantity.toInt(), price = price.toFloat())
                if(product != detailProduct){ // if there are changes in the product
                    viewModel.sendNotification(product)
                    viewModelShared.updateProduct(product)
                }
            }
        }
    }
}
