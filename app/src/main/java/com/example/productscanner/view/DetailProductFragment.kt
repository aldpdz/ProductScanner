package com.example.productscanner.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.VisibleForTesting
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.productscanner.R
import com.example.productscanner.databinding.FragmentDetailProductBinding
import com.example.productscanner.model.Product
import com.example.productscanner.viewmodel.DetailProductViewModel
import com.example.productscanner.viewmodel.MainActivityViewModel

/**
 * A simple [Fragment] subclass.
 */
class DetailProductFragment : Fragment() {

    private val viewModel by viewModels<DetailProductViewModel>()
    // TODO reuse activityViewModels after DI with Hilt
    // TODO verify the use of activityViewModel instead of viewModels
    private val viewModelShared by activityViewModels<MainActivityViewModel>()

    private lateinit var binding: FragmentDetailProductBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentDetailProductBinding.inflate(inflater)

        val detailProduct = arguments?.let { DetailProductFragmentArgs.fromBundle(it).argProduct }
        viewModel.setDetailProduct(detailProduct)

        detailProduct?.let {
            if(!detailProduct.isSaved){
                viewModelShared.saveIdProduct(activity as MainActivity, it.id)
            }
        }

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
                    this.findNavController()
                        .navigate(DetailProductFragmentDirections
                            .actionDetailProductToMainFragment())
                }
            }
        }
    }
}
