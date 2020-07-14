package com.example.productscanner.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.productscanner.data.domain.DomainProduct
import com.example.productscanner.util.Event

class MainFragmentViewModel: ViewModel() {
    private val _navigationToDetail = MutableLiveData<Event<DomainProduct>>()
    val navigationToDetail: LiveData<Event<DomainProduct>> get() = _navigationToDetail

    fun displayNavigationToDetail(product: DomainProduct){
        _navigationToDetail.value = Event(product)
    }
}