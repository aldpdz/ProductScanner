package com.example.productscanner.view

import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import android.os.Build
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
import com.example.productscanner.viewmodel.MainActivityViewModel
import com.example.productscanner.viewmodel.MainFragmentViewModel
import com.example.productscanner.viewmodel.ProductApiStatus
import com.example.productscanner.viewmodel.ScannerStatus
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
class MainFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var binding : FragmentMainBinding
    private lateinit var adapter: ProductAdapter
    private lateinit var viewModel: MainFragmentViewModel
    private lateinit var searchView: SearchView
    private lateinit var viewModelShared: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater)
        viewModelShared = (activity as MainActivity).viewModel
        viewModel = ViewModelProviders.of(this).get(MainFragmentViewModel::class.java)
        viewModel.setResponseData(viewModelShared.productsError, viewModelShared.status, viewModelShared.products)
        binding.viewModel = viewModel

        adapter = ProductAdapter(OpenProductListener { product ->
            viewModel.displayNavigationToDetail(product)
        })

        binding.rvProducts.adapter = adapter
        binding.lifecycleOwner = viewLifecycleOwner // necessary to update values with bindingAdapter

        setHasOptionsMenu(true)
        setObservers()

        createChannel(
            getString(R.string.product_notification_channel_id),
            getString(R.string.notification_channel_name))

        return binding.root
    }

    private fun setObservers() {
        viewModel.productsFiltered.observe(viewLifecycleOwner, Observer { productsList ->
            productsList?.let {
                adapter.submitList(productsList)
            }
        })
        viewModel.productsError?.observe(viewLifecycleOwner, Observer { error ->
            error?.let{
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })
        viewModel.navigationToDetail.observe(viewLifecycleOwner, Observer {
            it?.let {
                this.findNavController()
                    .navigate(MainFragmentDirections
                        .actionMainFragmentToDetailProduct(it))
                viewModel.displayNavigationToDetailComplete()
            }
        })
        viewModel.status?.observe(viewLifecycleOwner, Observer {
            it?.let {
                if(it == ProductApiStatus.DONE){
                    viewModel.filterProducts()
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView
        searchView.setOnQueryTextListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId){
            R.id.detail -> {
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToCamerax())
                true
            }
            R.id.refresh -> {
                viewModelShared.refreshData()
                true
            }
            R.id.action_search -> {
                true
            }
            else -> return super.onOptionsItemSelected(item)
        }

    }

    private fun createChannel(channelId: String, channelName: String){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,
                NotificationManager.IMPORTANCE_LOW)
                .apply {
                    setShowBadge(false)
                }

            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = getString(R.string.notification_channel_description)

            val notificationManager = requireActivity().getSystemService(
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(notificationChannel)
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

