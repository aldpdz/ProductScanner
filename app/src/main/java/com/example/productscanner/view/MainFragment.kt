package com.example.productscanner.view

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.productscanner.R
import com.example.productscanner.databinding.FragmentMainBinding
import com.example.productscanner.model.Product
import com.example.productscanner.viewmodel.MainFragmentViewModel
import kotlinx.android.synthetic.main.fragment_main.*

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment() {

    private lateinit var binding : FragmentMainBinding
    private lateinit var adapter: ProductAdapter
    private lateinit var viewModel: MainFragmentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater)
        viewModel = ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)
        binding.viewModel = viewModel

        adapter = ProductAdapter(OpenProductListener { product ->
            viewModel.displayNavigationToDetail(product)
        })

        binding.rvProducts.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner // necessary to update values with bindingAdapter

        setHasOptionsMenu(true)
        setObservers()

        return binding.root
    }

    private fun setObservers() {
        viewModel.products.observe(viewLifecycleOwner, Observer { productsList ->
            productsList?.let {
                binding.rvProducts.visibility = View.VISIBLE
                adapter.submitList(productsList)
            }
        })
        viewModel.productsError.observe(viewLifecycleOwner, Observer { error ->
            error?.let{
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.navigationToDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToDetailProduct(5, it))
                viewModel.displayNavigationToDetailComplete()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.detail -> {
                Toast.makeText(context, "Scan", Toast.LENGTH_SHORT).show()
                true
            }
            R.id.refresh -> {
                viewModel.refreshData()
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }
}
