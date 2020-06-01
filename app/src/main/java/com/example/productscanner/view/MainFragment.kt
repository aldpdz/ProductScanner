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
        binding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_main, container, false)
        viewModel = ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)

        adapter = ProductAdapter(OpenProductListener { product ->
//            findNavController().navigate(MainFragmentDirections.actionMainFragmentToDetailProduct(5))
            viewModel.displayNavigationToDetail(product)
        })
        binding.rvProducts.adapter = adapter

//        binding.button.setOnClickListener { view: View ->
////            view.findNavController().navigate(R.id.action_mainFragment_to_detailProduct)
//            view.findNavController().navigate(
//                MainFragmentDirections.actionMainFragmentToDetailProduct(
//                    5
//                )
//            )
//        }
//
//        binding.button.setOnClickListener(
//            Navigation.createNavigateOnClickListener(R.id.action_mainFragment_to_detailProduct)
//        )
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
        viewModel.loading.observe(viewLifecycleOwner, Observer { isLoading ->
            isLoading?.let {
                progressBar.visibility = if(it) View.VISIBLE else View.GONE
                if(it){
                    rv_products.visibility = View.GONE
                }
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
