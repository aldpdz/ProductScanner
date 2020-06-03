package com.example.productscanner.view

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import com.example.productscanner.R
import com.example.productscanner.databinding.FragmentMainBinding
import com.example.productscanner.viewmodel.MainFragmentViewModel
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var binding : FragmentMainBinding
    private lateinit var adapter: ProductAdapter
    private lateinit var viewModel: MainFragmentViewModel
    private lateinit var searchView: SearchView

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
        viewModel.productsFiltered.observe(viewLifecycleOwner, Observer { productsList ->
            productsList?.let {
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
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
//        searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener{
//            override fun onQueryTextSubmit(p0: String?): Boolean {
//                viewModel.queryProducts(p0?.toLowerCase(Locale.getDefault()))
//                return false
//            }
//
//            override fun onQueryTextChange(p0: String?): Boolean {
//                viewModel.queryProducts(p0?.toLowerCase(Locale.getDefault()))
//                return false
//            }
//        })
        searchView.setOnQueryTextListener(this)
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
            R.id.action_search -> {
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    override fun onQueryTextSubmit(p0: String?): Boolean {
        viewModel.queryProducts(p0?.toLowerCase(Locale.getDefault()))
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        viewModel.queryProducts(p0?.toLowerCase(Locale.getDefault()))
        return false
    }
}

