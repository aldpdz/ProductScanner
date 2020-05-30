package com.example.productscanner

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.navArgs
import com.example.productscanner.databinding.FragmentDetailProductBinding

/**
 * A simple [Fragment] subclass.
 */
class DetailProduct : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentDetailProductBinding= DataBindingUtil.inflate(
            inflater, R.layout.fragment_detail_product, container, false
        )

        val args = DetailProductArgs.fromBundle(requireArguments())

        binding.tv.text = "${binding.tv.text} with arguments ${args}"

        (activity as AppCompatActivity).supportActionBar?.title =
            "Producto"

        return binding.root
    }

}
