package com.example.productscanner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.model.Product
import com.example.productscanner.util.Event

class MainFragmentViewModel: ViewModel() {
    private val _navigationToDetail = MutableLiveData<Event<Product>>()
    val navigationToDetail: LiveData<Event<Product>> get() = _navigationToDetail

    fun displayNavigationToDetail(product: Product){
        _navigationToDetail.value = Event(product)
    }
}