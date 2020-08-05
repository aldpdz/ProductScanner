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
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.example.productscanner.R
import com.example.productscanner.databinding.FragmentMainBinding
import com.example.productscanner.viewmodel.SharedViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.*

/**
 * A simple [Fragment] subclass.
 */
@AndroidEntryPoint
class MainFragment : Fragment(), SearchView.OnQueryTextListener {

    private lateinit var binding : FragmentMainBinding
    private lateinit var adapter: ProductAdapter
    private lateinit var searchView: SearchView
    private val sharedViewModel by viewModels<SharedViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMainBinding.inflate(inflater)
        binding.viewModel = sharedViewModel

        adapter = ProductAdapter(OpenProductListener { product ->
            sharedViewModel.displayNavigationToDetail(product)
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
        sharedViewModel.productsError.observe(viewLifecycleOwner, Observer { error ->
            error.getContentIfNotHandled()?.let{
                Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            }
        })
        sharedViewModel.navigationToDetail.observe(viewLifecycleOwner, Observer {
            it.getContentIfNotHandled()?.let {product ->
                // Save query otherwise is set to "" when changing the views (shared same toolbar)
                sharedViewModel.saveQuery()
                this.findNavController()
                    .navigate(MainFragmentDirections
                        .actionMainFragmentToDetailProduct(product))
            }
        })
        // TODO - maybe use flow for loadIdsFromPreferences to filterProducts
        sharedViewModel.products.observe(viewLifecycleOwner, Observer {
            it?.let{
                sharedViewModel.filterProducts()
            }
        })
        sharedViewModel.productsFiltered.observe(viewLifecycleOwner, Observer { productsList ->
            productsList?.let {
                adapter.submitList(productsList)
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
            R.id.scan -> {
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToCamerax())
                true
            }
            R.id.refresh -> {
                sharedViewModel.refreshData()
                true
            }
            R.id.action_search -> {
                true
            }
            R.id.settings -> {
                this.findNavController().navigate(MainFragmentDirections.actionMainFragmentToSettingsFragment())
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
                NotificationManager.IMPORTANCE_DEFAULT)
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
        sharedViewModel.queryProducts(p0?.toLowerCase(Locale.getDefault()))
        return false
    }

    override fun onQueryTextChange(p0: String?): Boolean {
        sharedViewModel.queryProducts(p0?.toLowerCase(Locale.getDefault()))
        return false
    }
}

