package com.example.productscanner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.data.network.NetworkProduct
import com.example.productscanner.util.Event

class MainFragmentViewModel: ViewModel() {
    private val _navigationToDetail = MutableLiveData<Event<NetworkProduct>>()
    val navigationToDetail: LiveData<Event<NetworkProduct>> get() = _navigationToDetail

    fun displayNavigationToDetail(networkProduct: NetworkProduct){
        _navigationToDetail.value = Event(networkProduct)
    }
}